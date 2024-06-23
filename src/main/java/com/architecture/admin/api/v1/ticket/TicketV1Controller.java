package com.architecture.admin.api.v1.ticket;

import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.libraries.excel.ExcelXlsxView;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.ticket.GroupDto;
import com.architecture.admin.models.dto.ticket.TicketDto;
import com.architecture.admin.services.ticket.TicketService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/tickets")
public class TicketV1Controller extends BaseController {

    private final TicketService ticketService;

    /**************************************************************************************
     * 이용권 지급 대상 그룹 리스트
     **************************************************************************************/

    /**
     * 이용권 지급 대상 그룹 리스트 조회
     *
     * @param searchDto
     */
    @GetMapping("/groups")
    public String getTicketGroupList(@ModelAttribute SearchDto searchDto){

        // 관리자 접근 권한
        super.adminAccessFail(61);

        // 그룹 리스트 조회
        JSONObject joData = ticketService.getTicketGroupList(searchDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.ticket.success.search"); // 검색을 완료하였습니다.

        // 조회 결과가 없는 경우
        if (joData.getJSONArray("list").isEmpty()) {
            sMessage = super.langMessage("lang.ticket.exception.search.fail"); // 검색 결과가 없습니다.
        }
        return displayJson(true, "1000", sMessage, joData);
    }

    /**
     * 이용권 지급 대상 그룹 상세
     * @param idx
     * @return
     */
    @GetMapping("/groups/{idx}")
    public String getViewTicketGroup(@PathVariable(name = "idx") Long idx){

        // 관리자 접근 권한
        super.adminAccessFail(61);

        // 그룹 상세 조회
        JSONObject joData = ticketService.getViewTicketGroup(idx);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.ticket.success.search"); // 검색을 완료하였습니다.

        // 조회 결과가 없는 경우
        if (joData.isEmpty()) {
            sMessage = super.langMessage("lang.ticket.exception.search.fail"); // 검색 결과가 없습니다.
        }
        return displayJson(true, "1000", sMessage, joData);
    }

    /**
     * 이용권 지급 대상 그룹 수정
     * @param idx
     * @return
     */
    @PutMapping("/groups/{idx}")
    public String updateTicketGroup(@PathVariable(name = "idx") Long idx,
                                    @ModelAttribute("TicketDto") GroupDto groupDto) {

        // 관리자 접근 권한
        super.adminAccessFail(61);

        // idx set
        groupDto.setIdx(idx);

        // 그룹 정보 수정
        ticketService.updateTicketGroup(groupDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.ticket.success.modify"); // 수정을 완료하였습니다.

        return displayJson(true, "1000", sMessage);
    }

    /**************************************************************************************
     * 이용권 지급 예정 및 진행 리스트
     **************************************************************************************/

    /**
     * 이용권 지급 예정 및 진행 리스트 조회
     *
     * @param searchDto
     */
    @GetMapping("/ready")
    public String getTicketReadyList(@ModelAttribute SearchDto searchDto){

        // 관리자 접근 권한
        super.adminAccessFail(62);

        // 이용권 지급 예정 및 진행 리스트 조회
        JSONObject joData = ticketService.getTicketReadyList(searchDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.ticket.success.search"); // 검색을 완료하였습니다.

        // 조회 결과가 없는 경우
        if (joData.getJSONArray("list").isEmpty()) {
            sMessage = super.langMessage("lang.ticket.exception.search.fail"); // 검색 결과가 없습니다.
        }
        return displayJson(true, "1000", sMessage, joData);
    }

    /**
     * 지급 예정 및 진행중인 이용권 상세
     * @param idx
     * @return
     */
    @GetMapping("/ready/{idx}")
    public String getViewTicketReady(@PathVariable(name = "idx") Long idx){

        // 관리자 접근 권한
        super.adminAccessFail(62);

        // 이용권 상세
        JSONObject joData = ticketService.getViewTicketReady(idx);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.ticket.success.search"); // 검색을 완료하였습니다.

        // 조회 결과가 없는 경우
        if (joData.isEmpty()) {
            sMessage = super.langMessage("lang.ticket.exception.search.fail"); // 검색 결과가 없습니다.
        }
        return displayJson(true, "1000", sMessage, joData);
    }

    /**
     * 이용권 등록
     * @param ticketDto
     * @return
     */
    @PostMapping("/ready")
    public String insertTicket(@ModelAttribute("ticketDto") TicketDto ticketDto) {

        // 관리자 접근 권한
        super.adminAccessFail(62);

        // 이용권 등록
        ticketService.insertTicket(ticketDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.ticket.success.register"); // 등록을 완료하였습니다.

        return displayJson(true, "1000", sMessage);
    }

    /**
     * 이용권 삭제
     *
     * (1) 단일 건 삭제
     * (2) 복수 건 삭제
     * @param searchDto
     * @return
     */
    @DeleteMapping("/ready")
    public String deleteTicket(@ModelAttribute("list") SearchDto searchDto) throws JsonProcessingException {

        // 관리자 접근 권한
        super.adminAccessFail(62);

        // 이용권 삭제
        ticketService.deleteTicket(searchDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.ticket.success.delete"); // 삭제를 완료하였습니다.

        return displayJson(true, "1000", sMessage);
    }

    /**
     * 이용권 수정
     * @param idx
     * @return
     */
    @PutMapping("/ready/{idx}")
    public String updateTicket(@PathVariable(name = "idx") Long idx,
                                 @ModelAttribute("IpDto") TicketDto ticketDto) {

        // 관리자 접근 권한
        super.adminAccessFail(62);

        // idx set
        ticketDto.setIdx(idx);

        // 이용권 수정
        ticketService.updateTicket(ticketDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.ticket.success.modify"); // 수정을 완료하였습니다.

        return displayJson(true, "1000", sMessage);
    }

    /**************************************************************************************
     * 이용권 지급 완료 리스트
     **************************************************************************************/

    /**
     * 이용권 지급 완료 리스트 조회
     *
     * @param searchDto
     */
    @GetMapping("/complete")
    public String getTicketCompleteList(@ModelAttribute SearchDto searchDto){

        // 관리자 접근 권한
        super.adminAccessFail(63);

        // 이용권 지급 완료 리스트 조회
        JSONObject joData = ticketService.getTicketCompleteList(searchDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.ticket.success.search"); // 검색을 완료하였습니다.

        // 조회 결과가 없는 경우
        if (joData.getJSONArray("list").isEmpty()) {
            sMessage = super.langMessage("lang.ticket.exception.search.fail"); // 검색 결과가 없습니다.
        }
        return displayJson(true, "1000", sMessage, joData);
    }

    /**
     * 지급 완료 이용권 상세
     * @param idx
     * @return
     */
    @GetMapping("/complete/{idx}")
    public String getViewTicketComplete(@PathVariable(name = "idx") Long idx){

        // 관리자 접근 권한
        super.adminAccessFail(63);

        // 지급 완료 이용권 상세
        JSONObject joData = ticketService.getViewTicketComplete(idx);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.ticket.success.search"); // 검색을 완료하였습니다.

        // 조회 결과가 없는 경우
        if (joData.isEmpty()) {
            sMessage = super.langMessage("lang.ticket.exception.search.fail"); // 검색 결과가 없습니다.
        }
        return displayJson(true, "1000", sMessage, joData);
    }

    /**
     * 이용권 지급 완료 리스트 엑셀 다운로드
     *
     * @param searchDto
     * @return
     */
    @GetMapping("/complete/excel")
    public ModelAndView ticketCompleteListExcelDownload(@ModelAttribute("param") SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(63);

        // 뷰에 담을 모델객체를 생성(엑셀 파일에 들어갈 데이터)
        Map<String, Object> mExcelData = ticketService.excelTicketComplete(searchDto);

        // AbstractXlsxView 가 동작
        return new ModelAndView(new ExcelXlsxView(), mExcelData);
    }

}
