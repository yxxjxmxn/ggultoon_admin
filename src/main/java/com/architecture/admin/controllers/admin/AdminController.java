package com.architecture.admin.controllers.admin;

import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.services.admin.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
public class AdminController extends BaseController {

    private final AdminService adminService;

    /**
     * 관리자 로그인
     */
    @GetMapping("/login")
    public String login() {
        // view pages
        hmImportFile.put("importJs", "admin/login.js.html");
        return "admin/login";
    }

    /**
     * 관리자 로그아웃
     */
    @GetMapping("/logout")
    public String logout() {
        adminService.logout();
        return "redirect:/admin/login";
    }

    /**
     * 관리자 가입
     */
    @GetMapping("/join")
    public String join() {
        // view pages
        hmImportFile.put("importJs", "admin/join.js.html");
        return "admin/join";
    }

    /**
     * 관리자 목록 조회
     * @param model
     * @param page
     * @param searchDto
     * @return
     */
    @GetMapping()
    public String adminList(Model model, @RequestParam(required = false, defaultValue = "1") int page, @ModelAttribute SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(8);

        model.addAttribute("page", page);
        model.addAttribute("search", searchDto);

        hmImportFile.put("importJs", "admin/list.js.html");

        return "admin/list";
    }

    /**
     * 관리자 상세
     *
     * @param idx (admin.idx)
     * @param model
     * @return
     */
    @GetMapping("/{idx}")
    public String view(@PathVariable(required = false) Long idx, Model model) {

        // 관리자 접근 권한
        super.adminAccessFail(8);

        model.addAttribute("idx", idx);

        hmImportFile.put("importJs", "admin/view.js.html");

        return "admin/view";
    }

    /**
     * 관리자 내 정보 수정
     *
     * @param idx (admin.idx)
     * @param model
     * @return
     */
    @GetMapping("/mypage/{idx}")
    public String modifyMyPage(@PathVariable(required = false) Long idx, Model model) {

        // 관리자 접근 권한
        super.adminAccessFail(8);

        model.addAttribute("idx", idx);

        hmImportFile.put("importJs", "admin/mypage.js.html");

        return "admin/mypage";
    }

    /**
     * 관리자 비밀번호 수정
     *
     * @param idx (admin.idx)
     * @param model
     * @return
     */
    @GetMapping("/mypage/password/{idx}")
    public String modifyPassword(@PathVariable(required = false) Long idx, Model model) {

        // 관리자 접근 권한
        super.adminAccessFail(8);

        model.addAttribute("idx", idx);

        hmImportFile.put("importJs", "admin/password.js.html");

        return "admin/password";
    }

    /**
     * api 로그인
     */
    @GetMapping("/loginapi")
    public String loginapi() {
        return "admin/loginapi";
    }

    /**
     * 본인인증 샘플
     */
    @GetMapping("/sample/sample")
    public String sample() {
        return "admin/sample";
    }

    /**
     * api 로그인
     */
    @GetMapping("/sample/pop")
    public String pop() {
        return "admin/pop";
    }
}
