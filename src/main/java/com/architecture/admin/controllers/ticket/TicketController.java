package com.architecture.admin.controllers.ticket;

import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.models.dto.SearchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/tickets")
public class TicketController extends BaseController {

    /**************************************************************************************
     * 이용권 지급 대상 그룹 리스트
     **************************************************************************************/

    /**
     * 이용권 지급 대상 그룹 리스트 조회
     * @param model
     * @param page
     * @param searchDto
     * @return
     */
    @GetMapping("/groups")
    public String getTicketGroupList(Model model,
                                     @RequestParam(required = false, defaultValue = "1") int page,
                                     @ModelAttribute SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(61);

        model.addAttribute("page", page);
        model.addAttribute("search", searchDto);

        hmImportFile.put("importJs", "ticket/group/list.js.html");

        return "ticket/group/list";
    }

    /**
     * 이용권 지급 대상 그룹 상세
     * @param idx
     * @return
     */
    @GetMapping("/groups/{idx}")
    public String getViewTicketGroup(@PathVariable(required = false) Long idx, Model model) {

        // 관리자 접근 권한
        super.adminAccessFail(61);

        model.addAttribute("idx", idx);

        hmImportFile.put("importJs", "ticket/group/view.js.html");

        return "ticket/group/view";
    }

    /**************************************************************************************
     * 이용권 지급 예정 및 진행 리스트
     **************************************************************************************/

    /**
     * 이용권 지급 예정 및 진행 리스트 조회
     * @param model
     * @param page
     * @param searchDto
     * @return
     */
    @GetMapping("/ready")
    public String getTicketReadyList(Model model,
                                     @RequestParam(required = false, defaultValue = "1") int page,
                                     @ModelAttribute SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(62);

        model.addAttribute("page", page);
        model.addAttribute("search", searchDto);

        hmImportFile.put("importJs", "ticket/ready/list.js.html");

        return "ticket/ready/list";
    }

    /**
     * 지급 예정 및 진행 중인 이용권 상세
     * @param idx
     * @return
     */
    @GetMapping("/ready/{idx}")
    public String getViewTicketReady(@PathVariable(required = false) Long idx, Model model) {

        // 관리자 접근 권한
        super.adminAccessFail(62);

        model.addAttribute("idx", idx);

        hmImportFile.put("importJs", "ticket/ready/view.js.html");

        return "ticket/ready/view";
    }

    /**
     * 이용권 등록
     * @return
     */
    @GetMapping("/ready/register")
    public String registerTicketReady() {

        // 관리자 접근 권한
        super.adminAccessFail(62);

        hmImportFile.put("importJs", "ticket/ready/register.js.html");

        return "ticket/ready/register";
    }

    /**************************************************************************************
     * 이용권 지급 완료 리스트
     **************************************************************************************/

    /**
     * 이용권 지급 완료 리스트 조회
     * @param model
     * @param page
     * @param searchDto
     * @return
     */
    @GetMapping("/complete")
    public String getTicketCompleteList(Model model,
                                     @RequestParam(required = false, defaultValue = "1") int page,
                                     @ModelAttribute SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(63);

        model.addAttribute("page", page);
        model.addAttribute("search", searchDto);

        hmImportFile.put("importJs", "ticket/complete/list.js.html");

        return "ticket/complete/list";
    }

    /**
     * 지급 완료 이용권 상세
     * @param idx
     * @return
     */
    @GetMapping("/complete/{idx}")
    public String getViewTicketComplete(@PathVariable(required = false) Long idx, Model model) {

        // 관리자 접근 권한
        super.adminAccessFail(63);

        model.addAttribute("idx", idx);

        hmImportFile.put("importJs", "ticket/complete/view.js.html");

        return "ticket/complete/view";
    }
}
