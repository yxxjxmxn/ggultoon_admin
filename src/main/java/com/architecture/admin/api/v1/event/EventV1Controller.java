package com.architecture.admin.api.v1.event;

import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.libraries.excel.ExcelXlsxView;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.services.event.EventService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/event")

public class EventV1Controller extends BaseController {

    private final EventService eventService;

    /**************************************************************************************
     * 2024 설연휴 이벤트 참여 내역 조회
     **************************************************************************************/

    /**
     * 2024 설연휴 이벤트 참여 내역 조회
     *
     * @param searchDto
     */
    @GetMapping("/new-year/view")
    public String getNewYearEventList(@ModelAttribute SearchDto searchDto){

        // 관리자 접근 권한
        super.adminAccessFail(65);

        // 리스트 조회
        JSONObject joData = eventService.getNewYearEventList(searchDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.event.success.search"); // 검색을 완료하였습니다.

        // 조회 결과가 없는 경우
        if (joData.getJSONArray("list").isEmpty()) {
            sMessage = super.langMessage("lang.event.exception.search.fail"); // 검색 결과가 없습니다.
        }
        return displayJson(true, "1000", sMessage, joData);
    }

    /**
     * 2024 설연휴 이벤트 참여 내역 엑셀 다운로드
     *
     * @param searchDto
     * @return
     */
    @GetMapping("/new-year/view/excel")
    public ModelAndView excelNewYearEvent(@ModelAttribute("param") SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(65);

        // 뷰에 담을 모델객체를 생성(엑셀 파일에 들어갈 데이터)
        Map<String, Object> mExcelData = eventService.excelNewYearEvent(searchDto);

        // AbstractXlsxView 가 동작
        return new ModelAndView(new ExcelXlsxView(), mExcelData);
    }

    /**************************************************************************************
     * 2024 설연휴 이벤트 통계
     **************************************************************************************/

    /**
     * 2024 설연휴 이벤트 통계
     *
     * @param searchDto
     */
    @GetMapping("/new-year/stat")
    public String getNewYearEventStat(@ModelAttribute SearchDto searchDto){

        // 관리자 접근 권한
        super.adminAccessFail(66);

        // 리스트 조회
        JSONObject joData = eventService.getNewYearEventStat(searchDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.event.success.search"); // 검색을 완료하였습니다.

        // 조회 결과가 없는 경우
        if (joData.getJSONArray("list").isEmpty()) {
            sMessage = super.langMessage("lang.event.exception.search.fail"); // 검색 결과가 없습니다.
        }
        return displayJson(true, "1000", sMessage, joData);
    }

    /**
     * 2024 설연휴 이벤트 통계 엑셀 다운로드
     *
     * @param searchDto
     * @return
     */
    @GetMapping("/new-year/stat/excel")
    public ModelAndView excelNewYearEventStat(@ModelAttribute("param") SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(66);

        // 뷰에 담을 모델객체를 생성(엑셀 파일에 들어갈 데이터)
        Map<String, Object> mExcelData = eventService.excelNewYearEventStat(searchDto);

        // AbstractXlsxView 가 동작
        return new ModelAndView(new ExcelXlsxView(), mExcelData);
    }

    /**
     * 프로모션 이벤트
     * @return
     */
    @GetMapping("/promotion")
    public String promotionEvent () {

        // 관리자 접근 권한
        super.adminAccessFail(66);

        Integer result = eventService.promotionEvent();

        // 수정 실패
        String sMessage = super.langMessage("lang.episode.exception.modify_fail");
        // 수정 성공
        if (result > 0) {
            sMessage = super.langMessage("lang.episode.success.modify");
        }

        // response object
        JSONObject data = new JSONObject(result);

        return displayJson(true, "1000", sMessage, data);
    }

}
