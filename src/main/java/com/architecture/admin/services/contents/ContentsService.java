package com.architecture.admin.services.contents;

import com.architecture.admin.libraries.DateLibrary;
import com.architecture.admin.libraries.PaginationLibray;
import com.architecture.admin.libraries.S3Library;
import com.architecture.admin.libraries.ThumborLibrary;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dao.contents.ContentsDao;
import com.architecture.admin.models.daosub.contents.ContentsDaoSub;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.contents.AuthorDto;
import com.architecture.admin.models.dto.contents.CodeDto;
import com.architecture.admin.models.dto.contents.ContentsDto;
import com.architecture.admin.services.BaseService;
import com.architecture.admin.services.log.AdminActionLogService;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

@RequiredArgsConstructor
@Service
@Transactional
public class ContentsService  extends BaseService {

    private final ContentsDao contentsDao;
    private final ContentsDaoSub contentsDaoSub;
    private final S3Library s3Library;
    private final ThumborLibrary thumborLibrary;
    private final AdminActionLogService adminActionLogService;// 관리자 action log 처리용

    /**
     * 컨텐츠 목록
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public List<ContentsDto> getList(SearchDto searchDto) {

        // 검색어 공백제거
        if (searchDto.getSearchWord() != null && !searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // 컨텐츠 목록 전체 count
        int totalCount = contentsDaoSub.getTotalCount(searchDto);

        // paging
        PaginationLibray pagination = new PaginationLibray(totalCount, searchDto);
        searchDto.setPagination(pagination);

        // 검색 날짜를 UTC 시간으로 변경
        DateLibrary date = new DateLibrary();
        if (searchDto.getSearchStartDate() != null && !searchDto.getSearchStartDate().equals("")
            && searchDto.getSearchEndDate() != null && !searchDto.getSearchEndDate().equals("")) {
            searchDto.setSearchStartDate(date.localTimeToUtc(searchDto.getSearchStartDate().toString().concat(" 00:00:00")));
            searchDto.setSearchEndDate(date.localTimeToUtc(searchDto.getSearchEndDate().toString().concat(" 23:59:59")));
        }

        // 컨텐츠 목록
        List<ContentsDto> list = contentsDaoSub.getList(searchDto);

        // 이미지 fullUrl
        for (ContentsDto contentsDto : list) {
            contentsDto.setImageList(thumborLibrary.getImageCFUrl(contentsDto.getImageList()));
        }

        // 문자변환
        if (!list.isEmpty()) {
            convertTextList(list);
        }
        return list;
    }

    /**
     * 컨텐츠 등록
     * @param contentsDto
     * @return
     */
    @Transactional
    public Integer register(ContentsDto contentsDto) {

        // 등록일
        contentsDto.setRegdate(dateLibrary.getDatetime());

        // 오픈날짜(UTC)
        if (!contentsDto.getPubdate().isEmpty()) {
            String pubdate = contentsDto.getPubdate() + " " + contentsDto.getPubTime() + ":00:00";
            contentsDto.setPubdate(dateLibrary.localTimeToUtc(pubdate));
        } else {
            contentsDto.setPubdate(dateLibrary.getDatetime());
        }


        // login_idx(main cp idx만 등록)
//        contentsDto.setCpMemberIdx(Integer.parseInt(getAdminInfo("idx").toString()));

        // TODO validate
        contentsValidate(contentsDto);

        /** 컨텐츠 등록 */
        contentsDao.register(contentsDto);
        Integer result = contentsDto.getInsertedId();

        /** 컨텐츠 추가 정보 등록 */
        contentsDao.registerInfo(contentsDto);

        /** 코드 등록 */
        if (contentsDto.getCode() != null && contentsDto.getCode() > 0) {
            CodeDto codeDto = new CodeDto();
            codeDto.setCodeIdx(contentsDto.getCode());
            codeDto.setContentsIdx(contentsDto.getInsertedId());
            codeDto.setRegdate(dateLibrary.getDatetime());
            contentsDao.registerCode(codeDto);
        }

        /** 연재 요일 등록*/
        if (contentsDto.getWeekly() != null && !contentsDto.getWeekly().isEmpty()) {
            List<Map<String, Object>> weeklyList = new ArrayList<>();
            String[] weekly = contentsDto.getWeekly().split(",");
            for(String day : weekly) {
                Map<String, Object> map = new HashMap<>();
                map.put("contentsIdx", contentsDto.getInsertedId());
                map.put("weeklyIdx", day);
                map.put("regdate", dateLibrary.getDatetime());

                weeklyList.add(map);
            }
            contentsDao.registerWeekly(weeklyList);
        }


        /** 태그 등록 */
        if (contentsDto.getTagArr() != null && !contentsDto.getTagArr().isEmpty()) {
            List<Map<String, Object>> tagList = new ArrayList<>();
            String[] tags = contentsDto.getTagArr().split(",");
            for(String tag : tags) {
                Map<String, Object> map = new HashMap<>();
                map.put("contentsIdx", contentsDto.getInsertedId());
                map.put("tagIdx", tag.trim());
                map.put("regdate", dateLibrary.getDatetime());

                tagList.add(map);
            }
            contentsDao.registerTag(tagList);
        }


        /** 컨텐츠 이벤트 무료 */
        if (!contentsDto.getEventStartDate().isEmpty() && !contentsDto.getEventEndDate().isEmpty()) {
            // 컨텐츠 이벤트 기간(UTC)
            String eventStartDate = contentsDto.getEventStartDate() + " 00:00:00";
            String eventEndDate = contentsDto.getEventEndDate() + " 00:00:00";
            contentsDto.setEventStartDate(dateLibrary.localTimeToUtc(eventStartDate));
            contentsDto.setEventEndDate(dateLibrary.localTimeToUtc(eventEndDate));
        }
        contentsDao.registerEventFree(contentsDto);


        /** 작가 등록 */
        AuthorDto authorDto = new AuthorDto();
        // 컨텐츠 idx
        authorDto.setContentsIdx(contentsDto.getInsertedId());
        authorDto.setRegdate(dateLibrary.getDatetime());

        // 글작가
        String[] writers = contentsDto.getWriter().split(",");
        for (String name : writers) {
            // type 1:글작가, 2: 그림작가
            authorDto.setType(1);

            // 작가 조회(작가 등록후 바로 조회 하는 경우가 있어서 main 에서 조회)
            authorDto.setIdx(contentsDao.getAuthor(name.trim()));

            // 없으면 작가 등록
            if (authorDto.getIdx() == null) {
                authorDto.setName(name.trim());

                // 작가 등록
                contentsDao.registerAuthor(authorDto);
            }

            // 컨텐츠 글작가 등록
            contentsDao.registerContentsAuthor(authorDto);
        }

        // 그림 작가
        String[] illustrators = contentsDto.getIllustrator().split(",");
        for (String name : illustrators) {
            // type 1:글작가, 2: 그림작가
            authorDto.setType(2);

            // 작가 조회(작가 등록후 바로 조회 하는 경우가 있어서 main 에서 조회)
            authorDto.setIdx(contentsDao.getAuthor(name.trim()));

            // 작가 없으면 등록
            if (authorDto.getIdx() == null) {
                authorDto.setName(name.trim());

                // 작가(author) 등록
                contentsDao.registerAuthor(authorDto);
            }

            // 컨텐츠 그림작가(author_mapping) 등록
            contentsDao.registerContentsAuthor(authorDto);
        }

        /** 컨텐츠 CP 등록 */
        contentsDao.registerCp(contentsDto);

        /** 컨텐츠 이미지 등록 **/
        // s3에 저장될 path
        String s3Path = "kr/contents/"+ contentsDto.getInsertedId();

        /** 대표 이미지 원본 */
        String device = "origin";
        String type = "height";

        // webp 이미지 삭제
        contentsDto.getFileDataHeight().removeIf(multipartFiles -> multipartFiles.getContentType().contains("webp"));
        contentsDto.getFileDataHeight().removeIf(multipartFiles -> multipartFiles.isEmpty());
        List<MultipartFile> heightImage = contentsDto.getFileDataHeight();

        // 파일 있는지 체크
        Boolean chkHeightImage = chkIsEmptyImage(heightImage);
        if (Boolean.TRUE.equals(chkHeightImage)) {
            // s3 upload(원본)
            List<HashMap<String, Object>> uploadResponse = s3Library.uploadFileNew(heightImage, s3Path);

            uploadResponse = imageSize(uploadResponse, 180);

            // db insert
            registerImage(uploadResponse, contentsDto.getInsertedId(), s3Path, device, type, new ArrayList<>());
        }


        /** 가로 이미지 원본 */
        device = "origin";
        type = "width";

        // webp 이미지 삭제
        contentsDto.getFileDataWidth().removeIf(multipartFiles -> multipartFiles.getContentType().contains("webp"));
        contentsDto.getFileDataWidth().removeIf(multipartFiles -> multipartFiles.isEmpty());
        List<MultipartFile> widthImage = contentsDto.getFileDataWidth();

        // 파일 있는지 체크
        Boolean chkWidthImage = chkIsEmptyImage(widthImage);
        if (Boolean.TRUE.equals(chkWidthImage)) {
            // s3 upload
            List<HashMap<String, Object>> uploadResponse = s3Library.uploadFileNew(widthImage, s3Path);

            uploadResponse = imageSize(uploadResponse, 720);

            // db insert
            registerImage(uploadResponse, contentsDto.getInsertedId(), s3Path, device, type, new ArrayList<>());
        }
        /** //컨텐츠 이미지 등록 **/

        return result;
    }

