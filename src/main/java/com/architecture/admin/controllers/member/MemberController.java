package com.architecture.admin.controllers.member;

import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.models.dto.SearchDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/members")
public class MemberController extends BaseController {

    /**
     * 회원 목록 조회
     *
     * @param model
     * @param page
     * @param searchDto
     * @return
     */
    @GetMapping
    public String memberList(Model model,
                             @RequestParam(required = false, defaultValue = "1") int page,
                             @ModelAttribute SearchDto searchDto) {
        // 관리자 접근 권한
        super.adminAccessFail(4);

        model.addAttribute("page", page);
        model.addAttribute("search", searchDto);

        hmImportFile.put("importJs", "member/list.js.html");

        return "member/list";
    }

    /**
     * 회원 상세
     *
     * @param idx   : 회원 idx
     * @param model
     * @return
     */
    @GetMapping("/{idx}")
    public String memberDetail(@PathVariable Long idx, Model model) {
        // 관리자 접근 권한
        super.adminAccessFail(4);

        model.addAttribute("idx", idx);

        hmImportFile.put("importJs", "member/view.js.html");

        return "member/view";
    }

    /**
     * 회원 회차구매 내역
     *
     * @param model
     * @param page
     * @param searchDto
     * @return
     */
    @GetMapping("/purchases")
    public String memberPurchase(Model model,
                                 @RequestParam(required = false, defaultValue = "1") int page,
                                 @ModelAttribute SearchDto searchDto) {
        // 관리자 접근 권한
        super.adminAccessFail(4);

        model.addAttribute("page", page);
        model.addAttribute("search", searchDto);

        hmImportFile.put("importJs", "member/purchase/list.js.html");

        return "member/purchase/list";
    }

    /**
     * ott 회원 목록 조회
     *
     * @param model
     * @param page
     * @param searchDto
     * @return
     */
    @GetMapping("/ott")
    public String memberOttList(Model model,
                             @RequestParam(required = false, defaultValue = "1") int page,
                             @ModelAttribute SearchDto searchDto) {
        // 관리자 접근 권한
        super.adminAccessFail(40);

        model.addAttribute("page", page);
        model.addAttribute("search", searchDto);

        hmImportFile.put("importJs", "member/ott/list.js.html");

        return "member/ott/list";
    }

    /**
     * ott 회원 통계
     *
     * @param model
     * @param page
     * @param searchDto
     * @return
     */
    @GetMapping("/ott/stat")
    public String memberOttStat(Model model,
                                @RequestParam(required = false, defaultValue = "1") int page,
                                @ModelAttribute SearchDto searchDto) {
        // 관리자 접근 권한
        super.adminAccessFail(41);

        model.addAttribute("page", page);
        model.addAttribute("search", searchDto);

        hmImportFile.put("importJs", "member/ott/stat.js.html");

        return "member/ott/stat";
    }
}
