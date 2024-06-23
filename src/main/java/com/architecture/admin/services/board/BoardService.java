package com.architecture.admin.services.board;

import com.architecture.admin.libraries.PaginationLibray;
import com.architecture.admin.libraries.S3Library;
import com.architecture.admin.libraries.excel.ExcelData;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dao.board.BoardDao;
import com.architecture.admin.models.dao.notification.NotificationDao;
import com.architecture.admin.models.daosub.board.BoardDaoSub;
import com.architecture.admin.models.daosub.member.MemberDaoSub;
import com.architecture.admin.models.daosub.notification.NotificationDaoSub;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.board.NoticeDto;
import com.architecture.admin.models.dto.notification.NotificationDto;
import com.architecture.admin.services.BaseService;
import com.architecture.admin.services.log.AdminActionLogService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.architecture.admin.libraries.utils.NotificationUtils.NOTICE;

@RequiredArgsConstructor
@Service
@Transactional
public class BoardService extends BaseService {

    private final BoardDaoSub boardDaoSub;
    private final BoardDao boardDao;
    private final NotificationDao notificationDao;
    private final NotificationDaoSub notificationDaoSub;
    private final MemberDaoSub memberDaoSub;
    private final ExcelData excelData;
    private final S3Library s3Library;
    private final AdminActionLogService adminActionLogService;// 관리자 action log 처리용

    // 설정된 S3 tmpUpload 폴더
    @Value("${cloud.aws.s3.tmpFolder}")
    private String s3TmpUploadFolder;

