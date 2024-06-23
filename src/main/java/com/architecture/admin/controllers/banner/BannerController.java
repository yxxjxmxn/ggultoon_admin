package com.architecture.admin.controllers.banner;

import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.contents.CategoryDto;
import com.architecture.admin.services.banner.BannerService;
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
@RequestMapping("/banner")
public class BannerController extends BaseController {

    private final CategoryService categoryService;
    private final BannerService bannerService;

    @GetMapping("/list")
    public String lists(@ModelAttribute("params") SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(59);

        hmImportFile.put("importJs", "banner/list.js.html");

        return "banner/list";
    }

    @GetMapping("/register")
    public String registerBanner(Model model) {

        // 관리자 접근 권한
        super.adminAccessFail(59);

        // 카테고리, 장르
        List<CategoryDto> categoryList =  categoryService.getCategory();
        // 배너 마지막 순번
        Integer lastSort = bannerService.getSortNumber();

        // json string 변환
        Map<String, Object> map = new HashMap<>();
        map.put("webtoonCategory", categoryList.get(0));
        map.put("comicCategory", categoryList.get(1));
        map.put("novelCategory", categoryList.get(2));

        map.put("categoryList", categoryList);
        map.put("lastSort", lastSort);

        JSONObject data = new JSONObject(map);
        model.addAttribute("data", data);

        hmImportFile.put("importJs", "banner/register.js.html");

        return "banner/register";
    }

    @GetMapping("/{idx}")
    public String view(@PathVariable(name="idx", required = false) Integer idx, @ModelAttribute("params") SearchDto searchDto, Model model) {

        // 관리자 접근 권한
//        super.adminAccessFail(59);

        // 카테고리, 장르
        List<CategoryDto> categoryList =  categoryService.getCategory();


        // json string 변환
        Map<String, Object> map = new HashMap<>();
        map.put("webtoonCategory", categoryList.get(0));
        map.put("comicCategory", categoryList.get(1));
        map.put("novelCategory", categoryList.get(2));

        map.put("categoryList", categoryList);

        JSONObject data = new JSONObject(map);
        model.addAttribute("data", data);

        hmImportFile.put("importJs", "banner/view.js.html");

        return "banner/view";
    }

}
