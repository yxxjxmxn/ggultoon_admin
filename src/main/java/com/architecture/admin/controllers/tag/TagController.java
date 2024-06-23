package com.architecture.admin.controllers.tag;

import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.models.dto.SearchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/tags")
public class TagController extends BaseController {

    /**
     * 작품 태그 리스트
     * @param searchDto
     * @return
     */
    @GetMapping()
    public String tagList(Model model,
                              @RequestParam(required = false, defaultValue = "1") int page,
                              @ModelAttribute SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(32);

        model.addAttribute("page", page);
        model.addAttribute("search", searchDto);

        hmImportFile.put("importJs", "contents/tag/list.js.html");

        return "contents/tag/list";
    }

    /**
     * 작품 태그 상세
     *
     * @param idx
     * @param model
     * @return
     */
    @GetMapping("/{idx}")
    public String viewTag(@PathVariable(required = false) Long idx, Model model) {

        // 관리자 접근 권한
        super.adminAccessFail(32);

        model.addAttribute("idx", idx);

        hmImportFile.put("importJs", "contents/tag/view.js.html");

        return "contents/tag/view";
    }

    /**
     * 작품 태그 등록
     */
    @GetMapping("/register")
    public String registerPaymentMethod() {

        // 관리자 접근 권한
        super.adminAccessFail(32);

        hmImportFile.put("importJs", "contents/tag/register.js.html");

        return "contents/tag/register";
    }

    /**
     * 작품 태그 수정
     */
    @GetMapping("/modify/{idx}")
    public String modifyTag(@PathVariable(required = false) Long idx, Model model) {

        // 관리자 접근 권한
        super.adminAccessFail(32);

        model.addAttribute("idx", idx);

        hmImportFile.put("importJs", "contents/tag/modify.js.html");

        return "contents/tag/modify";
    }
}