    /**
     * 공지사항 리스트 엑셀 다운로드
     * @return
     */
    @Transactional(readOnly = true)
    public Map<String, Object> excelNotice(SearchDto searchDto) {

        // 검색어 공백제거
        if (!searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // 내보낼 데이터 set
        List<NoticeDto> noticeList = null;

        // 알림 개수 전체 카운트
        int totalCnt = boardDaoSub.getNoticeTotalCnt(searchDto);

        // 알림이 있는 경우
        if (totalCnt > 0) {

            // 알림 리스트 조회
            noticeList = boardDaoSub.getNoticeList(searchDto);

            // 알림 사용 상태값 문자변환
            noticeStateText(noticeList);
        }

        // 엑셀 데이터 생성
        return excelData.createExcelData(noticeList, NoticeDto.class);
    }

    /**
     * 공지사항 리스트 조회
     * @return
     */
    @Transactional(readOnly = true)
    public JSONObject getNoticeList(SearchDto searchDto) {

        // 검색어 공백제거
        if (!searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // 내보낼 데이터 set
        JSONObject joData = new JSONObject();
        List<NoticeDto> noticeList = null;

        // 공지사항 개수 전체 카운트
        int totalCnt = boardDaoSub.getNoticeTotalCnt(searchDto);
        
        // 공지사항이 있는 경우
        if (totalCnt > 0) {

            // recordSize set
            Integer recordSize = searchDto.getSearchCount();
            if (recordSize != null && recordSize > 0) {
                searchDto.setRecordSize(recordSize);
            }

            // paging
            PaginationLibray pagination = new PaginationLibray(totalCnt, searchDto);
            searchDto.setPagination(pagination);

            // 공지사항 리스트 조회
            noticeList = boardDaoSub.getNoticeList(searchDto);

            // 공지사항 사용 상태값 문자변환
            noticeStateText(noticeList);

            // paging 담기
            joData.put("params", new JSONObject(searchDto));

        }

        // list 담기
        joData.put("list", noticeList);

        return joData;
    }

    /**
     * 공지사항 상세
     * @return
     */
    @Transactional(readOnly = true)
    public JSONObject getViewNotice(Long idx) {

        // 공지사항 idx 유효성 검사
        isNoticeIdxValidate(idx);

        // 공지사항 상세 조회
        NoticeDto viewNotice = boardDaoSub.getViewNotice(idx);

        // 공지사항 상세 상태값 문자변환
        noticeStateText(viewNotice);

        // 내보낼 데이터 set
        JSONObject joData = new JSONObject(viewNotice);

        return joData;
    }

    /**
     * 공지사항 등록
     * @return
     */
    @Transactional
    public void registerNotice(NoticeDto noticeDto) {

        // 등록할 데이터 유효성 검사
        isInputNoticeDataValidate(noticeDto);

        // 공지사항 정보 등록(공지사항 내용 및 구분 타입값 제외 - insertedIdx 얻기 위해 따로 등록)
        noticeDto.setRegdate(dateLibrary.getDatetime()); // 등록일 set
        boardDao.registerNoticeInfo(noticeDto);
        noticeDto.setType(1); // 공지사항 구분 타입값 set(기본값)

        // 등록하면서 저장한 insertedIdx 조회
        Long insertedIdx = noticeDto.getInsertedIdx();

        // 임시 폴더
        String tmpPath = s3TmpUploadFolder;

        // S3에 저장될 path
        String s3Path = "notices/" + insertedIdx;

        // 이미지 S3 upload, detail src 변경
        Pattern imgSrcPattern = Pattern.compile("(<img[^>]*src\s*=\s*[\"']?([^>\"\']+)[\"']?[^>]*>)");
        Matcher matcher = imgSrcPattern.matcher(noticeDto.getContent());

        // 등록할 이미지가 있으면
        while (matcher.find()) {

            // img src 주소
            String imgSrc = matcher.group(2).trim();

            // 임시 폴더에 저장된 file name
            String fileName = s3Library.getUploadedFileName(imgSrc, tmpPath);

            // 파일 복사
            s3Library.copyFile(fileName, tmpPath, s3Path);

            // S3에 저장된 full url
            String s3FullUrl = s3Library.getUploadedFullUrl("/" + s3Path + "/" + fileName);

            // 이미지 경로 변경
            noticeDto.setContent(noticeDto.getContent().replace(imgSrc, s3FullUrl));

            // 공지사항 구분 타입값 set
            noticeDto.setType(2);
        }
        // 공지사항 내용 및 구분 타입값 등록
        int result = boardDao.registerNoticeContent(noticeDto);

        // 등록 실패 시
        if (result < 1) {
            throw new CustomException(CustomError.NOTICE_REGISTER_ERROR); // 공지사항 등록을 실패하였습니다.
        }

        // 관리자 action log
        adminActionLogService.regist(noticeDto, Thread.currentThread().getStackTrace());
    }

    /**
     * 공지사항 수정
     *
     * @param noticeDto
     */
    @Transactional
    public void modifyNotice(NoticeDto noticeDto) {

        /** 공지사항 등록 시 생성되었던 S3 폴더 삭제 : 수정하면서 새로 폴더 등록 -> 더 이상 불필요한 기존 폴더 제거 **/

        // 수정 전 공지사항 조회
        NoticeDto notice = boardDaoSub.getViewNotice(noticeDto.getIdx());

        // 수정 전 공지사항에 이미지가 포함되어 있는지 조회
        Pattern pattern = Pattern.compile("(<img[^>]*src\s*=\s*[\"']?([^>\"\']+)[\"']?[^>]*>)");
        Matcher matCher = pattern.matcher(notice.getContent());

        // 이미지가 포함되어 있다면
        while (matCher.find()) {

            // 수정 전 공지사항에 포함된 이미지 경로 조회
            String imgSrc = matCher.group(2).trim();

            // 공지사항 등록 시 생성되었던 S3 폴더 삭제
            s3Library.deleteFolder(imgSrc);
        }
        /**********************************************************************************************/

        // 수정할 데이터 유효성 검사
        isInputNoticeDataValidate(noticeDto);

        noticeDto.setType(1); // 공지사항 구분 타입값 set(기본값)
        noticeDto.setRegdate(dateLibrary.getDatetime()); // 수정일 set

        // 임시 폴더
        String tmpPath = s3TmpUploadFolder;

        // S3에 저장될 path
        String s3Path = "notices/" + noticeDto.getIdx();

        // 이미지 S3 upload, detail src 변경
        Pattern imgSrcPattern = Pattern.compile("(<img[^>]*src\s*=\s*[\"']?([^>\"\']+)[\"']?[^>]*>)");
        Matcher matcher = imgSrcPattern.matcher(noticeDto.getContent());

        // 등록할 이미지가 있을 경우
        while (matcher.find()) {

            // 공지사항 구분 타입값 set
            noticeDto.setType(2);

            // img src 주소
            String imgSrc = matcher.group(2).trim();

            // 임시 폴더에 저장된 이미지면 변환
            if (imgSrc.contains(tmpPath)) {

                // 임시 폴더에 저장된 file name
                String fileName = s3Library.getUploadedFileName(imgSrc, tmpPath);

                // 파일 복사
                s3Library.copyFile(fileName, tmpPath, s3Path);

                // S3에 저장된 full url
                String s3FullUrl = s3Library.getUploadedFullUrl("/" + s3Path + "/" + fileName);

                // 이미지 경로 변경
                noticeDto.setContent(noticeDto.getContent().replace(imgSrc, s3FullUrl));
            }
        }
        // 공지사항 수정
        int result = boardDao.modifyNotice(noticeDto);

        // 수정 실패 시
        if (result < 1) {
            throw new CustomException(CustomError.NOTICE_MODIFY_ERROR); // 공지사항 수정을 실패하였습니다.
        }

        // 관리자 action log
        adminActionLogService.regist(noticeDto, Thread.currentThread().getStackTrace());
    }

    /**
     * 공지사항 삭제
     *
     * @param idx
     */
    @Transactional
    public void deleteNotice(Long idx) {

        // 공지사항 idx 유효성 검사
        NoticeDto notice = isNoticeIdxValidate(idx);

        // S3에 업로드된 이미지 경로 조회
        Pattern imgSrcPattern = Pattern.compile("(<img[^>]*src\s*=\s*[\"']?([^>\"\']+)[\"']?[^>]*>)");
        Matcher matcher = imgSrcPattern.matcher(notice.getContent());

        // 삭제할 이미지가 있을 경우
        while (matcher.find()) {

            // img src 주소
            String imgSrc = matcher.group(2).trim();

            // 공지사항 등록 시 생성되었던 S3 폴더 삭제
            s3Library.deleteFolder(imgSrc);
        }
        // 공지사항 삭제
        int result = boardDao.deleteNotice(idx);

        // 삭제 실패 시
        if (result < 1) {
            throw new CustomException(CustomError.NOTICE_DELETE_ERROR); // 공지사항 삭제를 실패하였습니다.
        }

        // 관리자 action log
        adminActionLogService.regist(idx, Thread.currentThread().getStackTrace());
    }

    /**
     * 공지사항 알림 전송
     *
     * @param list (알림 전송할 공지사항 리스트)
     */
    @Transactional
    public void sendNoticeAlarm(List<Object> list) {

        // 알림 전송할 공지사항 idx 리스트 유효성 검사
        List<Long> idxList = isNoticeIdxValidate(list);

        // 알림 전송할 공지사항 리스트 조회 : regdate set
        List<NoticeDto> noticeList = boardDaoSub.getNoticeRegdateList(idxList);

        // 회원에게 전송된 공지사항 알림 idx 리스트 조회
        List<Long> noticeAlarmIdxList = notificationDaoSub.getNoticeAlarmIdxList();

        // 회원에게 이미 전송된 공지사항은 알림 보낼 리스트에서 제거
        if (noticeAlarmIdxList != null && !noticeAlarmIdxList.isEmpty()) {
            for (Long alarmIdx : noticeAlarmIdxList) {
                noticeList.removeIf(item -> item.getIdx().equals(alarmIdx));
            }
        }

        // 중복 제거 후에도 전송할 공지사항 알림이 남은 경우 -> 알림 전송
        if (noticeList != null && !noticeList.isEmpty()) {

            // 전체 회원 idx 리스트 조회
            List<Long> allMemberIdxList = memberDaoSub.getAllMemberIdxList();

            // 알림 보낼 리스트 set
            List<NotificationDto> sendNoticeList = new ArrayList<>();
            for (NoticeDto notice : noticeList) {
                for (Long memberIdx : allMemberIdxList) {

                    // dto set
                    NotificationDto dto = NotificationDto.builder()
                            .memberIdx(memberIdx) // 회원 idx
                            .category(NOTICE) // 알림 카테고리
                            .type("notice") // 알림 보낼 테이블명
                            .typeIdx(notice.getIdx()) // 알림 보낼 테이블 idx
                            .state(1)
                            .regdate(notice.getRegdate()) // 공지사항 등록일을 알림 전송일로 설정(정책)
                            .build();

                    // list add
                    sendNoticeList.add(dto);
                }
            }
            // 공지사항 알림 전송
            notificationDao.insertNoticeAlarm(sendNoticeList);

            // 관리자 action log
            adminActionLogService.regist(sendNoticeList, Thread.currentThread().getStackTrace());
        }
    }

    /*************************************************************
     * 이미지 관련
     *************************************************************/

    /**
     * s3 s3TmpUploadFolder(data/tmpUpload)에 임시 이미지 저장
     *
     * @param uploadFile
     * @return
     */
    public String tempImage(List<MultipartFile> uploadFile) {

        // s3 이미지 등록
        List<HashMap<String, Object>> response = s3Library.uploadFileNew(uploadFile, s3TmpUploadFolder);
        
        // 업로드된 이미지 url 조회
        return s3Library.getUploadedFullUrl(response.get(0).get("fileUrl").toString());
    }

    /*************************************************************
     * 문자변환
     *************************************************************/

    /**
     * 공지사항 리스트
     * 문자변환 list
     */
    private void noticeStateText(List<NoticeDto> list) {
        for (NoticeDto dto : list) {
            noticeStateText(dto);
        }
    }

    /**
     * 공지사항 리스트
     * 문자변환 dto
     */
    private void noticeStateText(NoticeDto dto)  {
        if (dto.getState() != null) {
            if (dto.getState() == 2) {
                // 미사용
                dto.setStateText(super.langMessage("lang.board.notice.state.unuse"));
                dto.setStateBg("badge-danger");
            } else if (dto.getState() == 1) {
                // 사용
                dto.setStateText(super.langMessage("lang.board.notice.state.use"));
                dto.setStateBg("badge-success");
            }
        }

        if (dto.getMustRead() != null) {
            if (dto.getMustRead() == 0) {
                // 일반
                dto.setMustReadText(super.langMessage("lang.board.notice.mustRead.normal"));
                dto.setMustReadBg("badge-light");
            } else if (dto.getMustRead() == 1) {
                // 필독
                dto.setMustReadText(super.langMessage("lang.board.notice.mustRead.must"));
                dto.setMustReadBg("badge-warning");
            }
        }

        if (dto.getType() != null) {
            if (dto.getType() == 1) {
                // TEXT
                dto.setTypeText(super.langMessage("lang.board.notice.type.text"));
            } else if (dto.getType() == 2) {
                // HTML
                dto.setTypeText(super.langMessage("lang.board.notice.type.html"));
            }
        }
    }

    /*************************************************************
     * Validation
     *************************************************************/

    /**
     * 선택한 공지사항 idx 유효성 검사
     *
     * @param idx
     */
    private NoticeDto isNoticeIdxValidate(Long idx) {

        // 공지사항 idx가 없는 경우
        if (idx == null || idx < 1) {
            throw new CustomException(CustomError.NOTICE_IDX_EMPTY); // 요청하신 공지사항 정보를 찾을 수 없습니다.
        }

        // 공지사항 idx가 유효하지 않은 경우
        NoticeDto notice = boardDaoSub.getViewNotice(idx);
        if (notice == null) {
            throw new CustomException(CustomError.NOTICE_IDX_ERROR); // 요청하신 공지사항 정보를 찾을 수 없습니다.
        }
        
        // 공지사항 정보 리턴
        return notice;
    }

    /**
     * 공지사항 등록 & 수정
     * 입력 데이터 유효성 검사
     *
     * @param noticeDto
     */
    private void isInputNoticeDataValidate(NoticeDto noticeDto) {

        // 공지사항 사용 상태
        if (noticeDto.getState() == null || noticeDto.getState() < 0 || noticeDto.getState() > 2) {
            throw new CustomException(CustomError.NOTICE_STATE_EMPTY); // 공지사항 사용 상태를 입력해주세요.
        }

        // 공지사항 필독 여부
        if (noticeDto.getMustRead() == null || noticeDto.getMustRead() < 0 || noticeDto.getMustRead() > 1) {
            throw new CustomException(CustomError.NOTICE_MUSTREAD_EMPTY); // 공지사항 필독 여부를 입력해주세요.
        }

        // 공지사항 제목
        if (noticeDto.getTitle() == null || noticeDto.getTitle().isEmpty()) {
            throw new CustomException(CustomError.NOTICE_TITLE_EMPTY); // 공지사항 제목을 입력해주세요.
        }

        // 공지사항 내용
        if (noticeDto.getContent() == null || noticeDto.getContent().isEmpty()) {
            throw new CustomException(CustomError.NOTICE_CONTENT_EMPTY); // 공지사항 내용을 입력해주세요.
        }
    }

    /**
     * 알림 전송할 공지사항 리스트의 idx 유효성 검사
     *
     * @param list
     */
    private List<Long> isNoticeIdxValidate(List<Object> list) {

        List<Long> idxList = new ArrayList<>();
        for (Object obj : list) {

            // 공지사항 idx set
            Long idx = Long.valueOf(String.valueOf(obj));

            // 공지사항 idx가 없는 경우
            if (idx == null || idx < 1) {
                throw new CustomException(CustomError.NOTICE_IDX_EMPTY); // 요청하신 공지사항 정보를 찾을 수 없습니다.
            }

            // 공지사항 idx가 있을 때만 list add
            idxList.add(idx);
        }
        return idxList;
    }
}
