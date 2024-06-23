package com.architecture.admin.api.v1.menu;

import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dto.menu.MenuDto;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/*****************************************************
 * 메뉴 API
 ****************************************************/
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/menu")
public class MenuV1ApiController extends BaseController {

    /*****************************************************
     * 카테고리 추가
     ****************************************************/
    @PostMapping("/category/regist")
    public Object registCate(@Valid MenuDto menuDto,
                             BindingResult result){
        // 관리자 접근 권한
        super.adminAccessFail(2);

        if (result.hasErrors()) {
            return displayError(result);
        }
        // 등록처리
        menuService.cateRegist(menuDto);

        String message = super.langMessage("lang.menu.regist");
        return displayJson(true, "1000", message);
    }

    /*****************************************************
     * 하위 메뉴 추가
     ****************************************************/
    @PostMapping("/regist")
    public Object regist(@Valid MenuDto menuDto,
                         BindingResult result){
        // 관리자 접근 권한
        super.adminAccessFail(2);
        if (result.hasErrors()) {
            return displayError(result);
        }
        // 등록처리
        menuService.regist(menuDto);

        String message = super.langMessage("lang.menu.regist");
        return displayJson(true, "1000", message);
    }

    /*****************************************************
     * 카테고리 수정
     ****************************************************/
    @PutMapping("/modify")
    public String modify(@Valid MenuDto menuDto, BindingResult result){
        // 관리자 접근 권한
        super.adminAccessFail(2);
        if (result.hasErrors()) {
            return displayError(result);
        }
        // 수정 처리
        menuService.cateModify(menuDto);

        String message = super.langMessage("lang.menu.modify");
        return displayJson(true, "1000", message);
    }

    /*****************************************************
     * 카테고리 정렬 수정
     ****************************************************/
    @GetMapping("/swap/{idx}/{sort}")
    public String swap(@PathVariable("idx") Integer idx,
                       @PathVariable("sort") String sort){
        // 관리자 접근 권한
        super.adminAccessFail(2);

        // 정렬을 변경하려는 메뉴 정보 조회
        MenuDto oCateInfo = menuService.getCateInfo(idx);

        // 스왑 메뉴 변수
        MenuDto oSwapInfo;
        
        // 다음 순서로 정렬 변경 시
        if (Objects.equals(sort, "next")) {
            
            // 현재 정렬을 변경하려는 메뉴의 다음 순서에 있는 메뉴 조회
            oSwapInfo = menuService.getNextMenu(oCateInfo.getParent(), oCateInfo.getSort());

            // 이전 순서로 정렬 변경 시
        } else if (Objects.equals(sort, "prev")) {

            // 현재 정렬을 변경하려는 메뉴의 이전 순서에 있는 메뉴 조회
            oSwapInfo = menuService.getPrevMenu(oCateInfo.getParent(), oCateInfo.getSort());
            
            // 예외 처리
        } else {
            throw new CustomException(CustomError.MENU_SWAP_FAIL);
        }

        Integer iOriSort = oCateInfo.getSort();     // 현 메뉴의 정렬 순서
        Integer iSwapSort = oSwapInfo.getSort();    // 스왑할 메뉴의 정렬 순서

        // 순서값들의 차이가 2 이상인 경우 1로 줄여주기
        // 오름차순 정렬이므로, 작은 값을 기준으로 큰값을 줄여줌
        if (iOriSort < iSwapSort && (iSwapSort - iOriSort) > 1) {
            iSwapSort = iOriSort + 1;
        } else {
            if (iOriSort > iSwapSort && (iOriSort - iSwapSort) > 1) {
                iOriSort = iSwapSort + 1;
            }
        }

        // 정렬 수정
        menuService.updateSort(oCateInfo.getIdx(), iSwapSort); // 현 메뉴 순서 -> 스왑 메뉴 순서로 변경
        menuService.updateSort(oSwapInfo.getIdx(), iOriSort); // 스왑 메뉴 순서 -> 현 메뉴 순서로 변경

        String message = super.langMessage("lang.menu.modify");
        return displayJson(true, "1000", message);
    }

    /*****************************************************
     * 카테고리 정렬 수정
     ****************************************************/
    @PatchMapping("/sort")
    public String sort(@RequestParam(value = "idx[]") String[] myParams){
        // 관리자 접근 권한
        super.adminAccessFail(2);
        List<String> list = new ArrayList<>(Arrays.asList(myParams));

        for (int i = 0; i < list.size(); i++) {
            menuService.updateSort(Integer.valueOf(list.get(i)), i + 1);
        }

        String message = super.langMessage("lang.menu.modify");
        return displayJson(true, "1000", message);
    }
}