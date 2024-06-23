package com.architecture.admin.api.v1.curation;

import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.libraries.excel.ExcelXlsxView;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.curation.CurationContentDto;
import com.architecture.admin.models.dto.curation.CurationDto;
import com.architecture.admin.services.curation.CurationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/curations")
public class CurationV1Controller extends BaseController {

    private final CurationService curationService;

    /**************************************************************************************
     * 큐레이션 리스트
     **************************************************************************************/

    /**
     * 큐레이션 리스트 엑셀 다운로드
     *
     * @param searchDto
     * @return
     */
    @GetMapping("/excel")
    public ModelAndView curationListExcelDownload(@ModelAttribute("param") SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(56);

        // 뷰에 담을 모델객체를 생성(엑셀 파일에 들어갈 데이터)
        Map<String, Object> mExcelData = curationService.excelCuration(searchDto);

        // AbstractXlsxView 가 동작
        return new ModelAndView(new ExcelXlsxView(), mExcelData);
    }

    /**
     * 큐레이션 리스트 조회
     * state = 1,2만 조회
     *
     * @param searchDto
     * @return
     */
    @GetMapping()
    public String curationList(@ModelAttribute("params") SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(56);

        // 큐레이션 리스트 조회
        JSONObject data = curationService.getCurationList(searchDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.curation.success.search"); // 검색을 완료하였습니다.

        // 큐레이션 리스트가 없는 경우
        if (data.getJSONArray("list").isEmpty()) {
            sMessage = super.langMessage("lang.curation.exception.search_fail"); // 검색 결과가 없습니다.
        }

        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 작품 큐레이션 재정렬
     * @param searchDto : 재정렬된 리스트의 인덱스와 순서 정보
     * @return
     */
    @PutMapping("/reorder")
    public String reorderCuration(@ModelAttribute("list") SearchDto searchDto) throws JsonProcessingException {

        // 관리자 접근 권한
        super.adminAccessFail(56);

        // 작품 큐레이션 재정렬
        curationService.reorderCuration(searchDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.curation.success.reorder"); // 순서 변경을 완료하였습니다.

        return displayJson(true, "1000", sMessage);
    }

    /**
     * 큐레이션 상세
     * @param idx
     * @return
     */
    @GetMapping("/{idx}")
    public String getViewCuration(@PathVariable(name = "idx") Long idx){

        // 관리자 접근 권한
        super.adminAccessFail(56);

        // 큐레이션 상세
        JSONObject joData = curationService.getViewCuration(idx);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.curation.success.search"); // 검색을 완료하였습니다.

        // 조회 결과가 없는 경우
        if (joData.isEmpty()) {
            sMessage = super.langMessage("lang.curation.exception.search.fail"); // 검색 결과가 없습니다.
        }
        return displayJson(true, "1000", sMessage, joData);
    }

    /**
     * 큐레이션 등록
     * @param curationDto
     * @return
     */
    @PostMapping()
    public String insertCuration(@ModelAttribute("CurationDto") CurationDto curationDto) {

        // 관리자 접근 권한
        super.adminAccessFail(56);

        // 큐레이션 등록
        curationService.insertCuration(curationDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.curation.success.register"); // 등록을 완료하였습니다.

        return displayJson(true, "1000", sMessage);
    }

    /**
     * 큐레이션 삭제
     *
     * (1) 단일 건 삭제
     * (2) 복수 건 삭제
     * @param searchDto
     * @return
     */
    @DeleteMapping()
    public String deleteCuration(@ModelAttribute("list") SearchDto searchDto) throws JsonProcessingException {

        // 관리자 접근 권한
        super.adminAccessFail(56);

        // 큐레이션 삭제
        curationService.deleteCuration(searchDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.curation.success.delete"); // 삭제를 완료하였습니다.

        return displayJson(true, "1000", sMessage);
    }

    /**
     * 큐레이션 수정
     * @param idx
     * @return
     */
    @PutMapping("/{idx}")
    public String updateCuration(@PathVariable(name = "idx") Long idx,
                                 @ModelAttribute("CurationDto") CurationDto curationDto) {

        // 관리자 접근 권한
        super.adminAccessFail(56);

        // idx set
        curationDto.setIdx(idx);

        // 큐레이션 수정
        curationService.updateCuration(curationDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.curation.success.modify"); // 수정을 완료하였습니다.

        return displayJson(true, "1000", sMessage);
    }

    /**************************************************************************************
     * 큐레이션 노출 영역 리스트
     **************************************************************************************/

    /**
     * 큐레이션 노출 영역 리스트 조회
     * @return
     */
    @GetMapping("/areas")
    public String curationAreaList(@ModelAttribute("param") SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(57);

        // 노출 영역 리스트
        JSONObject data = curationService.getAreaList(searchDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.curation.success.search"); // 검색을 완료하였습니다.

        // 노출 영역 리스트가 없는 경우
        if (data.getJSONArray("list").isEmpty()) {
            sMessage = super.langMessage("lang.curation.exception.search_fail"); // 검색 결과가 없습니다.
        }
        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 큐레이션 노출 영역 상세
     * @param idx
     * @return
     */
    @GetMapping("/areas/{idx}")
    public String getViewCurationArea(@PathVariable(name = "idx") Long idx){

        // 관리자 접근 권한
        super.adminAccessFail(57);

        // 큐레이션 노출 영역 상세
        JSONObject joData = curationService.getViewCurationArea(idx);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.curation.success.search"); // 검색을 완료하였습니다.

        // 조회 결과가 없는 경우
        if (joData.isEmpty()) {
            sMessage = super.langMessage("lang.curation.exception.search.fail"); // 검색 결과가 없습니다.
        }
        return displayJson(true, "1000", sMessage, joData);
    }

    /**
     * 큐레이션 노출 영역 등록
     * @param curationDto
     * @return
     */
    @PostMapping("/areas")
    public String insertCurationArea(@ModelAttribute("CurationDto") CurationDto curationDto) {

        // 관리자 접근 권한
        super.adminAccessFail(57);

        // 큐레이션 노출 영역 등록
        curationService.insertCurationArea(curationDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.curation.success.register"); // 등록을 완료하였습니다.

        return displayJson(true, "1000", sMessage);
    }

    /**
     * 큐레이션 노출 영역 삭제
     * @param idx
     * @return
     */
    @DeleteMapping("/areas/{idx}")
    public String deleteCurationArea(@PathVariable(name = "idx") Long idx) {

        // 관리자 접근 권한
        super.adminAccessFail(57);

        // 큐레이션 노출 영역 삭제
        curationService.deleteCurationArea(idx);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.curation.success.delete"); // 삭제를 완료하였습니다.

        return displayJson(true, "1000", sMessage);
    }

    /**
     * 큐레이션 노출 영역 수정
     * @param idx
     * @return
     */
    @PutMapping("/areas/{idx}")
    public String updateCurationArea(@PathVariable(name = "idx") Long idx,
                                @ModelAttribute("CurationDto") CurationDto curationDto) {

        // 관리자 접근 권한
        super.adminAccessFail(57);

        // idx set
        curationDto.setIdx(idx);

        // 큐레이션 노출 영역 수정
        curationService.updateCurationArea(curationDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.curation.success.modify"); // 수정을 완료하였습니다.

        return displayJson(true, "1000", sMessage);
    }

    /**************************************************************************************
     * 큐레이션 매핑 작품 리스트
     **************************************************************************************/

    /**
     * 큐레이션 매핑 작품 리스트 엑셀 다운로드
     *
     * @param searchDto
     * @return
     */
    @GetMapping("/{idx}/contents/excel")
    public ModelAndView curationContentListExcelDownload(@ModelAttribute("param") SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(56);

        // 뷰에 담을 모델객체를 생성(엑셀 파일에 들어갈 데이터)
        Map<String, Object> mExcelData = curationService.excelCurationContent(searchDto);

        // AbstractXlsxView 가 동작
        return new ModelAndView(new ExcelXlsxView(), mExcelData);
    }

    /**
     * 큐레이션 매핑 작품 리스트 조회
     * @return
     */
    @GetMapping("/{idx}/contents")
    public String curationContentList(@PathVariable(name = "idx") Long idx,
                                      @ModelAttribute("param") SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(56);

        // 큐레이션 idx set
        searchDto.setIdx(idx);

        // 큐레이션 매핑 작품 리스트
        JSONObject data = curationService.getContentList(searchDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.curation.success.search"); // 검색을 완료하였습니다.

        // 큐레이션 매핑 작품 리스트가 없는 경우
        if (data.getJSONArray("list").isEmpty()) {
            sMessage = super.langMessage("lang.curation.exception.search_fail"); // 검색 결과가 없습니다.
        }
        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 큐레이션 매핑 작품 재정렬
     *
     * @param searchDto : 재정렬된 리스트의 인덱스와 순서 정보
     * @return
     */
    @PutMapping("/{idx}/contents/reorder")
    public String reorderCurationContent(@ModelAttribute("list") SearchDto searchDto) throws JsonProcessingException {

        // 관리자 접근 권한
        super.adminAccessFail(56);

        // 작품 큐레이션 매핑 작품 재정렬
        curationService.reorderCurationContent(searchDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.curation.success.reorder"); // 순서 변경을 완료하였습니다.

        return displayJson(true, "1000", sMessage);
    }

    /**
     * 큐레이션 매핑 작품 검색용 리스트
     * @return
     */
    @GetMapping("/contents")
    public String searchContentList(@ModelAttribute("param") SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(56);

        // 큐레이션 매핑 작품 검색용 리스트
        JSONObject data = curationService.getSearchContentList(searchDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.curation.success.search"); // 검색을 완료하였습니다.

        // 큐레이션 매핑 작품 검색용 리스트가 없는 경우
        if (data.getJSONArray("list").isEmpty()) {
            sMessage = super.langMessage("lang.curation.exception.search_fail"); // 검색 결과가 없습니다.
        }
        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 큐레이션 매핑 작품 상세
     * @param idx
     * @param contentsIdx
     * @return
     */
    @GetMapping("/{idx}/contents/{contentsIdx}")
    public String getViewCurationContent(@PathVariable(name = "idx") Long idx,
                                         @PathVariable(name = "contentsIdx") Long contentsIdx){

        // 관리자 접근 권한
        super.adminAccessFail(56);

        // dto set
        CurationContentDto dto = CurationContentDto.builder()
                .curationIdx(idx)
                .contentsIdx(contentsIdx)
                .build();

        // 큐레이션 매핑 작품 상세
        JSONObject joData = curationService.getViewCurationContent(dto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.curation.success.search"); // 검색을 완료하였습니다.

        // 조회 결과가 없는 경우
        if (joData.isEmpty()) {
            sMessage = super.langMessage("lang.curation.exception.search.fail"); // 검색 결과가 없습니다.
        }
        return displayJson(true, "1000", sMessage, joData);
    }

    /**
     * 큐레이션 매핑 작품 등록
     * @return
     */
    @PostMapping("/{idx}/contents")
    public String insertCurationContent(@PathVariable(name = "idx") Long idx,
                                        @ModelAttribute("CurationContentDto") CurationContentDto curationContentDto) {

        // 관리자 접근 권한
        super.adminAccessFail(56);

        // 큐레이션 idx set
        curationContentDto.setCurationIdx(idx);

        // 큐레이션 매핑 작품 등록
        curationService.insertCurationContent(curationContentDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.curation.success.register"); // 등록을 완료하였습니다.

        return displayJson(true, "1000", sMessage);
    }

    /**
     * 큐레이션 매핑 작품 수정
     * @param idx
     * @return
     */
    @PutMapping("/{idx}/contents")
    public String updateCurationContent(@PathVariable(name = "idx") Long idx,
                                        @ModelAttribute("CurationContentDto") CurationContentDto curationContentDto) {

        // 관리자 접근 권한
        super.adminAccessFail(56);

        // 큐레이션 매핑 작품 수정
        curationService.updateCurationContent(curationContentDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.curation.success.modify"); // 수정을 완료하였습니다.

        return displayJson(true, "1000", sMessage);
    }

    /**
     * 큐레이션 매핑 작품 삭제
     *
     * (1) 단일 건 삭제
     * (2) 복수 건 삭제
     *
     * @param searchDto
     * @return
     */
    @DeleteMapping("/{idx}/contents")
    public String deleteCurationContent(@PathVariable(name = "idx") Long idx,
                                        @ModelAttribute("list") SearchDto searchDto) throws JsonProcessingException {

        // 관리자 접근 권한
        super.adminAccessFail(56);

        // 큐레이션 idx set
        searchDto.setIdx(idx);

        // 큐레이션 매핑 작품 삭제
        curationService.deleteCurationContent(searchDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.curation.success.delete"); // 삭제를 완료하였습니다.

        return displayJson(true, "1000", sMessage);
    }
}

