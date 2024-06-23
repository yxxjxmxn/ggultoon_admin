package com.architecture.admin.controllers.coupon;

import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.models.dto.SearchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/coupons")
public class CouponController extends BaseController {

    /**************************************************************************************
     * 쿠폰 업체 리스트
     **************************************************************************************/

    /**
     * 쿠폰 업체 리스트
     * @param model
     * @param page
     * @param searchDto
     * @return
     */
    @GetMapping("/stores")
    public String getCouponStoreList(Model model,
                                     @RequestParam(required = false, defaultValue = "1") int page,
                                     @ModelAttribute SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(61);

        model.addAttribute("page", page);
        model.addAttribute("search", searchDto);

        hmImportFile.put("importJs", "coupon/store/list.js.html");

        return "coupon/store/list";
    }

    /**
     * 쿠폰 업체 상세
     * @param idx
     * @return
     */
    @GetMapping("/stores/{idx}")
    public String getViewCouponStore(@PathVariable(required = false) Long idx, Model model) {

        // 관리자 접근 권한
        super.adminAccessFail(61);

        model.addAttribute("idx", idx);

        hmImportFile.put("importJs", "coupon/store/view.js.html");

        return "coupon/store/view";
    }

    /**
     * 쿠폰 업체 등록
     * @return
     */
    @GetMapping("/stores/register")
    public String registerCouponStore() {

        // 관리자 접근 권한
        super.adminAccessFail(61);

        hmImportFile.put("importJs", "coupon/store/register.js.html");

        return "coupon/store/register";
    }

    /**************************************************************************************
     * 쿠폰 리스트
     **************************************************************************************/

    /**
     * 쿠폰 리스트
     * @param model
     * @param page
     * @param searchDto
     * @return
     */
    @GetMapping()
    public String getCouponList(Model model,
                                     @RequestParam(required = false, defaultValue = "1") int page,
                                     @ModelAttribute SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(62);

        model.addAttribute("page", page);
        model.addAttribute("search", searchDto);

        hmImportFile.put("importJs", "coupon/list.js.html");

        return "coupon/list";
    }

    /**
     * 쿠폰 상세
     * @param idx
     * @return
     */
    @GetMapping("/{idx}")
    public String getViewCoupon(@PathVariable(required = false) Long idx, Model model) {

        // 관리자 접근 권한
        super.adminAccessFail(62);

        model.addAttribute("idx", idx);

        hmImportFile.put("importJs", "coupon/view.js.html");

        return "coupon/view";
    }

    /**
     * 쿠폰 등록
     * @return
     */
    @GetMapping("/register")
    public String registerCoupon() {

        // 관리자 접근 권한
        super.adminAccessFail(62);

        hmImportFile.put("importJs", "coupon/register.js.html");

        return "coupon/register";
    }

    /**************************************************************************************
     * 회원별 쿠폰 사용 내역
     **************************************************************************************/

    /**
     * 회원별 쿠폰 사용 내역
     * @param model
     * @param page
     * @param searchDto
     * @return
     */
    @GetMapping("/use")
    public String getCouponUsedList(Model model,
                                     @RequestParam(required = false, defaultValue = "1") int page,
                                     @ModelAttribute SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(63);

        model.addAttribute("page", page);
        model.addAttribute("search", searchDto);

        hmImportFile.put("importJs", "coupon/use/list.js.html");

        return "coupon/use/list";
    }

    /**************************************************************************************
     * 쿠폰 통계
     **************************************************************************************/

    /**
     * 쿠폰 통계 리스트
     * @param model
     * @param page
     * @param searchDto
     * @return
     */
    @GetMapping("/stat")
    public String getCouponStat(Model model,
                                    @RequestParam(required = false, defaultValue = "1") int page,
                                    @ModelAttribute SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(63);

        model.addAttribute("page", page);
        model.addAttribute("search", searchDto);

        hmImportFile.put("importJs", "coupon/stat.js.html");

        return "coupon/stat";
    }
}
