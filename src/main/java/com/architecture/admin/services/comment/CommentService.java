package com.architecture.admin.services.comment;

import com.architecture.admin.libraries.DateLibrary;
import com.architecture.admin.libraries.PaginationLibray;
import com.architecture.admin.libraries.ThumborLibrary;
import com.architecture.admin.libraries.excel.ExcelData;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dao.comment.CommentDao;
import com.architecture.admin.models.daosub.comment.CommentDaoSub;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.comment.ContentCommentDto;
import com.architecture.admin.models.dto.comment.ContentReplyDto;
import com.architecture.admin.models.dto.comment.EpisodeCommentDto;
import com.architecture.admin.services.BaseService;
import com.architecture.admin.services.log.AdminActionLogService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Transactional
public class CommentService extends BaseService {

    private final CommentDaoSub commentDaoSub;
    private final CommentDao commentDao;
    private final ExcelData excelData;
    private final ThumborLibrary thumborLibrary;
    private final AdminActionLogService adminActionLogService;// 관리자 action log 처리용


    /**************************************************************************************
     * 작품 목록
     **************************************************************************************/

    /**
     * 작품 목록 조회
     *
     * @param searchDto
     */
    @Transactional(readOnly = true)
    public JSONObject getContentList(SearchDto searchDto) {

        // 검색어 공백제거
        if (!searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // 검색 날짜를 UTC 시간으로 변경
        DateLibrary date = new DateLibrary();
        if (searchDto.getSearchStartDate() != null && !searchDto.getSearchStartDate().equals("")
                && searchDto.getSearchEndDate() != null && !searchDto.getSearchEndDate().equals("")) {
            searchDto.setSearchStartDate(date.localTimeToUtc(searchDto.getSearchStartDate().toString().concat(" 00:00:00")));
            searchDto.setSearchEndDate(date.localTimeToUtc(searchDto.getSearchEndDate().toString().concat(" 23:59:59")));
        }

        // 내보낼 데이터 set
        JSONObject joData = new JSONObject();
        List<ContentCommentDto> contentList = null;

        // 작품 목록 개수 카운트
        int totalCnt = commentDaoSub.getTotalContentCount(searchDto);

        // 작품 목록이 있는 경우
        if (totalCnt > 0) {

            // recordSize set
            Integer recordSize = searchDto.getSearchCount();
            if (recordSize != null && recordSize > 0) {
                searchDto.setRecordSize(recordSize);
            }

            // paging
            PaginationLibray pagination = new PaginationLibray(totalCnt, searchDto);
            searchDto.setPagination(pagination);

            // 작품 목록 조회
            contentList = commentDaoSub.getContentList(searchDto);

            // 이미지 fullUrl
            for (ContentCommentDto dto : contentList) {
                dto.setImageList(thumborLibrary.getImageCFUrl(dto.getImageList()));
            }

            // 문자변환
            if (!contentList.isEmpty()) {
                convertContentTextList(contentList);
            }

            // paging 담기
            joData.put("params", new JSONObject(searchDto));
        }
        // list 담기
        joData.put("list", contentList);

        return joData;
    }

    /**************************************************************************************
     * 회차 목록
     **************************************************************************************/

    /**
     * 회차 목록 조회
     *
     * @param searchDto
     */
    @Transactional(readOnly = true)
    public JSONObject getEpisodeList(SearchDto searchDto) {

        // 내보낼 데이터 set
        JSONObject joData = new JSONObject();
        List<EpisodeCommentDto> episodeList = null;

        // 회차 목록 개수 카운트
        int totalCnt = commentDaoSub.getTotalEpisodeCount(searchDto);

        // 회차 목록이 있는 경우
        if (totalCnt > 0) {

            // paging
            PaginationLibray pagination = new PaginationLibray(totalCnt, searchDto);
            searchDto.setPagination(pagination);

            // 회차 목록 조회
            episodeList = commentDaoSub.getEpisodeList(searchDto);

            // 이미지 fullUrl
            for (EpisodeCommentDto dto : episodeList) {
                dto.setImageList(thumborLibrary.getImageCFUrl(dto.getImageList()));
            }

            // 문자변환
            if (!episodeList.isEmpty()) {
                convertEpisodeTextList(episodeList);
            }

            // paging 담기
            joData.put("params", new JSONObject(searchDto));
        }
        // list 담기
        joData.put("list", episodeList);

        return joData;
    }

    /**************************************************************************************
     * 작품 댓글
     **************************************************************************************/

    /**
     * 작품 댓글 목록 조회
     *
     * @param searchDto
     */
    @Transactional(readOnly = true)
    public JSONObject getContentComments(SearchDto searchDto) {

        // 검색어 공백제거
        if (!searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // 내보낼 데이터 set
        JSONObject joData = new JSONObject();
        List<ContentCommentDto> contentCommentList = null;

        // 작품 댓글 목록 개수 카운트
        int totalCnt = commentDaoSub.getContentCommentTotalCnt(searchDto);

        // 작품 댓글 목록이 있는 경우
        if (totalCnt > 0) {

            // recordSize set
            Integer recordSize = searchDto.getSearchCount();
            if (recordSize != null && recordSize > 0) {
                searchDto.setRecordSize(recordSize);
            }

            // paging
            PaginationLibray pagination = new PaginationLibray(totalCnt, searchDto);
            searchDto.setPagination(pagination);

            // 작품 댓글 목록 조회
            contentCommentList = commentDaoSub.getContentCommentList(searchDto);

            // 작품 댓글 목록 상태값 문자변환
            contentCommentStateText(contentCommentList);

            // paging 담기
            joData.put("params", new JSONObject(searchDto));
        }

        // list 담기
        joData.put("list", contentCommentList);

        return joData;
    }

    /**
     * 작품 댓글 목록 엑셀 다운로드
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public Map<String, Object> excelContentComments(SearchDto searchDto) {

        // 검색어 공백제거
        if (!searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // 내보낼 데이터 set
        List<ContentCommentDto> contentCommentList = null;

        // 작품 댓글 목록 개수 카운트
        int totalCnt = commentDaoSub.getContentCommentTotalCnt(searchDto);

        // 작품 댓글 목록이 있는 경우
        if (totalCnt > 0) {

            // 작품 댓글 목록 조회
            contentCommentList = commentDaoSub.getContentCommentList(searchDto);

            // 작품 댓글 목록 상태값 문자변환
            contentCommentStateText(contentCommentList);
        }
        // 엑셀 데이터 생성
        return excelData.createExcelData(contentCommentList, ContentCommentDto.class);
    }

    /**
     * 작품 댓글 숨김
     *
     * @param idx
     */
    @Transactional
    public int hideContentComments(Long idx) {

        // return value
        int view;

        // 작품 댓글 idx 유효성 검사
        isContentCommentIdxValidate(idx);

        // 선택한 작품 댓글 정보 조회
        ContentCommentDto comment = commentDaoSub.getContentComment(idx);

        if (comment.getView() == 1) { // 댓글이 노출 상태일 경우
            comment.setView(0); // 댓글 숨김 처리
            view = 0;

        } else { // 댓글이 이미 숨김 처리된 경우
            comment.setView(1); // 댓글 숨김 취소 처리
            view = 1;
        }

        // 작품 댓글 숨김 OR 숨김 취소
        int result = commentDao.hideContentComments(comment);

        // 숨김 OR 숨김 취소 실패 시
        if (result < 1) {
            if (comment.getView() == 1) { // 댓글이 노출 상태일 경우
                throw new CustomException(CustomError.COMMENT_HIDE_ERROR); // 댓글 숨김을 실패하였습니다.
            } else {
                throw new CustomException(CustomError.COMMENT_SHOW_ERROR); // 댓글 노출을 실패하였습니다.
            }
        }

        // 관리자 action log
        adminActionLogService.regist(idx, Thread.currentThread().getStackTrace());

        // 댓글 상태 정보 리턴
        return view;
    }

    /**
     * 작품 댓글 삭제
     *
     * @param idx
     */
    @Transactional
    public int deleteContentComments(Long idx) {

        // return value
        int state;

        // 작품 댓글 idx 유효성 검사
        isContentCommentIdxValidate(idx);

        // 선택한 작품 댓글 정보 조회
        ContentCommentDto comment = commentDaoSub.getContentComment(idx);

        if (comment.getState() == 1) { // 댓글이 정상 상태일 경우
            comment.setState(0); // 댓글 삭제 처리
            state = 0;

        } else { // 댓글이 이미 삭제 처리된 경우
            comment.setState(1); // 댓글 삭제 취소 처리
            state = 1;
        }

        // 작품 댓글 삭제 OR 삭제 취소 처리
        int result = commentDao.deleteContentComments(comment);

        // 삭제 OR 삭제 취소 실패 시
        if (result < 1) {
            if (comment.getState() == 1) { // 댓글이 정상 상태일 경우
                throw new CustomException(CustomError.COMMENT_DELETE_ERROR); // 댓글 삭제를 실패하였습니다.
            } else {
                throw new CustomException(CustomError.COMMENT_NORMAL_ERROR); // 댓글 복구를 실패하였습니다.
            }
        }

        // 관리자 action log
        adminActionLogService.regist(idx, Thread.currentThread().getStackTrace());

        // 댓글 상태 정보 리턴
        return state;
    }

    /**************************************************************************************
     * 작품 대댓글
     **************************************************************************************/

    /**
     * 작품 댓글의 대댓글 목록 조회
     *
     * @param searchDto
     */
    @Transactional(readOnly = true)
    public JSONObject getContentReplies(SearchDto searchDto) {

        // 내보낼 데이터 set
        JSONObject joData = new JSONObject();
        List<ContentReplyDto> contentReplyList = null;

        // 대댓글 목록 개수 카운트
        int totalCnt = commentDaoSub.getContentReplyTotalCnt(searchDto);

        // 대댓글 목록이 있는 경우
        if (totalCnt > 0) {

            // recordSize set
            Integer recordSize = searchDto.getSearchCount();
            if (recordSize != null && recordSize > 0) {
                searchDto.setRecordSize(recordSize);
            }

            // paging
            PaginationLibray pagination = new PaginationLibray(totalCnt, searchDto);
            searchDto.setPagination(pagination);

            // 대댓글 목록 조회
            contentReplyList = commentDaoSub.getContentReplyList(searchDto);

            // 대댓글 목록 상태값 문자변환
            contentReplyStateText(contentReplyList);

            // paging 담기
            joData.put("params", new JSONObject(searchDto));
        }

        // list 담기
        joData.put("list", contentReplyList);

        return joData;
    }

    /**
     * 작품 대댓글 목록 엑셀 다운로드
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public Map<String, Object> excelContentReplies(SearchDto searchDto) {

        // 검색어 공백제거
        if (!searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // 내보낼 데이터 set
        List<ContentReplyDto> contentReplyList = null;

        // 대댓글 목록 개수 카운트
        int totalCnt = commentDaoSub.getContentReplyTotalCnt(searchDto);

        // 대댓글 목록이 있는 경우
        if (totalCnt > 0) {

            // 대댓글 목록 조회
            contentReplyList = commentDaoSub.getContentReplyList(searchDto);

            // 대댓글 목록 상태값 문자변환
            contentReplyStateText(contentReplyList);
        }
        // 엑셀 데이터 생성
        return excelData.createExcelData(contentReplyList, ContentReplyDto.class);
    }

    /**
     * 작품 대댓글 숨김
     *
     * @param idx
     */
    @Transactional
    public int hideContentReplies(Long idx) {

        // return value
        int view;

        // 작품 대댓글 idx 유효성 검사
        isContentCommentIdxValidate(idx);

        // 선택한 작품 대댓글 정보 조회
        ContentCommentDto comment = commentDaoSub.getContentComment(idx);

        if (comment.getView() == 1) { // 대댓글이 노출 상태일 경우
            comment.setView(0); // 대댓글 숨김 처리
            view = 0;

        } else { // 대댓글이 이미 숨김 처리된 경우
            comment.setView(1); // 대댓글 숨김 취소 처리
            view = 1;
        }

        // 작품 대댓글 숨김 OR 숨김 취소
        int result = commentDao.hideContentReplies(comment);

        // 숨김 OR 숨김 취소 실패 시
        if (result < 1) {
            if (comment.getView() == 1) { // 대댓글이 노출 상태일 경우
                throw new CustomException(CustomError.COMMENT_HIDE_ERROR); // 댓글 숨김을 실패하였습니다.
            } else {
                throw new CustomException(CustomError.COMMENT_SHOW_ERROR); // 댓글 노출을 실패하였습니다.
            }
        }

        // 관리자 action log
        adminActionLogService.regist(idx, Thread.currentThread().getStackTrace());

        // 대댓글 상태 정보 리턴
        return view;
    }

    /**
     * 작품 대댓글 삭제
     *
     * @param idx
     */
    @Transactional
    public int deleteContentReplies(Long idx) {

        // return value
        int state;

        // 작품 대댓글 idx 유효성 검사
        isContentCommentIdxValidate(idx);

        // 선택한 작품 대댓글 정보 조회
        ContentCommentDto comment = commentDaoSub.getContentComment(idx);

        if (comment.getState() == 1) { // 대댓글이 정상 상태일 경우
            comment.setState(0); // 대댓글 삭제 처리
            state = 0;

        } else { // 대댓글이 이미 삭제 처리된 경우
            comment.setState(1); // 대댓글 삭제 취소 처리
            state = 1;
        }

        // 작품 대댓글 삭제 OR 삭제 취소 처리
        int result = commentDao.deleteContentReplies(comment);

        // 삭제 OR 삭제 취소 실패 시
        if (result < 1) {
            if (comment.getState() == 1) { // 대댓글이 정상 상태일 경우
                throw new CustomException(CustomError.COMMENT_DELETE_ERROR); // 댓글 삭제를 실패하였습니다.
            } else {
                throw new CustomException(CustomError.COMMENT_NORMAL_ERROR); // 댓글 복구를 실패하였습니다.
            }
        }

        // 관리자 action log
        adminActionLogService.regist(idx, Thread.currentThread().getStackTrace());

        // 대댓글 상태 정보 리턴
        return state;
    }

    /**************************************************************************************
     * 회차 댓글
     **************************************************************************************/

    /**
     * 회차 댓글 목록 조회
     *
     * @param searchDto
     */
    @Transactional(readOnly = true)
    public JSONObject getEpisodeComments(SearchDto searchDto) {

        // 검색어 공백제거
        if (!searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // 내보낼 데이터 set
        JSONObject joData = new JSONObject();
        List<EpisodeCommentDto> episodeCommentList = null;

        // 회차 댓글 목록 개수 카운트
        int totalCnt = commentDaoSub.getEpisodeCommentTotalCnt(searchDto);

        // 회차 댓글 목록이 있는 경우
        if (totalCnt > 0) {

            // recordSize set
            Integer recordSize = searchDto.getSearchCount();
            if (recordSize != null && recordSize > 0) {
                searchDto.setRecordSize(recordSize);
            }

            // paging
            PaginationLibray pagination = new PaginationLibray(totalCnt, searchDto);
            searchDto.setPagination(pagination);

            // 회차 댓글 목록 조회
            episodeCommentList = commentDaoSub.getEpisodeCommentList(searchDto);

            // 회차 댓글 목록 상태값 문자변환
            episodeCommentStateText(episodeCommentList);

            // paging 담기
            joData.put("params", new JSONObject(searchDto));
        }

        // list 담기
        joData.put("list", episodeCommentList);

        return joData;
    }

    /**
     * 회차 댓글 목록 엑셀 다운로드
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public Map<String, Object> excelEpisodeComments(SearchDto searchDto) {

        // 검색어 공백제거
        if (!searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // 내보낼 데이터 set
        List<EpisodeCommentDto> episodeCommentList = null;

        // 회차 댓글 목록 개수 카운트
        int totalCnt = commentDaoSub.getEpisodeCommentTotalCnt(searchDto);

        // 회차 댓글 목록이 있는 경우
        if (totalCnt > 0) {

            // 회차 댓글 목록 조회
            episodeCommentList = commentDaoSub.getEpisodeCommentList(searchDto);

            // 회차 댓글 목록 상태값 문자변환
            episodeCommentStateText(episodeCommentList);
        }
        // 엑셀 데이터 생성
        return excelData.createExcelData(episodeCommentList, EpisodeCommentDto.class);
    }

    /**
     * 회차 댓글 숨김
     *
     * @param idx
     */
    @Transactional
    public int hideEpisodeComments(Long idx) {

        // return value
        int view;

        // 회차 댓글 idx 유효성 검사
        isContentCommentIdxValidate(idx);

        // 선택한 회차 댓글 정보 조회
        EpisodeCommentDto comment = commentDaoSub.getEpisodeComment(idx);

        if (comment.getView() == 1) { // 댓글이 노출 상태일 경우
            comment.setView(0); // 댓글 숨김 처리
            view = 0;

        } else { // 댓글이 이미 숨김 처리된 경우
            comment.setView(1); // 댓글 숨김 취소 처리
            view = 1;
        }

        // 회차 댓글 숨김 OR 숨김 취소
        int result = commentDao.hideEpisodeComments(comment);

        // 숨김 OR 숨김 취소 실패 시
        if (result < 1) {
            if (comment.getView() == 1) { // 댓글이 노출 상태일 경우
                throw new CustomException(CustomError.COMMENT_HIDE_ERROR); // 댓글 숨김을 실패하였습니다.
            } else {
                throw new CustomException(CustomError.COMMENT_SHOW_ERROR); // 댓글 노출을 실패하였습니다.
            }
        }

        // 관리자 action log
        adminActionLogService.regist(idx, Thread.currentThread().getStackTrace());

        // 댓글 상태 정보 리턴
        return view;
    }

    /**
     * 회차 댓글 삭제
     *
     * @param idx
     */
    @Transactional
    public int deleteEpisodeComments(Long idx) {

        // return value
        int state;

        // 회차 댓글 idx 유효성 검사
        isContentCommentIdxValidate(idx);

        // 선택한 회차 댓글 정보 조회
        EpisodeCommentDto comment = commentDaoSub.getEpisodeComment(idx);

        if (comment.getState() == 1) { // 댓글이 정상 상태일 경우
            comment.setState(0); // 댓글 삭제 처리
            state = 0;

        } else { // 댓글이 이미 삭제 처리된 경우
            comment.setState(1); // 댓글 삭제 취소 처리
            state = 1;
        }

        // 회차 댓글 삭제 OR 삭제 취소 처리
        int result = commentDao.deleteEpisodeComments(comment);

        // 삭제 OR 삭제 취소 실패 시
        if (result < 1) {
            if (comment.getState() == 1) { // 댓글이 정상 상태일 경우
                throw new CustomException(CustomError.COMMENT_DELETE_ERROR); // 댓글 삭제를 실패하였습니다.
            } else {
                throw new CustomException(CustomError.COMMENT_NORMAL_ERROR); // 댓글 복구를 실패하였습니다.
            }
        }

        // 관리자 action log
        adminActionLogService.regist(idx, Thread.currentThread().getStackTrace());

        // 댓글 상태 정보 리턴
        return state;
    }

    /*************************************************************
     * 문자변환
     *************************************************************/

    /**
     * 작품 목록
     * 문자변환 list
     * @param list
     */
    public void convertContentTextList(List<ContentCommentDto> list) {

        for (ContentCommentDto dto : list) {
            convertText(dto);
        }
    }

    /**
     * 작품 목록
     * 문자변환 dto
     * @param dto
     */
    public void convertText(ContentCommentDto dto) {
        // 상태값
        if (dto.getState() == 1) {
            // 정상
            dto.setStateText(super.langMessage("lang.contents.state.normal"));
            dto.setStateBg("badge-success");
        } else if (dto.getState() == 0) {
            // 중지
            dto.setStateText(super.langMessage("lang.contents.state.unsold"));
            dto.setStateBg("badge-danger");
        } else if (dto.getState() == 2) {
            // 대기
            dto.setStateText(super.langMessage("lang.contents.state.wait"));
            dto.setStateBg("badge-danger");
        }

        // 시청 연령
        if (dto.getAdult() == 1) {
            dto.setAdultText(super.langMessage("lang.contents.age.adult"));
            dto.setAdultBg("badge-danger");
        } else if (dto.getAdult() == 0) {
            dto.setAdultText(super.langMessage("lang.contents.age.non_adult"));
            dto.setAdultBg("badge-success");
        }

        // 이용관 정보
        if (dto.getAdultPavilion() == 1) { // 성인관
            dto.setAdultPavilionText(super.langMessage("lang.contents.age.adult"));
            dto.setAdultPavilionBg("badge-danger");
        } else if (dto.getAdultPavilion() == 0) { // 일반관
            dto.setAdultPavilionText(super.langMessage("lang.contents.age.non_adult"));
            dto.setAdultPavilionBg("badge-success");
        }

        // 1:연재, 2:휴재, 3:완결
        if (dto.getProgress() != null) {
            if (dto.getProgress() == 1) {
                dto.setProgressText(super.langMessage("lang.contents.completed.serialized"));
            } else if (dto.getProgress() == 2) {
                dto.setProgressText(super.langMessage("lang.contents.completed.pause"));
            } else if (dto.getProgress() == 3) {
                dto.setProgressText(super.langMessage("lang.contents.completed.complete"));
            }
        }

        // 독점
        if (dto.getExclusive() != null && dto.getExclusive() == 1) {
            dto.setExclusiveText(super.langMessage("lang.contents.exclusive"));
        } else {
            dto.setExclusiveText("-");
        }
    }

    /**
     * 회차 목록
     * 문자변환 list
     * @param list
     */
    public void convertEpisodeTextList(List<EpisodeCommentDto> list) {

        for (EpisodeCommentDto dto : list) {
            convertText(dto);
        }
    }

    /**
     * 회차 목록
     * 문자변환 dto
     * @param dto
     */
    public void convertText(EpisodeCommentDto dto) {
        // 상태값
        if (dto.getState() == 1) {
            // 정상
            dto.setStateText(super.langMessage("lang.contents.state.normal"));
            dto.setStateBg("badge-success");
        } else if (dto.getState() == 0) {
            // 중지
            dto.setStateText(super.langMessage("lang.contents.state.unsold"));
            dto.setStateBg("badge-danger");
        } else if (dto.getState() == 2) {
            // 대기
            dto.setStateText(super.langMessage("lang.contents.state.wait"));
            dto.setStateBg("badge-danger");
        }
    }

    /**
     * 작품 댓글 목록
     * 문자변환 list
     */
    private void contentCommentStateText(List<ContentCommentDto> list) {
        for (ContentCommentDto dto : list) {
            contentCommentStateText(dto);
        }
    }

    /**
     * 작품 댓글 목록
     * 문자변환 dto
     */
    private void contentCommentStateText(ContentCommentDto dto)  {
        
        // 댓글 상태
        if (dto.getState() != null) {
            if (dto.getState() == 0) {
                // 삭제
                dto.setStateText(super.langMessage("lang.comment.state.delete"));
                dto.setStateBg("badge-danger");
            } else if (dto.getState() == 1) {
                // 정상
                dto.setStateText(super.langMessage("lang.comment.state.normal"));
                dto.setStateBg("badge-success");
            }
        }
        
        // 댓글 OR 대댓글 구분
        if (dto.getParentIdx() != null) {
            if (dto.getParentIdx() == 0) {
                // 댓글
                dto.setTypeText(super.langMessage("lang.comment.comment"));
                dto.setTypeBg("badge-warning");
            } else {
                // 대댓글
                dto.setTypeText(super.langMessage("lang.comment.reply"));
                dto.setTypeBg("badge-secondary");
            }
        }

        // 댓글 노출 상태
        if (dto.getView() != null) {
            if (dto.getView() == 0) {
                // 비노출
                dto.setViewText(super.langMessage("lang.comment.view.false"));
                dto.setViewBg("badge-danger");
            } else if (dto.getView() == 1) {
                // 노출
                dto.setViewText(super.langMessage("lang.comment.view.true"));
                dto.setViewBg("badge-success");
            }
        }
    }

    /**
     * 작품 대댓글 목록
     * 문자변환 list
     */
    private void contentReplyStateText(List<ContentReplyDto> list) {
        for (ContentReplyDto dto : list) {
            contentReplyStateText(dto);
        }
    }

    /**
     * 작품 대댓글 목록
     * 문자변환 dto
     */
    private void contentReplyStateText(ContentReplyDto dto)  {

        // 댓글 상태
        if (dto.getState() != null) {
            if (dto.getState() == 0) {
                // 삭제
                dto.setStateText(super.langMessage("lang.comment.state.delete"));
                dto.setStateBg("badge-danger");
            } else if (dto.getState() == 1) {
                // 정상
                dto.setStateText(super.langMessage("lang.comment.state.normal"));
                dto.setStateBg("badge-success");
            }
        }

        // 댓글 OR 대댓글 구분
        if (dto.getParentIdx() != null) {
            if (dto.getParentIdx() == 0) {
                // 댓글
                dto.setTypeText(super.langMessage("lang.comment.comment"));
                dto.setTypeBg("badge-warning");
            } else {
                // 대댓글
                dto.setTypeText(super.langMessage("lang.comment.reply"));
                dto.setTypeBg("badge-secondary");
            }
        }

        // 댓글 노출 상태
        if (dto.getView() != null) {
            if (dto.getView() == 0) {
                // 비노출
                dto.setViewText(super.langMessage("lang.comment.view.false"));
                dto.setViewBg("badge-danger");
            } else if (dto.getView() == 1) {
                // 노출
                dto.setViewText(super.langMessage("lang.comment.view.true"));
                dto.setViewBg("badge-success");
            }
        }
    }

    /**
     * 회차 댓글 목록
     * 문자변환 list
     */
    private void episodeCommentStateText(List<EpisodeCommentDto> list) {
        for (EpisodeCommentDto dto : list) {
            episodeCommentStateText(dto);
        }
    }

    /**
     * 회차 댓글 목록
     * 문자변환 dto
     */
    private void episodeCommentStateText(EpisodeCommentDto dto)  {

        // 댓글 상태
        if (dto.getState() != null) {
            if (dto.getState() == 0) {
                // 삭제
                dto.setStateText(super.langMessage("lang.comment.state.delete"));
                dto.setStateBg("badge-danger");
            } else if (dto.getState() == 1) {
                // 정상
                dto.setStateText(super.langMessage("lang.comment.state.normal"));
                dto.setStateBg("badge-success");
            }
        }

        // 댓글 OR 대댓글 구분
        if (dto.getParentIdx() != null) {
            if (dto.getParentIdx() == 0) {
                // 댓글
                dto.setTypeText(super.langMessage("lang.comment.comment"));
                dto.setTypeBg("badge-warning");
            } else {
                // 대댓글
                dto.setTypeText(super.langMessage("lang.comment.reply"));
                dto.setTypeBg("badge-secondary");
            }
        }

        // 댓글 노출 상태
        if (dto.getView() != null) {
            if (dto.getView() == 0) {
                // 비노출
                dto.setViewText(super.langMessage("lang.comment.view.false"));
                dto.setViewBg("badge-danger");
            } else if (dto.getView() == 1) {
                // 노출
                dto.setViewText(super.langMessage("lang.report.content.comment.view.true"));
                dto.setViewBg("badge-success");
            }
        }
    }

    /*************************************************************
     * validation
     *************************************************************/

    /**
     * 선택한 작품 댓글 idx 유효성 검사
     *
     * @param idx
     */
    private void isContentCommentIdxValidate(Long idx) {

        // 작품 댓글 idx가 없는 경우
        if (idx == null || idx < 1) {
            throw new CustomException(CustomError.COMMENT_IDX_EMPTY); // 요청하신 댓글을 찾을 수 없습니다.
        }

        // 작품 댓글 idx가 유효하지 않은 경우
        int totalCnt = commentDaoSub.getContentCommentIdxCnt(idx);
        if (totalCnt < 1) {
            throw new CustomException(CustomError.COMMENT_IDX_ERROR); // 요청하신 댓글을 찾을 수 없습니다.
        }
    }
}
