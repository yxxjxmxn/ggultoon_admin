package com.architecture.admin.services.contents;

import com.architecture.admin.libraries.PaginationLibray;
import com.architecture.admin.libraries.excel.ExcelData;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dao.contents.TagDao;
import com.architecture.admin.models.daosub.contents.TagDaoSub;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.contents.TagDto;
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
public class TagService extends BaseService {

    private final TagDaoSub tagDaoSub;
    private final TagDao tagDao;
    private final ExcelData excelData;
    private final AdminActionLogService adminActionLogService;// 관리자 action log 처리용

    /**
     * 작품 등록용 태그 리스트 조회
     * 페이징 X
     * @return
     */
    @Transactional(readOnly = true)
    public JSONObject getRegisterTagList(SearchDto searchDto) {

        // 내보낼 데이터 set
        JSONObject joData = new JSONObject();
        List<TagDto> tagList = null;

        // 작품 등록용 태그 개수 전체 카운트
        int totalCnt = tagDaoSub.getRegisterTagTotalCnt(searchDto);

        // 작품 등록용 태그가 있는 경우
        if (totalCnt > 0) {

            // 작품 등록용 태그 리스트 조회
            tagList = tagDaoSub.getRegisterTagList(searchDto);
        }

        // list 담기
        joData.put("list", tagList);

        return joData;
    }


