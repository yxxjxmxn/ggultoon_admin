package com.architecture.admin.services.curation;

import com.architecture.admin.libraries.ThumborLibrary;
import com.architecture.admin.libraries.excel.ExcelData;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dao.curation.CurationDao;
import com.architecture.admin.models.daosub.curation.CurationDaoSub;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.contents.ImageDto;
import com.architecture.admin.models.dto.curation.CurationContentDto;
import com.architecture.admin.models.dto.curation.CurationDto;
import com.architecture.admin.services.BaseService;
import com.architecture.admin.services.log.AdminActionLogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Transactional
public class CurationService extends BaseService {

    private final CurationDao curationDao;
    private final CurationDaoSub curationDaoSub;
    private final ExcelData excelData;
    private final ThumborLibrary thumborLibrary;
    private final AdminActionLogService adminActionLogService;// 관리자 action log 처리용

    /**************************************************************************************
     * 큐레이션 리스트
     **************************************************************************************/

    /**
     * 큐레이션 리스트 엑셀 다운로드
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public Map<String, Object> excelCuration(SearchDto searchDto) {

        // 검색어 공백제거
        if (searchDto.getSearchWord() != null && !searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // 내보낼 데이터 set
        List<CurationDto> curationList = null;

        // 큐레이션 개수 카운트
        int totalCnt = curationDaoSub.getCurationTotalCnt(searchDto);

        // 큐레이션이 있는 경우
        if (totalCnt > 0) {

            // 큐레이션 리스트 조회
            curationList = curationDaoSub.getCurationList(searchDto);

            // 문자변환
            convertCurationText(curationList);
        }
        // 엑셀 데이터 생성
        return excelData.createExcelData(curationList, CurationDto.class);
    }

    /**
     * 작품 큐레이션 리스트 조회
     * state = 1,2만 조회
     *
     * @param searchDto
     */
    @Transactional(readOnly = true)
    public JSONObject getCurationList(SearchDto searchDto) {

        // 검색어 공백제거
        if (searchDto.getSearchWord() != null && !searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // 내보낼 데이터 set
        JSONObject joData = new JSONObject();
        List<CurationDto> curationList = null;

        // 작품 큐레이션 개수 카운트
        int totalCnt = curationDaoSub.getCurationTotalCnt(searchDto);

        // 큐레이션이 있는 경우
        if (totalCnt > 0) {

            // 작품 큐레이션 리스트 조회
            curationList = curationDaoSub.getCurationList(searchDto);

            // 문자변환
            convertCurationText(curationList);

            // paging 담기
            joData.put("params", new JSONObject(searchDto));
        }

        // list 담기
        joData.put("list", curationList);

        return joData;
    }

    /**
     * 큐레이션 상세
     *
     * @param idx
     */
    @Transactional(readOnly = true)
    public JSONObject getViewCuration(Long idx) {

        // 큐레이션 idx 유효성 검사
        isCurationIdxValidate(idx);

        // 큐레이션 상세 조회
        CurationDto viewCuration = curationDaoSub.getViewCuration(idx);

        if (viewCuration != null) {

            // 문자변환
            convertCurationText(viewCuration);
        }

        // 내보낼 데이터 set
        JSONObject joData = new JSONObject(viewCuration);

        return joData;
    }

    /**
     * 큐레이션 등록
     *
     * @param curationDto
     */
    @Transactional
    public void insertCuration(CurationDto curationDto) {

        // 등록할 큐레이션 유효성 검사
        isCurationDataValidate(curationDto);

        // 등록일 set
        curationDto.setRegdate(dateLibrary.getDatetime());

        // 큐레이션 상세 정보 등록
        curationDao.insertCuration(curationDto);

        // 큐레이션 노출 영역 매핑 정보 set
        List<CurationDto> list = new ArrayList<>();
        String[] strArea = curationDto.getArea().split(",");
        for (String area : strArea) {
            // dto set
            CurationDto dto = CurationDto.builder()
                    .idx(curationDto.getInsertedIdx())
                    .areaIdx(Long.parseLong(area)) // String to Long
                    .state(1)
                    .regdate(curationDto.getRegdate())
                    .build();

            // list set
            list.add(dto);
        }
        // 큐레이션 매핑 노출 영역 등록
        int result = curationDao.insertCurationAreaList(list);

        // 등록 실패 시
        if (result < 1) {
            throw new CustomException(CustomError.CURATION_REGISTER_ERROR); // 큐레이션 등록을 실패하였습니다.
        }

        // 관리자 action log
        adminActionLogService.regist(curationDto, Thread.currentThread().getStackTrace());
    }

    /**
     * 큐레이션 삭제
     * 삭제 후 나머지 데이터 재정렬
     *
     * (1) 단일 건 삭제
     * (2) 복수 건 삭제
     * @param searchDto
     */
    @Transactional
    public void deleteCuration(SearchDto searchDto) throws JsonProcessingException {

        // Json String -> List 변환
        List<String> list = new ObjectMapper().readValue(searchDto.getList(), new TypeReference<>(){});
        List<Long> idxList = new ArrayList<>();
        for (String idx : list) {
            // String to Long
            idxList.add(Long.parseLong(idx));
        }

        // 큐레이션 idx 유효성 검사
        isCurationIdxValidate(idxList);

        // 큐레이션 작품 매핑 정보 삭제
        curationDao.deleteAllCurationContent(idxList);

        // 큐레이션 노출 영역 매핑 정보 삭제
        curationDao.deleteCurationAreaList(idxList);

        // 큐레이션 삭제
        curationDao.deleteCuration(idxList);

        // 큐레이션 리스트 조회
        List<CurationDto> curationList = curationDaoSub.getCurationList(searchDto);

        // 삭제된 큐레이션 idx일 경우 리스트에서 제거 --> 삭제 후 바로 조회했을 때의 데이터 오차를 맞추기 위한 작업
        for (Long idx : idxList) {
            curationList.removeIf(dto -> dto.getIdx().equals(idx));
        }

        // 삭제 후 남은 데이터 재정렬용 리스트 세팅(역순)
        List<HashMap<String, Long>> sortList = new ArrayList<>();
        if (curationList != null && curationList.size() > 0) {

            for (int index = 0; index < curationList.size(); index++) {
                HashMap<String, Long> map = new HashMap<>();
                map.put("idx", curationList.get(index).getIdx());
                map.put("sort", (long)curationList.size() - index);
                sortList.add(map);
            }

            // dto set
            CurationDto curationDto = new CurationDto();
            curationDto.setList(sortList);

            // 삭제하면서 변경된 큐레이션 순서 재정렬
            int result = curationDao.reorderCuration(curationDto);

            // 삭제 실패 시
            if (result < 1) {
                throw new CustomException(CustomError.CURATION_DELETE_ERROR); // 큐레이션 삭제를 실패하였습니다.
            }

            // 관리자 action log
            adminActionLogService.regist(idxList, Thread.currentThread().getStackTrace());
        }
    }

    /**
     * 큐레이션 수정
     *
     * @param curationDto
     */
    @Transactional
    public void updateCuration(CurationDto curationDto) {

        // 수정할 데이터 유효성 검사
        isCurationDataValidate(curationDto);

        // 큐레이션 상세 정보 수정
        curationDao.updateCuration(curationDto);

        // 기존에 매핑된 큐레이션 노출 영역 삭제
        List<Long> idxList = new ArrayList<>();
        idxList.add(curationDto.getIdx());
        curationDao.deleteCurationAreaList(idxList);

        // 수정된 노출 영역으로 다시 매핑
        List<CurationDto> list = new ArrayList<>();
        String[] strArea = curationDto.getArea().split(",");
        for (String area : strArea) {
            // dto set
            CurationDto dto = CurationDto.builder()
                    .idx(curationDto.getIdx())
                    .areaIdx(Long.parseLong(area)) // String to Long
                    .state(1)
                    .regdate(dateLibrary.getDatetime())
                    .build();

            // list set
            list.add(dto);
        }
        int result = curationDao.insertCurationAreaList(list);

        // 수정 실패 시
        if (result < 1) {
            throw new CustomException(CustomError.CURATION_MODIFY_ERROR); // 큐레이션 수정을 실패하였습니다.
        }

        // 관리자 action log
        adminActionLogService.regist(curationDto, Thread.currentThread().getStackTrace());
    }

    /**
     * 작품 큐레이션 재정렬
     *
     * @param searchDto
     */
    @Transactional
    public void reorderCuration(SearchDto searchDto) throws JsonProcessingException {

        // Json String -> List 변환
        List<HashMap<String, Long>> list = new ObjectMapper().readValue(searchDto.getList(), new TypeReference<>(){});

        // dto set
        CurationDto curationDto = new CurationDto();
        curationDto.setList(list);

        // 작품 큐레이션 재정렬
        int result = curationDao.reorderCuration(curationDto);

        // 재정렬 실패 시
        if (result < 1) {
            throw new CustomException(CustomError.CURATION_REORDER_ERROR); // 순서 변경을 실패하였습니다.
        }

        // 관리자 action log
        adminActionLogService.regist(curationDto, Thread.currentThread().getStackTrace());
    }

    /**************************************************************************************
     * 큐레이션 노출 영역 리스트
     **************************************************************************************/

    /**
     * 큐레이션 노출 영역 리스트 조회
     * @return
     */
    @Transactional(readOnly = true)
    public JSONObject getAreaList(SearchDto searchDto) {

        // 검색어 공백제거
        if (searchDto.getSearchWord() != null && !searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // 내보낼 데이터 set
        JSONObject joData = new JSONObject();
        List<CurationDto> areaList = null;

        // 큐레이션 노출 영역 개수 카운트
        int totalCount = curationDaoSub.getCurationAreaTotalCnt(searchDto);

        // 노출 영역이 있을 경우
        if (totalCount > 0) {

            // 큐레이션 노출 영역 목록 조회
            areaList = curationDaoSub.getCurationAreaList(searchDto);
            
            // 문자변환
            convertCurationText(areaList);
        }
        // list 담기
        joData.put("list", areaList);
        return joData;
    }

    /**
     * 큐레이션 노출 영역 상세
     *
     * @param idx
     */
    @Transactional(readOnly = true)
    public JSONObject getViewCurationArea(Long idx) {

        // 큐레이션 노출 영역 idx 유효성 검사
        isCurationAreaIdxValidate(idx);

        // 큐레이션 노출 영역 상세 조회
        CurationDto viewCurationArea = curationDaoSub.getViewCurationArea(idx);

        if (viewCurationArea != null) {

            // 문자변환
            convertCurationText(viewCurationArea);
        }

        // 내보낼 데이터 set
        JSONObject joData = new JSONObject(viewCurationArea);

        return joData;
    }

    /**
     * 큐레이션 노출 영역 등록
     *
     * @param curationDto
     */
    @Transactional
    public void insertCurationArea(CurationDto curationDto) {

        // 등록할 큐레이션 노출 영역 유효성 검사
        isAreaDataValidate(curationDto);

        // 큐레이션 노출 영역 등록
        curationDto.setRegdate(dateLibrary.getDatetime()); // 등록일 set
        int result = curationDao.insertCurationArea(curationDto);

        // 등록 실패 시
        if (result < 1) {
            throw new CustomException(CustomError.CURATION_AREA_REGISTER_ERROR); // 큐레이션 노출 영역 등록을 실패하였습니다.
        }

        // 관리자 action log
        adminActionLogService.regist(curationDto, Thread.currentThread().getStackTrace());
    }

    /**
     * 큐레이션 노출 영역 삭제
     *
     * @param idx
     */
    @Transactional
    public void deleteCurationArea(Long idx) {

        // 큐레이션 노출 영역 idx 유효성 검사
        isCurationAreaIdxValidate(idx);

        // 큐레이션 노출 영역 삭제
        int result = curationDao.deleteCurationArea(idx);

        // 삭제 실패 시
        if (result < 1) {
            throw new CustomException(CustomError.CURATION_AREA_DELETE_ERROR); // 큐레이션 노출 영역 삭제를 실패하였습니다.
        }

        // 관리자 action log
        adminActionLogService.regist(idx, Thread.currentThread().getStackTrace());
    }

    /**
     * 큐레이션 노출 영역 수정
     *
     * @param curationDto
     */
    @Transactional
    public void updateCurationArea(CurationDto curationDto) {

        // 수정할 큐레이션 노출 영역 유효성 검사
        isAreaDataValidate(curationDto);

        // 큐레이션 노출 영역 수정
        curationDto.setRegdate(dateLibrary.getDatetime()); // 수정일 set
        int result = curationDao.updateCurationArea(curationDto);

        // 수정 실패 시
        if (result < 1) {
            throw new CustomException(CustomError.CURATION_AREA_MODIFY_ERROR); // 큐레이션 노출 영역 수정을 실패하였습니다.
        }

        // 관리자 action log
        adminActionLogService.regist(curationDto, Thread.currentThread().getStackTrace());
    }

    /**************************************************************************************
     * 큐레이션 매핑 작품 리스트
     **************************************************************************************/

    /**
     * 큐레이션 매핑 작품 리스트 엑셀 다운로드
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public Map<String, Object> excelCurationContent(SearchDto searchDto) {

        // 검색어 공백제거
        if (searchDto.getSearchWord() != null && !searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // 내보낼 데이터 set
        List<CurationContentDto> contentList = null;

        // 큐레이션 매핑 작품 개수 카운트
        int totalCount = curationDaoSub.getCurationContentTotalCnt(searchDto);

        // 매핑 작품이 있을 경우
        if (totalCount > 0) {

            // 큐레이션 매핑 작품 리스트 조회
            contentList = curationDaoSub.getCurationContentList(searchDto);

            // 문자변환
            convertContentText(contentList);
        }
        // 엑셀 데이터 생성
        return excelData.createExcelData(contentList, CurationContentDto.class);
    }

    /**
     * 큐레이션 매핑 작품 리스트 조회
     * @return
     */
    @Transactional(readOnly = true)
    public JSONObject getContentList(SearchDto searchDto) {

        // 검색어 공백제거
        if (searchDto.getSearchWord() != null && !searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // 선택한 큐레이션 정보 조회
        CurationDto curationInfo = curationDaoSub.getViewCuration(searchDto.getIdx());

        // 내보낼 데이터 set
        JSONObject joData = new JSONObject(curationInfo);
        List<CurationContentDto> contentList = null;

        // 큐레이션 매핑 작품 개수 카운트
        int totalCount = curationDaoSub.getCurationContentTotalCnt(searchDto);

        // 매핑 작품이 있을 경우
        if (totalCount > 0) {

            // 큐레이션 매핑 작품 리스트 조회
            contentList = curationDaoSub.getCurationContentList(searchDto);

            // 문자변환
            convertContentText(contentList);
        }
        // list 담기
        joData.put("list", contentList);
        return joData;
    }

    /**
     * 큐레이션 매핑 작품 재정렬
     *
     * @param searchDto : 재정렬된 리스트의 인덱스와 순서 정보
     */
    @Transactional
    public void reorderCurationContent(SearchDto searchDto) throws JsonProcessingException {

        // Json String -> List 변환
        List<HashMap<String, Long>> list = new ObjectMapper().readValue(searchDto.getList(), new TypeReference<>(){});

        // dto set
        CurationContentDto curationContentDto = new CurationContentDto();
        curationContentDto.setList(list);

        // 큐레이션 매핑 작품 재정렬
        int result = curationDao.reorderCurationContent(curationContentDto);

        // 재정렬 실패 시
        if (result < 1) {
            throw new CustomException(CustomError.CURATION_REORDER_ERROR); // 순서 변경을 실패하였습니다.
        }
        // 관리자 action log
        adminActionLogService.regist(curationContentDto, Thread.currentThread().getStackTrace());
    }

    /**
     * 큐레이션 매핑 작품 상세
     *
     * @param dto
     */
    @Transactional(readOnly = true)
    public JSONObject getViewCurationContent(CurationContentDto dto) {

        // 큐레이션 idx 유효성 검사
        isCurationIdxValidate(dto.getCurationIdx());

        // 매핑 작품 idx 유효성 검사
        isContentIdxValidate(dto.getContentsIdx());

        // 큐레이션 매핑 작품 상세 조회
        CurationContentDto viewCurationContent = curationDaoSub.getViewCurationContent(dto);

        if (viewCurationContent != null) {

            // 문자변환
            convertContentText(viewCurationContent);

            // 이미지 full url 세팅
            setContentImgFullUrl(viewCurationContent);
        }
        // 내보낼 데이터 set
        JSONObject joData = new JSONObject(viewCurationContent);

        return joData;
    }

    /**
     * 큐레이션 매핑 작품 검색용 리스트 조회
     * @return
     */
    @Transactional(readOnly = true)
    public JSONObject getSearchContentList(SearchDto searchDto) {

        // 검색어 공백제거
        if (searchDto.getSearchWord() != null && !searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // 내보낼 데이터 set
        JSONObject joData = new JSONObject();
        List<CurationContentDto> contentList = null;

        // 큐레이션 매핑 작품 검색 결과 개수 카운트
        int totalCount = curationDaoSub.getSearchContentTotalCnt(searchDto);

        // 큐레이션 매핑 작품 검색 결과가 있을 경우
        if (totalCount > 0) {

            // 큐레이션 매핑 작품 검색용 리스트 조회
            contentList = curationDaoSub.getSearchContentList(searchDto);

            // 이미지 full url 세팅
            setContentImgFullUrl(contentList);

            // 문자변환
            convertContentText(contentList);
        }
        // list 담기
        joData.put("list", contentList);
        return joData;
    }

    /**
     * 큐레이션 매핑 작품 등록
     *
     * @param curationContentDto
     */
    @Transactional
    public void insertCurationContent(CurationContentDto curationContentDto) {

        // 큐레이션 idx 유효성 검사
        isCurationIdxValidate(curationContentDto.getCurationIdx());

        // 작품 idx 유효성 검사
        isContentIdxValidate(curationContentDto.getContentsIdx());

        // 이미 해당 큐레이션에 등록되어 있는 작품인지 체크
        isAlreadyRegistered(curationContentDto);

        // 등록일 set
        curationContentDto.setRegdate(dateLibrary.getDatetime());

        // 큐레이션 매핑 작품 등록
        int result = curationDao.insertCurationContent(curationContentDto);

        // 등록 실패 시
        if (result < 1) {
            throw new CustomException(CustomError.CURATION_CONTENT_REGISTER_ERROR); // 큐레이션 매핑 작품 등록을 실패했습니다.
        }

        // 관리자 action log
        adminActionLogService.regist(curationContentDto, Thread.currentThread().getStackTrace());
    }

    /**
     * 큐레이션 매핑 작품 수정
     *
     * @param curationContentDto
     */
    @Transactional
    public void updateCurationContent(CurationContentDto curationContentDto) {

        // 작품 idx 유효성 검사
        isContentIdxValidate(curationContentDto.getIdx());

        // 매핑 사용 상태 체크
        if (curationContentDto.getState() == null || curationContentDto.getState() < 0 || curationContentDto.getState() > 2) {
            throw new CustomException(CustomError.CURATION_CONTENT_STATE_EMPTY); // 큐레이션 매핑 작품 사용 상태를 선택해주세요.
        }

        // 수정일 set
        curationContentDto.setRegdate(dateLibrary.getDatetime());

        // 큐레이션 매핑 작품 수정
        int result = curationDao.updateCurationContent(curationContentDto);

        // 수정 실패 시
        if (result < 1) {
            throw new CustomException(CustomError.CURATION_CONTENT_MODIFY_ERROR); // 큐레이션 매핑 작품 수정을 실패하였습니다.
        }

        // 관리자 action log
        adminActionLogService.regist(curationContentDto, Thread.currentThread().getStackTrace());
    }

    /**
     * 큐레이션 매핑 작품 삭제
     * 삭제 후 나머지 데이터 재정렬
     *
     * @param searchDto
     */
    @Transactional
    public void deleteCurationContent(SearchDto searchDto) throws JsonProcessingException {

        // Json String -> List 변환
        List<String> list = new ObjectMapper().readValue(searchDto.getList(), new TypeReference<>(){});
        List<Long> idxList = new ArrayList<>();
        for (String idx : list) {
            // String to Long
            idxList.add(Long.parseLong(idx));
        }

        // 큐레이션 매핑 idx 유효성 검사
        isContentIdxValidate(idxList);

        // 큐레이션 작품 매핑 정보 삭제
        curationDao.deleteCurationContent(idxList);

        // 큐레이션에 매핑된 작품 리스트 조회
        List<CurationContentDto> contentList = curationDaoSub.getCurationContentList(searchDto);

        // 삭제된 작품의 매핑 idx일 경우 리스트에서 제거 --> 삭제 후 바로 조회했을 때의 데이터 오차를 맞추기 위한 작업
        for (Long idx : idxList) {
            contentList.removeIf(dto -> dto.getMappingIdx().equals(idx));
        }

        // 삭제 후 남은 데이터 재정렬용 리스트 세팅
        List<HashMap<String, Long>> sortList = new ArrayList<>();
        if (contentList != null && contentList.size() > 0) {

            for (int index = 0; index < contentList.size(); index++) {
                HashMap<String, Long> map = new HashMap<>();
                map.put("mappingIdx", contentList.get(index).getMappingIdx()); // 작품 매핑 idx
                map.put("mappingSort", (long)index + 1); // 작품 매핑 순서
                sortList.add(map);
            }

            // dto set
            CurationContentDto curationContentDto = new CurationContentDto();
            curationContentDto.setList(sortList);

            // 삭제하면서 변경된 매핑 작품 순서 재정렬
            int result = curationDao.reorderCurationContent(curationContentDto);

            // 삭제 실패 시
            if (result < 1) {
                throw new CustomException(CustomError.CURATION_CONTENT_DELETE_ERROR); // 큐레이션 매핑 작품 삭제를 실패하였습니다.
            }
            // 관리자 action log
            adminActionLogService.regist(idxList, Thread.currentThread().getStackTrace());
        }
    }

    /*************************************************************
     * 문자변환
     *************************************************************/

    /**
     * 큐레이션 + 큐레이션 노출 영역
     * 문자변환 list
     */
    private void convertCurationText(List<CurationDto> curationList) {
        for (CurationDto dto : curationList) {
            if (dto != null) {
                convertCurationText(dto);
            }
        }
    }

    /**
     * 큐레이션 + 큐레이션 노출 영역
     * 문자변환 dto
     */
    private void convertCurationText(CurationDto dto) {

        // 사용 여부
        if (dto.getState() != null) {

            // 미사용
            if (dto.getState() == 2) {
                dto.setStateText(super.langMessage("lang.curation.state.unuse"));
                dto.setStateBg("badge-danger");

                // 사용
            } else if (dto.getState() == 1) {
                dto.setStateText(super.langMessage("lang.curation.state.use"));
                dto.setStateBg("badge-success");
            }
        }

        // 예약 여부
        if (dto.getReservation() != null) {

            // 예약X
            if (dto.getReservation() == 0) {
                dto.setReservationText(super.langMessage("lang.curation.not.reserved"));
                dto.setReservationBg("badge-light");

                // 예약O
            } else if (dto.getReservation() == 1) {
                dto.setReservationText(super.langMessage("lang.curation.reserved"));
                dto.setReservationBg("badge-primary");
            }
        }
    }

    /**
     * 큐레이션 매핑 작품
     * 문자변환 list
     */
    private void convertContentText(List<CurationContentDto> contentList) {
        for (CurationContentDto dto : contentList) {
            if (dto != null) {
                convertContentText(dto);
            }
        }
    }

    /**
     * 큐레이션 매핑 작품
     * 문자변환 dto
     */
    private void convertContentText(CurationContentDto dto) {

        // 매핑 사용 상태
        if (dto.getMappingState() != null) {
            if (dto.getMappingState() == 1) { // 사용
                dto.setMappingStateText(super.langMessage("lang.curation.mapping.use"));
                dto.setMappingStateBg("badge-success");

            } else if (dto.getMappingState() == 2) { // 미사용
                dto.setMappingStateText(super.langMessage("lang.curation.mapping.unuse"));
                dto.setMappingStateBg("badge-danger");
            }
        }

        // 작품 상태
        if (dto.getState() != null) {
            if (dto.getState() == 1) { // 정상
                dto.setStateText(super.langMessage("lang.contents.state.normal"));
                dto.setStateBg("badge-success");

            } else if (dto.getState() == 0) { // 중지
                dto.setStateText(super.langMessage("lang.contents.state.unsold"));
                dto.setStateBg("badge-danger");

            } else if (dto.getState() == 2) { // 대기
                dto.setStateText(super.langMessage("lang.contents.state.wait"));
                dto.setStateBg("badge-danger");
            }
        }

        // 이용관
        if (dto.getPavilion() != null) {

            if (dto.getPavilion() == 0) { // 일반관
                dto.setPavilionText(super.langMessage("lang.curation.pavilion.all"));
                dto.setPavilionBg("badge-success");

            } else if (dto.getPavilion() == 1) { // 성인관
                dto.setPavilionText(super.langMessage("lang.curation.pavilion.adult"));
                dto.setPavilionBg("badge-danger");
            }
        }
        
        // 성인작 여부
        if (dto.getAdult() != null) {

            if (dto.getAdult() == 0) { // 전체관람가
                dto.setAdultText(super.langMessage("lang.curation.content.not.adult"));
                dto.setAdultBg("badge-primary");

            } else if (dto.getAdult() == 1) { // 성인
                dto.setAdultText(super.langMessage("lang.curation.content.adult"));
                dto.setAdultBg("badge-danger");
            }
        }
    }

    /*************************************************************
     * Validation
     *************************************************************/

    /**
     * 선택한 큐레이션 idx 유효성 검사
     * @param idxList
     */
    private void isCurationIdxValidate(List<Long> idxList) {

        for (Long idx : idxList) {
            isCurationIdxValidate(idx);
        }
    }

    /**
     * 선택한 큐레이션 idx 유효성 검사
     * @param idx
     */
    private void isCurationIdxValidate(Long idx) {

        // 큐레이션 idx가 없는 경우
        if (idx == null || idx < 1) {
            throw new CustomException(CustomError.CURATION_IDX_EMPTY); // 요청하신 큐레이션 상세 정보를 찾을 수 없습니다.
        }
    }

    /**
     * 선택한 큐레이션 노출 영역 idx 유효성 검사
     * @param idx
     */
    private void isCurationAreaIdxValidate(Long idx) {

        // 큐레이션 노출 영역 idx가 없는 경우
        if (idx == null || idx < 1) {
            throw new CustomException(CustomError.CURATION_AREA_IDX_EMPTY); // 요청하신 큐레이션 노출 영역 상세 정보를 찾을 수 없습니다.
        }
    }

    /**
     * 선택한 작품 idx 유효성 검사
     * @param idxList
     */
    private void isContentIdxValidate(List<Long> idxList) {

        for (Long idx : idxList) {
            isContentIdxValidate(idx);
        }
    }

    /**
     * 선택한 작품 idx 유효성 검사
     * @param idx
     */
    private void isContentIdxValidate(Long idx) {

        // 작품 idx가 없는 경우
        if (idx == null || idx < 1) {
            throw new CustomException(CustomError.CURATION_CONTENT_IDX_EMPTY); // 요청하신 큐레이션 매핑 작품 정보를 찾을 수 없습니다.
        }
    }

    /**
     * 큐레이션 등록 & 수정
     * 입력 데이터 유효성 검사
     *
     * @param curationDto
     */
    private void isCurationDataValidate(CurationDto curationDto) {

        // 사용 상태
        if (curationDto.getState() == null || curationDto.getState() < 0 || curationDto.getState() > 2) {
            throw new CustomException(CustomError.CURATION_STATE_EMPTY); // 큐레이션 사용 상태를 선택해주세요.
        }

        // 제목
        if (curationDto.getTitle() == null || curationDto.getTitle().isEmpty()) {
            throw new CustomException(CustomError.CURATION_TITLE_EMPTY); // 큐레이션 제목을 입력해주세요.
        }

        // 노출 영역
        String areaList = curationDto.getArea().replaceAll("[\\[\\]]", "").replaceAll("\"", "");
        curationDto.setArea(areaList);
        if (curationDto.getArea() == null || curationDto.getArea().isEmpty()) {
            throw new CustomException(CustomError.CURATION_AREA_EMPTY); // 큐레이션 노출 영역을 선택해주세요.
        }

        // 예약 여부
        if (curationDto.getReservation() == null || curationDto.getReservation() < 0 || curationDto.getReservation() > 2) {
            throw new CustomException(CustomError.CURATION_RESERVATION_EMPTY); // 큐레이션 예약 여부를 선택해주세요.
        }

        // 예약 기간
        if (curationDto.getReservation() == 1) {

            // 입력 받은 예약 기간이 없는 경우
            if (curationDto.getStartDate() == null || curationDto.getStartDate().isEmpty() || curationDto.getEndDate() == null || curationDto.getEndDate().isEmpty()) {
                throw new CustomException(CustomError.CURATION_RESERVATION_DATE_EMPTY); // 큐레이션 예약 기간을 선택해주세요.
                
                // 입력 받은 예약 기간이 있는 경우
            } else {
                // 입력받은 예약 기간에 시분초 추가
                curationDto.setStartDate(curationDto.getStartDate() + " 00:00:00");
                curationDto.setEndDate(curationDto.getEndDate() + " 23:59:59");
            }
        }
    }

    /**
     * 큐레이션 노출 영역 등록 & 수정
     * 입력 데이터 유효성 검사
     *
     * @param curationDto
     */
    private void isAreaDataValidate(CurationDto curationDto) {

        // 사용 상태
        if (curationDto.getState() == null || curationDto.getState() < 0 || curationDto.getState() > 2) {
            throw new CustomException(CustomError.CURATION_AREA_STATE_EMPTY); // 노출 영역 사용 상태를 선택해주세요.
        }

        // 코드
        if (curationDto.getCode() == null || curationDto.getCode().isEmpty()) {
            throw new CustomException(CustomError.CURATION_AREA_CODE_EMPTY); // 노출 영역 코드를 입력해주세요.
        }

        // 이름
        if (curationDto.getName() == null || curationDto.getName().isEmpty()) {
            throw new CustomException(CustomError.CURATION_AREA_NAME_EMPTY); // 노출 영역 이름을 입력해주세요.
        }
    }

    /*************************************************************
     * SUB
     *************************************************************/

    /**
     * 매핑 작품 검색 결과 리스트
     * 이미지 full Url 세팅
     *
     * @param contentList
     */
    private void setContentImgFullUrl(List<CurationContentDto> contentList) {

        for (CurationContentDto dto : contentList) {
            setContentImgFullUrl(dto);
        }
    }

    /**
     * 매핑 작품 검색 결과 리스트
     * 이미지 full Url 세팅
     *
     * @param dto
     */
    private void setContentImgFullUrl(CurationContentDto dto) {

        // 리사이징을 위한 이미지 리스트 생성
        List<ImageDto> imageList = new ArrayList<>();

        // dto set
        ImageDto imageDto = ImageDto.builder()
                .url(dto.getUrl())
                .width(dto.getWidth())
                .height(dto.getHeight())
                .type(dto.getType())
                .build();

        // list set
        imageList.add(imageDto);

        // thumbor url setting
        dto.setImageList(thumborLibrary.getImageCFUrl(imageList));
    }

    /**
     * 큐레이션 작품 매핑 유효성 검사
     * 추가하려는 작품이 이미 해당 큐레이션에 있는지 체크
     *
     * @param curationContentDto
     */
    private void isAlreadyRegistered(CurationContentDto curationContentDto) {

        // 추가하려는 작품이 해당 큐레이션에 있는지 개수 카운트
        int totalCnt = curationDaoSub.getContentTotalCnt(curationContentDto);

        // 이미 등록되어 있는 경우
        if (totalCnt > 0) {
            throw new CustomException(CustomError.CURATION_CONTENT_IDX_DUPLICATED); // 현재 큐레이션에 이미 등록된 작품입니다.
        }
    }
}
