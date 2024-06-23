package com.architecture.admin.controllers.admin;

import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.models.dto.SearchDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cp")
public class CpController extends BaseController {
    /**
     * CP관리자 목록 조회
     * @param searchDto
     * @return
     */
    @GetMapping()
    public String list(Model model,
                            @RequestParam(required = false, defaultValue = "1") int page,
                            @ModelAttribute SearchDto searchDto) {
        // 관리자 접근 권한
        super.adminAccessFail(12);

        model.addAttribute("page", page);
        model.addAttribute("search", searchDto);

        hmImportFile.put("importJs", "cp/list.js.html");

        return "cp/list";
    }

    /**
     * CP관리자 상세
     *
     * @param idx
     * @param model
     * @return
     */
    @GetMapping("/{idx}")
    public String view(@PathVariable(required = false) Long idx, Model model) {
        // 관리자 접근 권한
        super.adminAccessFail(12);

        model.addAttribute("idx", idx);

        hmImportFile.put("importJs", "cp/view.js.html");

        return "cp/view";
    }

    /**
     * CP관리자 등록
     *
     * @return
     */
    @GetMapping("/regist")
    public String regist() {
        // 관리자 접근 권한
        super.adminAccessFail(12);

        hmImportFile.put("importJs", "cp/regist.js.html");

        return "cp/regist";
    }


}
