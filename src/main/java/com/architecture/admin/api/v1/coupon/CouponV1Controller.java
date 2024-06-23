package com.architecture.admin.api.v1.coupon;

import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.libraries.excel.ExcelXlsxView;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.coupon.CouponDto;
import com.architecture.admin.models.dto.coupon.CouponStoreDto;
import com.architecture.admin.services.coupon.CouponService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/coupons")
public class CouponV1Controller extends BaseController {

    private final CouponService couponService;

    /**************************************************************************************
     * 쿠폰 업체 리스트
     **************************************************************************************/

    /**
     * 쿠폰 업체 리스트
     *
     * @param searchDto
     */
    @GetMapping("/stores")
    public String getCouponStoreList(@ModelAttribute SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(68);

        // 쿠폰 업체 리스트 조회
        JSONObject joData = couponService.getCouponStoreList(searchDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.coupon.success.search"); // 검색을 완료하였습니다.

        // 조회 결과가 없는 경우
        if (joData.getJSONArray("list").isEmpty()) {
            sMessage = super.langMessage("lang.coupon.exception.search.fail"); // 검색 결과가 없습니다.
        }
        return displayJson(true, "1000", sMessage, joData);
    }

    /**
     * 쿠폰 업체 리스트 엑셀 다운로드
     *
     * @param searchDto
     * @return
     */
    @GetMapping("/stores/excel")
    public ModelAndView excelCouponStoreList(@ModelAttribute("param") SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(68);

        // 뷰에 담을 모델객체를 생성(엑셀 파일에 들어갈 데이터)
        Map<String, Object> mExcelData = couponService.excelCouponStoreList(searchDto);

        // AbstractXlsxView 가 동작
        return new ModelAndView(new ExcelXlsxView(), mExcelData);
    }

    /**
     * 쿠폰 업체 상세
     * @param idx
     * @return
     */
    @GetMapping("/stores/{idx}")
    public String getViewCouponStore(@PathVariable(name = "idx") Long idx) {

        // 관리자 접근 권한
        super.adminAccessFail(68);

        // 쿠폰 업체 상세 조회
        JSONObject joData = couponService.getViewCouponStore(idx);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.coupon.success.search"); // 검색을 완료하였습니다.

        // 조회 결과가 없는 경우
        if (joData.isEmpty()) {
            sMessage = super.langMessage("lang.coupon.exception.search.fail"); // 검색 결과가 없습니다.
        }
        return displayJson(true, "1000", sMessage, joData);
    }

    /**
     * 쿠폰 업체 등록
     * @param couponStoreDto
     * @return
     */
    @PostMapping("/stores")
    public String insertCouponStore(@ModelAttribute("couponStoreDto") CouponStoreDto couponStoreDto) {

        // 관리자 접근 권한
        super.adminAccessFail(68);

        // 쿠폰 업체 등록
        couponService.insertCouponStore(couponStoreDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.coupon.success.register"); // 등록을 완료하였습니다.

        return displayJson(true, "1000", sMessage);
    }

    /**
     * 쿠폰 업체 삭제
     * @param idx
     * @return
     */
    @DeleteMapping("/stores/{idx}")
    public String deleteCouponStore(@PathVariable(name = "idx") Long idx) {

        // 관리자 접근 권한
        super.adminAccessFail(68);

        // 쿠폰 업체 삭제
        couponService.deleteCouponStore(idx);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.coupon.success.delete"); // 삭제를 완료하였습니다.

        return displayJson(true, "1000", sMessage);
    }

    /**
     * 쿠폰 업체 수정
     * @param idx
     * @return
     */
    @PutMapping("/stores/{idx}")
    public String updateCouponStore(@PathVariable(name = "idx") Long idx,
                                    @ModelAttribute("couponStoreDto") CouponStoreDto couponStoreDto) {

        // 관리자 접근 권한
        super.adminAccessFail(68);

        // idx set
        couponStoreDto.setIdx(idx);

        // 쿠폰 업체 수정
        couponService.updateCouponStore(couponStoreDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.coupon.success.modify"); // 수정을 완료하였습니다.

        return displayJson(true, "1000", sMessage);
    }

    /**************************************************************************************
     * 쿠폰 리스트
     **************************************************************************************/

    /**
     * 쿠폰 리스트
     *
     * @param searchDto
     */
    @GetMapping()
    public String getCouponList(@ModelAttribute SearchDto searchDto){

        // 관리자 접근 권한
        super.adminAccessFail(69);

        // 쿠폰 리스트 조회
        JSONObject joData = couponService.getCouponList(searchDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.coupon.success.search"); // 검색을 완료하였습니다.

        // 조회 결과가 없는 경우
        if (joData.getJSONArray("list").isEmpty()) {
            sMessage = super.langMessage("lang.coupon.exception.search.fail"); // 검색 결과가 없습니다.
        }
        return displayJson(true, "1000", sMessage, joData);
    }

    /**
     * 쿠폰 리스트 엑셀 다운로드
     *
     * @param searchDto
     * @return
     */
    @GetMapping("/excel")
    public ModelAndView excelCouponList(@ModelAttribute("param") SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(69);

        // 뷰에 담을 모델객체를 생성(엑셀 파일에 들어갈 데이터)
        Map<String, Object> mExcelData = couponService.excelCouponList(searchDto);

        // AbstractXlsxView 가 동작
        return new ModelAndView(new ExcelXlsxView(), mExcelData);
    }

    /**
     * 쿠폰 상세
     * @param idx
     * @return
     */
    @GetMapping("/{idx}")
    public String getViewCoupon(@PathVariable(name = "idx") Long idx){

        // 관리자 접근 권한
        super.adminAccessFail(69);

        // 쿠폰 상세
        JSONObject joData = couponService.getViewCoupon(idx);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.coupon.success.search"); // 검색을 완료하였습니다.

        // 조회 결과가 없는 경우
        if (joData.length() < 1) {
            sMessage = super.langMessage("lang.coupon.exception.search.fail"); // 검색 결과가 없습니다.
        }
        return displayJson(true, "1000", sMessage, joData);
    }

    /**
     * 쿠폰 등록
     * @param couponDto
     * @return
     */
    @PostMapping()
    public String insertCoupon(@ModelAttribute("couponDto") CouponDto couponDto) {

        // 관리자 접근 권한
        super.adminAccessFail(69);

        // 쿠폰 등록
        couponService.insertCoupon(couponDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.coupon.success.register"); // 등록을 완료하였습니다.

        return displayJson(true, "1000", sMessage);
    }

    /**
     * 쿠폰 삭제
     * @param idx
     * @return
     */
    @DeleteMapping("/{idx}")
    public String deleteCoupon(@PathVariable(name = "idx") Long idx) {

        // 관리자 접근 권한
        super.adminAccessFail(68);

        // 쿠폰 삭제
        couponService.deleteCoupon(idx);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.coupon.success.delete"); // 삭제를 완료하였습니다.

        return displayJson(true, "1000", sMessage);
    }

    /**
     * 쿠폰 수정
     * @param idx
     * @return
     */
    @PutMapping("/{idx}")
    public String updateCoupon(@PathVariable(name = "idx") Long idx,
                                    @ModelAttribute("couponDto") CouponDto couponDto) {

        // 관리자 접근 권한
        super.adminAccessFail(68);

        // idx set
        couponDto.setIdx(idx);

        // 쿠폰 수정
        couponService.updateCoupon(couponDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.coupon.success.modify"); // 수정을 완료하였습니다.

        return displayJson(true, "1000", sMessage);
    }

    /**************************************************************************************
     * 회원별 쿠폰 사용 내역
     **************************************************************************************/

    /**
     * 회원별 쿠폰 사용 내역
     *
     * @param searchDto
     */
    @GetMapping("/use")
    public String getCouponUsedList(@ModelAttribute SearchDto searchDto){

        // 관리자 접근 권한
        super.adminAccessFail(70);

        // 회원별 쿠폰 사용 내역 조회
        JSONObject joData = couponService.getCouponUsedList(searchDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.coupon.success.search"); // 검색을 완료하였습니다.

        // 조회 결과가 없는 경우
        if (joData.getJSONArray("list").isEmpty()) {
            sMessage = super.langMessage("lang.coupon.exception.search.fail"); // 검색 결과가 없습니다.
        }
        return displayJson(true, "1000", sMessage, joData);
    }

    /**
     * 회원별 쿠폰 사용 내역 엑셀 다운로드
     *
     * @param searchDto
     * @return
     */
    @GetMapping("/use/excel")
    public ModelAndView excelCouponUsedList(@ModelAttribute("param") SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(70);

        // 뷰에 담을 모델객체를 생성(엑셀 파일에 들어갈 데이터)
        Map<String, Object> mExcelData = couponService.excelCouponUsedList(searchDto);

        // AbstractXlsxView 가 동작
        return new ModelAndView(new ExcelXlsxView(), mExcelData);
    }

    /**************************************************************************************
     * 쿠폰 통계
     **************************************************************************************/

    /**
     * 쿠폰 통계
     *
     * @param searchDto
     */
    @GetMapping("/stat")
    public String getCouponStat(@ModelAttribute SearchDto searchDto){

        // 관리자 접근 권한
        super.adminAccessFail(71);

        // 쿠폰 통계 조회
        JSONObject joData = couponService.getCouponStat(searchDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.coupon.success.search"); // 검색을 완료하였습니다.

        // 조회 결과가 없는 경우
        if (joData.getJSONArray("list").isEmpty()) {
            sMessage = super.langMessage("lang.coupon.exception.search.fail"); // 검색 결과가 없습니다.
        }
        return displayJson(true, "1000", sMessage, joData);
    }

    /**
     * 쿠폰 통계 엑셀 다운로드
     *
     * @param searchDto
     * @return
     */
    @GetMapping("/stat/excel")
    public ModelAndView excelCouponStat(@ModelAttribute("param") SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(71);

        // 뷰에 담을 모델객체를 생성(엑셀 파일에 들어갈 데이터)
        Map<String, Object> mExcelData = couponService.excelCouponStat(searchDto);

        // AbstractXlsxView 가 동작
        return new ModelAndView(new ExcelXlsxView(), mExcelData);
    }
}