    /**
     * 이미지 등록
     * @param uploadResponse
     * @param idx
     * @param s3Path
     * @param device
     * @param type
     * @param parents
     * @return
     */
    @Transactional
    public List<Integer> registerImage(List<HashMap<String,Object>> uploadResponse, Integer idx, String s3Path, String device, String type, List<Integer> parents) {

        int i = 1;
        for (HashMap<String, Object> map : uploadResponse) {
            map.put("idx", idx);
            map.put("path", s3Path);
            map.put("sort", i);
            map.put("device", device);
            map.put("type", type);
            map.put("regdate", dateLibrary.getDatetime());
            if (!parents.isEmpty()) {
                map.put("parent", parents.get(i-1) );
            } else {
                map.put("parent", 0);
            }

            // insertedId
            map.put("insertedId", 0);

            // 대량 insertedId
            map.put("keyId",0);

            i++;
        }

        contentsDao.registerImage(uploadResponse);

        // 원본 insert idx list
        List<Integer> parentList = new ArrayList<>();
        for(HashMap<String, Object> map : uploadResponse) {
            map.forEach((key, value) -> {
                if (key.equals("keyId")) {
                    parentList.add(Integer.parseInt(value.toString()));
                }
            });
        }

        return parentList;
    }

    /**
     * 컨텐츠 상세
     * @param idx
     * @return
     */
    @Transactional(readOnly = true)
    public ContentsDto getView(Integer idx) {
        if (idx == null || idx < 1) {
            throw new CustomException(CustomError.IDX_CONTENTS_ERROR);
        }

        // 컨텐츠 상세
        ContentsDto view = contentsDaoSub.getView(idx);

        if (view == null) {
            throw new CustomException(CustomError.NO_DATA_CONTENTS_ERROR);
        }

        // 이미지 fullUrl
        view.setImageList(thumborLibrary.getImageCFUrl(view.getImageList()));

        return view;
    }

