package com.architecture.admin.api.v1.report;

import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.libraries.excel.ExcelXlsxView;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.services.report.ReportService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/reports")
public class ReportV1Controller extends BaseController {

    private final ReportService reportService;

    /**************************************************************************************
     * 작품 신고 내역
     **************************************************************************************/

    /**
     * 작품 신고 내역 엑셀 다운로드
     *
     * @param searchDto
     * @return
     */
    @GetMapping("/contents/excel")
    public ModelAndView reportContentListExcelDownload(@ModelAttribute("param") SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(29);

        // 뷰에 담을 모델객체를 생성(엑셀 파일에 들어갈 데이터)
        Map<String, Object> mExcelData = reportService.excelReportContent(searchDto);

        // AbstractXlsxView 가 동작
        return new ModelAndView(new ExcelXlsxView(), mExcelData);
    }

    /**
     * 작품 신고 내역 조회
     * @param searchDto
     * @return
     */
    @GetMapping("/contents")
    public String reportContentList(@ModelAttribute SearchDto searchDto){

        // 관리자 접근 권한
        super.adminAccessFail(29);

        // 작품 신고 내역 조회
        JSONObject joData = reportService.getReportContentList(searchDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.report.success.search"); // 검색을 완료하였습니다.

        // 조회 결과가 없는 경우
        if (joData.getJSONArray("list").isEmpty()) {
            sMessage = super.langMessage("lang.report.exception.search_fail"); // 검색 결과가 없습니다.
        }

        return displayJson(true, "1000", sMessage, joData);
    }

    /**
     * 작품 신고 내역 삭제
     * @param idx
     * @return
     */
    @DeleteMapping("/contents/{idx}")
    public String deleteReportContent(@PathVariable(name = "idx") Integer idx) {

        // 관리자 접근 권한
        super.adminAccessFail(29);

        // 작품 신고 내역 삭제
        reportService.deleteReportContent(idx);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.report.success.delete"); // 삭제를 완료하였습니다.

        return displayJson(true, "1000", sMessage);
    }

    /**************************************************************************************
     * 작품 댓글 신고 내역
     **************************************************************************************/

    /**
     * 작품 댓글 신고 내역 엑셀 다운로드
     *
     * @param searchDto
     * @return
     */
    @GetMapping("/contents/comments/excel")
    public ModelAndView reportContentCommentListExcelDownload(@ModelAttribute("param") SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(30);

        // 뷰에 담을 모델객체를 생성(엑셀 파일에 들어갈 데이터)
        Map<String, Object> mExcelData = reportService.excelReportContentComment(searchDto);

        // AbstractXlsxView 가 동작
        return new ModelAndView(new ExcelXlsxView(), mExcelData);
    }

    /**
     * 작품 신고 내역 조회
     * @param searchDto
     * @return
     */
    @GetMapping("/contents/comments")
    public String reportContentCommentList(@ModelAttribute SearchDto searchDto){

        // 관리자 접근 권한
        super.adminAccessFail(30);

        // 작품 댓글 신고 내역 조회
        JSONObject joData = reportService.getReportContentCommentList(searchDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.report.success.search"); // 검색을 완료하였습니다.

        // 조회 결과가 없는 경우
        if (joData.getJSONArray("list").isEmpty()) {
            sMessage = super.langMessage("lang.report.exception.search_fail"); // 검색 결과가 없습니다.
        }

        return displayJson(true, "1000", sMessage, joData);
    }

    /**
     * 작품 댓글 신고 내역 삭제
     * @param idx
     * @return
     */
    @DeleteMapping("/contents/comments/{idx}")
    public String deleteReportContentComment(@PathVariable(name = "idx") Integer idx) {

        // 관리자 접근 권한
        super.adminAccessFail(30);

        // 작품 댓글 신고 내역 삭제
        reportService.deleteReportContentComment(idx);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.report.success.delete"); // 삭제를 완료하였습니다.

        return displayJson(true, "1000", sMessage);
    }

    /**************************************************************************************
     * 회차 댓글 신고 내역
     **************************************************************************************/

    /**
     * 회차 댓글 신고 내역 엑셀 다운로드
     *
     * @param searchDto
     * @return
     */
    @GetMapping("/episodes/comments/excel")
    public ModelAndView reportEpisodeCommentListExcelDownload(@ModelAttribute("param") SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(31);

        // 뷰에 담을 모델객체를 생성(엑셀 파일에 들어갈 데이터)
        Map<String, Object> mExcelData = reportService.excelReportEpisodeComment(searchDto);

        // AbstractXlsxView 가 동작
        return new ModelAndView(new ExcelXlsxView(), mExcelData);
    }


    /**
     * 회차 신고 내역 조회
     * @param searchDto
     * @return
     */
    @GetMapping("/episodes/comments")
    public String reportEpisodeCommentList(@ModelAttribute SearchDto searchDto){

        // 관리자 접근 권한
        super.adminAccessFail(31);

        // 회차 댓글 신고 내역 조회
        JSONObject joData = reportService.getReportEpisodeCommentList(searchDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.report.success.search"); // 검색을 완료하였습니다.

        // 조회 결과가 없는 경우
        if (joData.getJSONArray("list").isEmpty()) {
            sMessage = super.langMessage("lang.report.exception.search_fail"); // 검색 결과가 없습니다.
        }

        return displayJson(true, "1000", sMessage, joData);
    }

    /**
     * 회차 댓글 신고 내역 삭제
     * @param idx
     * @return
     */
    @DeleteMapping("/episodes/comments/{idx}")
    public String deleteReportEpisodeComment(@PathVariable(name = "idx") Integer idx) {

        // 관리자 접근 권한
        super.adminAccessFail(31);

        // 회차 댓글 신고 내역 삭제
        reportService.deleteReportEpisodeComment(idx);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.report.success.delete"); // 삭제를 완료하였습니다.

        return displayJson(true, "1000", sMessage);
    }
}
