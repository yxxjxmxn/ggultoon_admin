package com.architecture.admin.controllers.contents;

import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.contents.CategoryDto;
import com.architecture.admin.services.contents.CategoryService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
@RequestMapping("/contents")
public class ContentsController extends BaseController {

    private final CategoryService categoryService;

    /**
     * 컨텐츠 목록
     * @return
     */
    @GetMapping("/list")
    public String lists(@ModelAttribute("params") SearchDto searchDto) {

        hmImportFile.put("importJs", "contents/list.js.html");

        return "contents/list";
    }

    /**
     * 컨텐츠 등록
     * @return
     */
    @GetMapping("/register")
    public String regist(@ModelAttribute("params") SearchDto searchDto, Model model) {

        // 카테고리, 장르
        List<CategoryDto> categoryList =  categoryService.getCategory();

        // json string 변환
        Map<String, Object> map = new HashMap<>();
        map.put("categoryList", categoryList);

        JSONObject data = new JSONObject(map);
        model.addAttribute("data", data);

        hmImportFile.put("importJs", "contents/register.js.html");

        return "contents/register";
    }

    /**
     * 컨텐츠 상세
     * @param idx
     * @param searchDto
     * @param model
     * @return
     */
    @GetMapping("/view/{idx}")
    public String view(@PathVariable(name="idx", required = false) Integer idx, @ModelAttribute("params") SearchDto searchDto, Model model) {

        // 카테고리, 장르
        List<CategoryDto> categoryList =  categoryService.getCategory();

        // json string 변환
        Map<String, Object> map = new HashMap<>();
        map.put("categoryList", categoryList);

        JSONObject data = new JSONObject(map);
        model.addAttribute("data", data);

        hmImportFile.put("importJs", "contents/view.js.html");

        return "contents/view";
    }

    /**
     * Tag 레이어
     * @return
     */
    @GetMapping("/layer/{name}")
    public String layer(@PathVariable(name="name", required = false) String name) {

        return "contents/layer/" + name + "Layer";
    }
}