    /**
     * 컨텐츠 수정
     * @param contentsDto
     * @return
     */
    @Transactional
    public Integer modifyContent(ContentsDto contentsDto) {
        // 결과
        Integer result = null;

        // 오픈날짜(UTC)
        String pubdate = contentsDto.getPubdate() + " " + contentsDto.getPubTime() + ":00:00";
        contentsDto.setPubdate(dateLibrary.localTimeToUtc(pubdate));

        // 개정판
        if (contentsDto.getRevision() == null) {
            contentsDto.setRevision(0);
        }

        /** 컨텐츠 modify */
        result = contentsDao.modify(contentsDto);

        /** 컨텐츠 추가 정보 modify */
        contentsDao.modifyInfo(contentsDto);

        /** 코드 등록 */
        contentsDao.deleteCode(contentsDto.getIdx());
        if (contentsDto.getCode() != null && contentsDto.getCode() > 0) {
            CodeDto codeDto = new CodeDto();
            codeDto.setCodeIdx(contentsDto.getCode());
            codeDto.setContentsIdx(contentsDto.getIdx());
            codeDto.setRegdate(dateLibrary.getDatetime());
            contentsDao.registerCode(codeDto);
        }


        /** 연재 요일 수정*/
        // 기존 요일정보 삭제
        contentsDao.deleteWeekly(contentsDto.getIdx());
        if (contentsDto.getWeekly() != null && !contentsDto.getWeekly().isEmpty()) {
            List<Map<String, Object>> weeklyList = new ArrayList<>();
            String[] weekly = contentsDto.getWeekly().split(",");

            for(String day : weekly) {
                Map<String, Object> map = new HashMap<>();
                map.put("contentsIdx", contentsDto.getIdx());
                map.put("weeklyIdx", day);
                map.put("regdate", dateLibrary.getDatetime());

                weeklyList.add(map);
            }
            // 새로 등록
            contentsDao.registerWeekly(weeklyList);
        }

        /** 태그 등록 */
        // 기존 태그 삭제
        contentsDao.deleteTag(contentsDto.getIdx());
        // 새로 등록
        if (contentsDto.getTagArr() != null && !contentsDto.getTagArr().isEmpty()) {
            List<Map<String, Object>> tagList = new ArrayList<>();
            String[] tags = contentsDto.getTagArr().split(",");
            for(String tag : tags) {
                Map<String, Object> map = new HashMap<>();
                map.put("contentsIdx", contentsDto.getIdx());
                map.put("tagIdx", tag.trim());
                map.put("regdate", dateLibrary.getDatetime());

                tagList.add(map);
            }
            contentsDao.registerTag(tagList);
        }

        /** 컨텐츠 이벤트 무료 */
        if (!contentsDto.getEventStartDate().isEmpty() && !contentsDto.getEventEndDate().isEmpty()) {
            // 컨텐츠 이벤트 기간(UTC)
            contentsDto.setEventStartDate(dateLibrary.localTimeToUtc(contentsDto.getEventStartDate() + " 00:00:00"));
            contentsDto.setEventEndDate(dateLibrary.localTimeToUtc(contentsDto.getEventEndDate() + " 00:00:00"));
        }
        contentsDao.modifyEventFree(contentsDto);

        /** 작가 수정 */
        AuthorDto authorDto = new AuthorDto();

        // 컨텐츠 idx
        authorDto.setContentsIdx(contentsDto.getIdx());
        authorDto.setRegdate(dateLibrary.getDatetime());

        Map<String, Integer> authorMap = new HashMap<>();
        authorMap.put("idx", contentsDto.getIdx());
        authorMap.put("type", 1);
        // 글작가 삭제
        contentsDao.deleteContentsAuthor(authorMap);

        // 글작가
        if (!contentsDto.getWriter().isEmpty()) {
            String[] writers = contentsDto.getWriter().split(",");
            for (String name : writers) {
                if (name != null && !name.equals("")) {
                    // type 1:글작가, 2: 그림작가
                    authorDto.setType(1);

                    // 작가 조회(작가 등록후 바로 조회 하는 경우가 있어서 main 에서 조회)
                    authorDto.setIdx(contentsDao.getAuthor(name.trim()));

                    // 없으면 작가 등록
                    if (authorDto.getIdx() == null) {
                        authorDto.setName(name.trim());

                        // 작가 등록
                        contentsDao.registerAuthor(authorDto);
                    }

                    // 컨텐츠 글작가 등록
                    contentsDao.registerContentsAuthor(authorDto);
                }
            }
        }
        // 그림 작가 삭제
        authorMap.put("type", 2);
        contentsDao.deleteContentsAuthor(authorMap);

        // 그림 작가
        if (!contentsDto.getIllustrator().isEmpty()) {
            String[] illustrators = contentsDto.getIllustrator().split(",");
            for (String name : illustrators) {
                if (name != null && !name.equals("")) {
                    // type 1:글작가, 2: 그림작가
                    authorDto.setType(2);

                    // 작가 조회(작가 등록후 바로 조회 하는 경우가 있어서 main 에서 조회)
                    authorDto.setIdx(contentsDao.getAuthor(name.trim()));

                    // 작가 없으면 등록
                    if (authorDto.getIdx() == null) {
                        authorDto.setName(name.trim());

                        // 작가(author) 등록
                        contentsDao.registerAuthor(authorDto);
                    }

                    // 컨텐츠 그림작가(author_mapping) 등록
                    contentsDao.registerContentsAuthor(authorDto);
                }
            }
        }

        /** 컨텐츠 CP 수정 */
        contentsDao.modifyCp(contentsDto);

        /** 컨텐츠 이미지 수정 **/
        // s3에 저장될 path
        String s3Path = "kr/contents/"+ contentsDto.getIdx();

        String device = "origin";
        String type = "height";

        /** 대표 이미지 원본 */
        // webp 이미지 삭제
        contentsDto.getFileDataHeight().removeIf(multipartFiles -> multipartFiles.getContentType().contains("webp"));
        contentsDto.getFileDataHeight().removeIf(multipartFiles -> multipartFiles.isEmpty());
        List<MultipartFile> heightImage = contentsDto.getFileDataHeight();

        // s3 upload(원본)
        List<HashMap<String, Object>> uploadResponse = s3Library.uploadFileNew(heightImage, s3Path);

        uploadResponse = imageSize(uploadResponse, 180);

        List<Integer> parentList = new ArrayList<>();

        // upload 가 있을 경우
        if (!uploadResponse.isEmpty()) {
            // db insert
            parentList = registerImage(uploadResponse, contentsDto.getIdx(), s3Path, device, type, new ArrayList<>());
        }

        // sort 재정렬
        List<Map<String, Object>> mapList = new ArrayList<>();

        Integer uploadIdx = 0;
        Integer sort = 1;

        JSONArray jsonArray = new JSONArray(uploadResponse);
        for (Object heightObj : jsonArray) {
            Map<String, Object> map = new HashMap<>();
            JSONObject jsonObject = (JSONObject) heightObj;
            String idx = jsonObject.get("idx").toString();

            if (idx.equals("null")) {
                map.put("idx", parentList.get(uploadIdx));
                uploadIdx++;
            } else {
                map.put("idx", Integer.parseInt(idx));
            }
            map.put("sort", sort);
            map.put("contentsIdx", contentsDto.getIdx());
            sort++;

            mapList.add(map);
        }

        if (!mapList.isEmpty()) {
            // sort 수정 - origin
            contentsDao.imageSort(mapList);
        }

        /** 가로 이미지 원본 */
        device = "origin";
        type = "width";

        // webp 이미지 삭제
        contentsDto.getFileDataWidth().removeIf(multipartFiles -> multipartFiles.getContentType().contains("webp"));
        contentsDto.getFileDataWidth().removeIf(multipartFiles -> multipartFiles.isEmpty());
        List<MultipartFile> widthImage = contentsDto.getFileDataWidth();

        // s3 upload
        uploadResponse = s3Library.uploadFileNew(widthImage, s3Path);

        uploadResponse = imageSize(uploadResponse, 720);

        parentList = new ArrayList<>();
        // upload 가 있을 경우
        if (!uploadResponse.isEmpty()) {
            // db insert
            parentList = registerImage(uploadResponse, contentsDto.getIdx(), s3Path, device, type, new ArrayList<>());
        }

        // sort 재정렬
        mapList = new ArrayList<>();

        uploadIdx = 0;
        sort = 1;

        jsonArray = new JSONArray(uploadResponse);
        for (Object widthObj : jsonArray) {
            Map<String, Object> map = new HashMap<>();
            JSONObject jsonObject = (JSONObject) widthObj;
            String idx = jsonObject.get("idx").toString();

            if (idx.equals("null")) {
                map.put("idx", parentList.get(uploadIdx));
                uploadIdx++;
            } else {
                map.put("idx", Integer.parseInt(idx));
            }
            map.put("sort", sort);
            map.put("contentsIdx", contentsDto.getIdx());
            sort++;

            mapList.add(map);
        }

        if (!mapList.isEmpty()) {
            // sort 수정 - origin
            contentsDao.imageSort(mapList);
        }

        // 관리자 action log
        adminActionLogService.regist(contentsDto, Thread.currentThread().getStackTrace());

        return result;
    }