    /**
     * 태그 리스트 엑셀 다운로드
     * @return
     */
    @Transactional(readOnly = true)
    public Map<String, Object> excelTag(SearchDto searchDto) {

        // 검색어 공백제거
        if (!searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // 내보낼 데이터 set
        List<TagDto> tagList = null;

        // 태그 개수 전체 카운트
        int totalCnt = tagDaoSub.getTagTotalCnt(searchDto);

        // 태그가 있는 경우
        if (totalCnt > 0) {

            // 태그 리스트 조회
            tagList = tagDaoSub.getTagList(searchDto);

            // 태그 사용 상태값 문자변환
            tagStateText(tagList);

        }

        // 엑셀 데이터 생성
        return excelData.createExcelData(tagList, TagDto.class);
    }

    /**
     * 태그 리스트 조회
     * @return
     */
    @Transactional(readOnly = true)
    public JSONObject getTagList(SearchDto searchDto) {

        // 검색어 공백제거
        if (searchDto.getSearchWord() != null && !searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // 내보낼 데이터 set
        JSONObject joData = new JSONObject();
        List<TagDto> tagList = null;

        // 태그 개수 전체 카운트
        int totalCnt = tagDaoSub.getTagTotalCnt(searchDto);
        
        // 태그가 있는 경우
        if (totalCnt > 0) {

            // recordSize set
            Integer recordSize = searchDto.getSearchCount();
            if (recordSize != null && recordSize > 0) {
                searchDto.setRecordSize(recordSize);
            }

            // paging
            PaginationLibray pagination = new PaginationLibray(totalCnt, searchDto);
            searchDto.setPagination(pagination);

            // 태그 리스트 조회
            tagList = tagDaoSub.getTagList(searchDto);

            // 태그 사용 상태값 문자변환
            tagStateText(tagList);

            // paging 담기
            joData.put("params", new JSONObject(searchDto));

        }

        // list 담기
        joData.put("list", tagList);

        return joData;
    }

    /**
     * 작품 태그 상세
     * @return
     */
    @Transactional(readOnly = true)
    public JSONObject getViewTag(Integer idx) {

        // 작품 태그 idx 유효성 검사
        isTagIdxValidate(idx);

        // 작품 태그 상세 조회
        TagDto viewTag = tagDaoSub.getViewTag(idx);

        // 작품 태그 상세 상태값 문자변환
        tagStateText(viewTag);

        // 내보낼 데이터 set
        JSONObject joData = new JSONObject(viewTag);

        return joData;

    }

    /**
     * 작품 태그 등록
     * @return
     */
    @Transactional
    public void registerTag(TagDto tagDto) {

        // 등록할 데이터 유효성 검사
        isInputTagDataValidate(tagDto);

        // 결제 수단 등록
        tagDto.setRegdate(dateLibrary.getDatetime()); // 등록일 set
        int result = tagDao.registerTag(tagDto);

        // 등록 실패 시
        if (result < 1) {
            throw new CustomException(CustomError.CONTENTS_TAG_REGISTER_ERROR); // 작품 태그 등록을 실패하였습니다.
        }

        // 관리자 action log
        adminActionLogService.regist(tagDto, Thread.currentThread().getStackTrace());
    }

    /**
     * 작품 태그 수정
     *
     * @param tagDto
     */
    @Transactional
    public void modifyTag(TagDto tagDto) {

        // 수정할 데이터 유효성 검사
        isInputTagDataValidate(tagDto);

        // 작품 태그 수정
        tagDto.setRegdate(dateLibrary.getDatetime()); // 수정일 set
        int result = tagDao.modifyTag(tagDto);

        // 수정 실패 시
        if (result < 1) {
            throw new CustomException(CustomError.CONTENTS_TAG_MODIFY_ERROR); // 작품 태그 수정을 실패하였습니다.
        }

        // 관리자 action log
        adminActionLogService.regist(tagDto, Thread.currentThread().getStackTrace());
    }

    /**
     * 작품 태그 삭제
     *
     * @param idx
     */
    @Transactional
    public void deleteTag(Integer idx) {

        // 작품 태그 idx 유효성 검사
        isTagIdxValidate(idx);

        // 작품 태그 삭제
        int result = tagDao.deleteTag(idx);

        // 삭제 실패 시
        if (result < 1) {
            throw new CustomException(CustomError.CONTENTS_TAG_DELETE_ERROR); // 작품 태그 삭제를 실패하였습니다.
        }

        // 관리자 action log
        adminActionLogService.regist(idx, Thread.currentThread().getStackTrace());
    }

    /*************************************************************
     * 문자변환
     *************************************************************/

    /**
     * 작품 태그 리스트
     * 문자변환 list
     */
    private void tagStateText(List<TagDto> list) {
        for (TagDto dto : list) {
            tagStateText(dto);
        }
    }

    /**
     * 작품 태그 리스트
     * 문자변환 dto
     */
    private void tagStateText(TagDto dto)  {
        if (dto.getState() != null) {
            if (dto.getState() == 2) {
                // 미사용
                dto.setStateText(super.langMessage("lang.contents.tag.state.unuse"));
                dto.setStateBg("badge-danger");
            } else if (dto.getState() == 1) {
                // 사용
                dto.setStateText(super.langMessage("lang.contents.tag.state.use"));
                dto.setStateBg("badge-success");
            }
        }

    }

    /*************************************************************
     * Validation
     *************************************************************/

    /**
     * 선택한 작품 태그 idx 유효성 검사
     *
     * @param idx
     */
    private void isTagIdxValidate(Integer idx) {

        // 작품 태그 idx가 없는 경우
        if (idx == null || idx < 1) {
            throw new CustomException(CustomError.CONTENTS_TAG_IDX_EMPTY); // 요청하신 태그 정보를 찾을 수 없습니다.
        }

        // 작품 태그 idx가 유효하지 않은 경우
        int totalCnt = tagDaoSub.getTagIdxCnt(idx);
        if (totalCnt < 1) {
            throw new CustomException(CustomError.CONTENTS_TAG_IDX_ERROR); // 요청하신 태그 정보를 찾을 수 없습니다.
        }
    }

    /**
     * 작품 태그 등록 & 수정
     * 입력 데이터 유효성 검사
     *
     * @param tagDto
     */
    private void isInputTagDataValidate(TagDto tagDto) {

        // 작품 태그 사용 상태
        if (tagDto.getState() == null || tagDto.getState() < 0 || tagDto.getState() > 2) {
            throw new CustomException(CustomError.CONTENTS_TAG_STATE_EMPTY); // 태그 사용 상태를 입력해주세요.
        }

        // 태그 그룹 번호
        if (tagDto.getTagGroupIdx() == null || tagDto.getTagGroupIdx() < 0) {
            throw new CustomException(CustomError.CONTENTS_TAG_GROUP_EMPTY); // 태그 그룹 번호를 입력해주세요.
        }

        // 태그 이름
        if (tagDto.getName() == null || tagDto.getName().isEmpty()) {
            throw new CustomException(CustomError.CONTENTS_TAG_NAME_EMPTY); // 태그 이름을 입력해주세요.
        }
    }

}
