package com.architecture.admin.controllers.menu;

import com.architecture.admin.config.AdminLevelConfig;
import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.models.dto.menu.MenuDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;

/*****************************************************
 * WEBTOON 회원 컨트롤러
 ****************************************************/
@RequiredArgsConstructor
@Controller
@RequestMapping("/menu")
public class MenuController extends BaseController {
    /*****************************************************
     * 메뉴 리스트
     ****************************************************/
    @GetMapping("/list")
    public String list(@ModelAttribute("param") final MenuDto params) {
        
        // 관리자 접근 권한
        super.adminAccessFail(2);
        
        // 메뉴 리스트 조회
        List<MenuDto> list = menuService.getList(params);

        // 메뉴 리스트의 정렬 최대값 조회
        Integer maxSort = menuService.getMaxSort();
        
        // set view data
        hmDataSet.put("list", list);
        hmDataSet.put("maxSort", maxSort);

        // view pages
        hmImportFile.put("importJs", "menu/menu.js.html");
        return "menu/list";
    }

    /*****************************************************
     * 카테고리 추가
     ****************************************************/
    @GetMapping("/category/regist")
    public String cateRegist() {
        // 관리자 접근 권한
        super.adminAccessFail(2);
        // 관리자 레벨 가져오기
        HashMap<Integer, String> hmAdminLevel = (HashMap<Integer, String>) AdminLevelConfig.getAdminLevel();
        // set view data
        hmDataSet.put("adminLevel", hmAdminLevel);
        // view pages
        hmImportFile.put("importJs", "menu/menu.js.html");
        return "menu/category/regist";
    }

    /*****************************************************
     * 하위 메뉴 추가
     ****************************************************/
    @GetMapping("/regist/{idx}")
    public String regist(@PathVariable("idx") Integer idx) {
        // 관리자 접근 권한
        super.adminAccessFail(2);

        // 관리자 레벨 가져오기
        HashMap<Integer, String> hmAdminLevel = (HashMap<Integer, String>) AdminLevelConfig.getAdminLevel();
        // set view data
        hmDataSet.put("adminLevel", hmAdminLevel);
        hmDataSet.put("idx", idx);
        // view pages
        hmImportFile.put("importJs", "menu/menu.js.html");
        return "menu/regist";
    }

    /*****************************************************
     * 카테고리 수정
     ****************************************************/
    @GetMapping("/category/modify/{idx}")
    public String cateModify(@PathVariable("idx") Integer idx) {
        // 관리자 접근 권한
        super.adminAccessFail(2);
        // get list
        MenuDto oCate = menuService.getCateInfo(idx);
        // 관리자 레벨 가져오기
        HashMap<Integer, String> hmAdminLevel = (HashMap<Integer, String>) AdminLevelConfig.getAdminLevel();
        // set view data
        hmDataSet.put("adminLevel", hmAdminLevel);
        hmDataSet.put("data", oCate);
        // view pages
        hmImportFile.put("importJs", "menu/menu.js.html");
        return "menu/category/modify";
    }

    /*****************************************************
     * 카테고리 하위 메뉴 수정
     ****************************************************/
    @GetMapping("/modify/{idx}")
    public String modify(@PathVariable("idx") Integer idx) {
        // 관리자 접근 권한
        super.adminAccessFail(2);
        // get list
        MenuDto oCate = menuService.getCateInfo(idx);
        // 관리자 레벨 가져오기
        HashMap<Integer, String> hmAdminLevel = (HashMap<Integer, String>) AdminLevelConfig.getAdminLevel();
        // set view data
        hmDataSet.put("adminLevel", hmAdminLevel);
        hmDataSet.put("idx", idx);
        hmDataSet.put("data", oCate);
        // view pages
        hmImportFile.put("importJs", "menu/menu.js.html");
        return "menu/modify";
    }
}