    /**
     * 컨텐츠 이미지 삭제
     * @param idx
     * @return
     */
    @Transactional
    public Integer deleteContentImage(Integer idx) {

        // idx validation
        if (idx == null || idx < 1) {
            throw new CustomException(CustomError.IDX_CONTENTS_ERROR);
        }

        // 관리자 action log
        adminActionLogService.regist(idx, Thread.currentThread().getStackTrace());

        // origin 삭제
        return contentsDao.deleteImage(idx);
    }
    
    /**
     * 카테고리별 장르 목록
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public List<ContentsDto> getGenreList(SearchDto searchDto) {

        // return value
        List<ContentsDto> list = null;

        // 카테고리별 장르 개수 카운트
        int totalCount = contentsDaoSub.getTotalGenreCount(searchDto);

        // 카테고리별 장르가 있을 경우
        if (totalCount > 0) {
            
            // 카테고리별 장르 목록 조회
            list = contentsDaoSub.getGenreList(searchDto);
        }

        return list;
    }

    /**
     * 큐레이션 리스트 조회
     * @return
     */
    @Transactional(readOnly = true)
    public JSONObject getCurationList(SearchDto searchDto) {

        // 검색어 공백제거
        if (searchDto.getSearchWord() != null && !searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // 내보낼 데이터 set
        JSONObject joData = new JSONObject();
        List<ContentsDto> curationList = null;

        // 큐레이션 개수 전체 카운트
        int totalCnt = contentsDaoSub.getTotalCurationCnt(searchDto);

        // 큐레이션이 있는 경우
        if (totalCnt > 0) {

            // recordSize set
            Integer recordSize = searchDto.getSearchCount();
            if (recordSize != null && recordSize > 0) {
                searchDto.setRecordSize(recordSize);
            }

            // paging
            PaginationLibray pagination = new PaginationLibray(totalCnt, searchDto);
            searchDto.setPagination(pagination);

            // 큐레이션 리스트 조회
            curationList = contentsDaoSub.getCurationList(searchDto);

            // 큐레이션 리스트 문자변환
            convertTextList(curationList);

            // paging 담기
            joData.put("params", new JSONObject(searchDto));

        }

        // list 담기
        joData.put("list", curationList);

        return joData;
    }

    /*****************************************************
     *  SubFunction - Etc
     ****************************************************/

    /*
     * List<MultipartFile> image empty 체크
     * List 형식임으로 각 image 별로 isEmpty() 체크 처리
     * return Boolean
     */
    public Boolean chkIsEmptyImage(List<MultipartFile> uploadFileImgList) {
        boolean isEmptyValue = false;
        int isNotEmptyCnt = 0;
        if (!uploadFileImgList.isEmpty()) {
            for(MultipartFile image : uploadFileImgList){
                if(!image.isEmpty()) {
                    isNotEmptyCnt += 1;
                }
            }

            if (isNotEmptyCnt > 0) {
                isEmptyValue = true;
            }
        }

        return isEmptyValue;
    }

    /**
     * validation
     * @return
     */
    private Boolean contentsValidate(ContentsDto contentsDto) {

        // 카테고리 idx
        if (contentsDto.getCategoryIdx() == null) {
            throw new CustomException(CustomError.CONTENTS_CATEGORY_ERROR);
        }

        // 작품 제목
        if (contentsDto.getTitle() == null || contentsDto.getTitle().trim().equals("")) {
            throw new CustomException(CustomError.CONTENTS_TITLE_ERROR);
        }

        // 제휴사명
        if (contentsDto.getCompanyName() == null || contentsDto.getCompanyName().trim().equals("")) {
            throw new CustomException(CustomError.CONTENTS_COMPANY_ERROR);
        }

        return true;
    }

    /**
     * 문자변환 list
     * @param list
     */
    public void convertTextList(List<ContentsDto> list) {

        for (ContentsDto dto : list) {
            convertText(dto);
        }
    }

    /**
     * 문자변환 dto
     * @param dto
     */
    public void convertText(ContentsDto dto) {

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
            dto.setAdultPavilionText(super.langMessage("lang.contents.pavilion.adult"));
            dto.setAdultPavilionBg("badge-danger");
        } else if (dto.getAdultPavilion() == 0) { // 일반관
            dto.setAdultPavilionText(super.langMessage("lang.contents.pavilion.all"));
            dto.setAdultPavilionBg("badge-primary");
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
     * 리사이징 이미지 사이즈 구하기
     * @param uploadResponse
     * @param width
     * @return
     */
    public List<HashMap<String, Object>> imageSize(List<HashMap<String, Object>> uploadResponse, int width) {
        for(HashMap<String, Object> data : uploadResponse) {
            String url  = data.get("fileUrl").toString();
            String fullUrl = s3Library.getUploadedFullUrl(url);
            String suffix = url.substring(url.lastIndexOf('.')+1).toLowerCase();

            Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(suffix);
            if (iter.hasNext()) {
                ImageReader reader = iter.next();

                try {
                    InputStream is = new URL(fullUrl).openStream();
                    ImageInputStream stream = ImageIO.createImageInputStream(is);
                    reader.setInput(stream);

                    int widthOrg = reader.getWidth(reader.getMinIndex());
                    int heightOrg = reader.getHeight(reader.getMinIndex());
                    int height = (width * heightOrg / widthOrg);

                    data.put("width", width);
                    data.put("height", height);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    reader.dispose();
                }
            }
        }

        return uploadResponse;
    }
}
