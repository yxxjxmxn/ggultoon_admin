package com.architecture.admin.api.v1.payment;

import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.libraries.excel.ExcelXlsxView;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.payment.PaymentDto;
import com.architecture.admin.models.dto.payment.PaymentMethodDto;
import com.architecture.admin.models.dto.payment.PaymentProductDto;
import com.architecture.admin.services.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/payments")
public class PaymentV1Controller extends BaseController {

    private final PaymentService paymentService;

    /**************************************************************************************
     * 결제 내역
     **************************************************************************************/

    /**
     * 결제 내역 리스트
     *
     * @param searchDto
     * @return
     */
    @GetMapping()
    public String paymentList(@ModelAttribute SearchDto searchDto){

        // 관리자 접근 권한
        super.adminAccessFail(10);

        // 내보낼 데이터 set
        String sMessage = ""; // 응답 메시지
        JSONObject joData = new JSONObject();
        List<PaymentDto> listPayment = null;

        // 날짜 유효성 검사
        Boolean isValidDate = dateValidator(searchDto);

        // 유효한 날짜일 경우
        if (Boolean.TRUE == isValidDate) {

            // 검색어 공백제거
            searchDto.setSearchWord(searchDto.getSearchWord().trim());

            // 검색 조건 유효성 검사
            isValidSearchDateType(searchDto);

            // 전체 결제 내역 카운트
            int totalCnt = paymentService.getPaymentTotalCnt(searchDto);

            if (totalCnt < 1) { // 검색 결과가 없는 경우

                sMessage = super.langMessage("lang.payment.exception.search_fail"); // 검색 결과가 없습니다.

            } else { // 검색 결과가 있는 경우

                listPayment = paymentService.getPaymentList(searchDto);
                sMessage = super.langMessage("lang.payment.success.search"); // 검색을 완료하였습니다.

                // paging 담기
                joData.put("params", new JSONObject(searchDto));

            }
        }
        // 유효하지 않은 날짜일 경우
        else {
            sMessage = super.langMessage("lang.payment.exception.date.format"); // 잘못된 날짜 형식입니다.
        }
        // list 담기
        joData.put("list", listPayment);

        return displayJson(true, "1000", sMessage, joData);
    }

    /**
     * 결제 내역 엑셀 다운로드
     *
     * @param searchDto
     * @return
     */
    @GetMapping("/excel")
    public ModelAndView paymentListExcelDownload(@ModelAttribute("param") SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(10);

        //검색어 공백제거
        searchDto.setSearchWord(searchDto.getSearchWord().trim());

        // 뷰에 담을 모델객체를 생성(엑셀 파일에 들어갈 데이터)
        Map<String, Object> mExcelData = paymentService.excelPayment(searchDto);

        // AbstractXlsxView 가 동작
        return new ModelAndView(new ExcelXlsxView(), mExcelData);
    }

    /**
     * 회원 결제 내역 상세
     * @param paymentIdx (payment.idx)
     * @return
     */
    @GetMapping("/{idx}")
    public String viewPayment(@PathVariable(name = "idx") Long paymentIdx) {

        // 관리자 접근 권한
        super.adminAccessFail(10);

        // 결제 상세
        PaymentDto viewPayment = paymentService.getViewPayment(paymentIdx);

        // response object
        JSONObject data = new JSONObject(viewPayment);

        String sMessage = "";

        if (viewPayment == null) {
            sMessage = super.langMessage("lang.payment.exception.search_fail"); // 검색 결과가 없습니다.
        }

        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 결제 취소
     * @param paymentIdx (취소할 결제 정보의 payment.idx)
     * @return
     */
    @DeleteMapping("/{idx}")
    public String deletePayment(@PathVariable(name = "idx") Long paymentIdx) {

        // 관리자 접근 권한
        super.adminAccessFail(10);

        // 결제 취소
        paymentService.deleteDatePayment(paymentIdx);

        String sMessage = super.langMessage("lang.payment.success.delete"); // 결제를 취소하였습니다.

        return displayJson(true, "1000", sMessage);
    }

    /**************************************************************************************
     * 회차 구매 내역
     **************************************************************************************/

    /**
     * 회원 회차구매 내역 엑셀 다운로드
     *
     * @param searchDto
     * @return
     */
    @GetMapping("/purchases/members/excel")
    public ModelAndView memberPurchaseListExcelDownload(@ModelAttribute SearchDto searchDto) {
        //검색어 공백제거
        searchDto.setSearchWord(searchDto.getSearchWord().trim());

        Map<String, Object> excelData = paymentService.excelPurchase(searchDto);

        return new ModelAndView(new ExcelXlsxView(), excelData);
    }

    /**************************************************************************************
     * 결제 수단
     **************************************************************************************/

    /**
     * 결제 수단 리스트 엑셀 다운로드
     *
     * @param searchDto
     * @return
     */
    @GetMapping("/methods/excel")
    public ModelAndView paymentMethodListExcelDownload(@ModelAttribute("param") SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(25);

        // 뷰에 담을 모델객체를 생성(엑셀 파일에 들어갈 데이터)
        Map<String, Object> mExcelData = paymentService.excelPaymentMethod(searchDto);

        // AbstractXlsxView 가 동작
        return new ModelAndView(new ExcelXlsxView(), mExcelData);
    }

    /**
     * 결제 수단 리스트 조회
     * @param searchDto
     * @return
     */
    @GetMapping("/methods")
    public String paymentMethodList(@ModelAttribute SearchDto searchDto){

        // 관리자 접근 권한
        super.adminAccessFail(25);

        // 결제 수단 리스트 조회
        JSONObject joData = paymentService.getPaymentMethodList(searchDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.payment.success.search"); // 검색을 완료하였습니다.

        // 조회 결과가 없는 경우
        if (joData.getJSONArray("list").isEmpty()) {
            sMessage = super.langMessage("lang.payment.exception.search_fail"); // 검색 결과가 없습니다.
        }

        return displayJson(true, "1000", sMessage, joData);
    }

    /**
     * 결제 수단 상세
     * @param idx
     * @return
     */
    @GetMapping("/methods/{idx}")
    public String viewPaymentMethod(@PathVariable(name = "idx") Integer idx){

        // 관리자 접근 권한
        super.adminAccessFail(25);

        // 결제 수단 상세
        JSONObject joData = paymentService.getViewPaymentMethod(idx);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.payment.success.search"); // 검색을 완료하였습니다.

        // 조회 결과가 없는 경우
        if (joData.isEmpty()) {
            sMessage = super.langMessage("lang.payment.exception.search_fail"); // 검색 결과가 없습니다.
        }

        return displayJson(true, "1000", sMessage, joData);
    }

    /**
     * 결제 수단 등록
     * @param paymentMethodDto
     * @return
     */
    @PostMapping("/methods")
    public String registerPaymentMethod(@ModelAttribute("PaymentMethodDto") PaymentMethodDto paymentMethodDto) {

        // 관리자 접근 권한
        super.adminAccessFail(25);

        // 결제 수단 등록
        paymentService.registerPaymentMethod(paymentMethodDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.payment.method.success.register"); // 등록을 완료하였습니다.

        return displayJson(true, "1000", sMessage);
    }

    /**
     * 결제 수단 삭제
     * @param paymentMethodIdx
     * @return
     */
    @DeleteMapping("/methods/{idx}")
    public String deletePaymentMethod(@PathVariable(name = "idx") Integer paymentMethodIdx) {

        // 관리자 접근 권한
        super.adminAccessFail(25);

        // 결제 수단 삭제
        paymentService.deletePaymentMethod(paymentMethodIdx);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.payment.method.success.delete"); // 삭제를 완료하였습니다.

        return displayJson(true, "1000", sMessage);
    }


    /**
     * 결제 수단 수정
     * @param idx
     * @return
     */
    @PutMapping("/methods/{idx}")
    public String modifyPaymentMethod(@PathVariable(name = "idx") Integer idx,
                                      @ModelAttribute("PaymentMethodDto") PaymentMethodDto paymentMethodDto) {

        // 관리자 접근 권한
        super.adminAccessFail(25);

        // idx set
        paymentMethodDto.setIdx(idx);

        // 결제 수단 수정
        paymentService.modifyPaymentMethod(paymentMethodDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.payment.method.success.modify"); // 수정을 완료하였습니다.

        return displayJson(true, "1000", sMessage);
    }

    /**************************************************************************************
     * 결제 상품
     **************************************************************************************/

    /**
     * 결제 상품 리스트 엑셀 다운로드
     *
     * @param searchDto
     * @return
     */
    @GetMapping("/products/excel")
    public ModelAndView paymentProductListExcelDownload(@ModelAttribute("param") SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(26);

        // 뷰에 담을 모델객체를 생성(엑셀 파일에 들어갈 데이터)
        Map<String, Object> mExcelData = paymentService.excelPaymentProduct(searchDto);

        // AbstractXlsxView 가 동작
        return new ModelAndView(new ExcelXlsxView(), mExcelData);
    }

    /**
     * 결제 상품 리스트 조회
     * @param searchDto
     * @return
     */
    @GetMapping("/products")
    public String paymentProductList(@ModelAttribute SearchDto searchDto){

        // 관리자 접근 권한
        super.adminAccessFail(26);

        // 결제 상품 리스트 조회
        JSONObject joData = paymentService.getPaymentProductList(searchDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.payment.success.search"); // 검색을 완료하였습니다.

        // 조회 결과가 없는 경우
        if (joData.getJSONArray("list").isEmpty()) {
            sMessage = super.langMessage("lang.payment.exception.search_fail"); // 검색 결과가 없습니다.
        }

        return displayJson(true, "1000", sMessage, joData);
    }

    /**
     * 결제 상품 상세
     * @param idx
     * @return
     */
    @GetMapping("/products/{idx}")
    public String viewPaymentProduct(@PathVariable(name = "idx") Integer idx){

        // 관리자 접근 권한
        super.adminAccessFail(26);

        // 결제 상품 상세
        JSONObject joData = paymentService.getViewPaymentProduct(idx);

        // 결제 상품 구분 리스트
        HashMap<String, String> productType = new HashMap<>();
        productType.put("pc", "PC");
        productType.put("mobile", "Mobile");
        productType.put("android", "Android");
        productType.put("ios", "IOS");

        // set view data
        joData.put("productType", productType);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.payment.success.search"); // 검색을 완료하였습니다.

        // 조회 결과가 없는 경우
        if (joData.isEmpty()) {
            sMessage = super.langMessage("lang.payment.exception.search_fail"); // 검색 결과가 없습니다.
        }

        return displayJson(true, "1000", sMessage, joData);
    }

    /**
     * 결제 상품 등록
     * @param paymentProductDto
     * @return
     */
    @PostMapping("/products")
    public String registerPaymentProduct(@ModelAttribute("PaymentMethodDto") PaymentProductDto paymentProductDto) {

        // 관리자 접근 권한
        super.adminAccessFail(26);

        // 결제 상품 등록
        paymentService.registerPaymentProduct(paymentProductDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.payment.product.success.register"); // 등록을 완료하였습니다.

        return displayJson(true, "1000", sMessage);
    }

    /**
     * 결제 상품 삭제
     * @param idx
     * @return
     */
    @DeleteMapping("/products/{idx}")
    public String deletePaymentProduct(@PathVariable(name = "idx") Integer idx) {

        // 관리자 접근 권한
        super.adminAccessFail(26);

        // 결제 상품 삭제
        paymentService.deletePaymentProduct(idx);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.payment.product.success.delete"); // 삭제를 완료하였습니다.

        return displayJson(true, "1000", sMessage);
    }


    /**
     * 결제 상품 수정
     * @param idx
     * @return
     */
    @PutMapping("/products/{idx}")
    public String modifyPaymentProduct(@PathVariable(name = "idx") Integer idx,
                                       @ModelAttribute("PaymentMethodDto") PaymentProductDto paymentProductDto) {

        // 관리자 접근 권한
        super.adminAccessFail(26);

        // idx set
        paymentProductDto.setIdx(idx);

        // 결제 상품 수정
        paymentService.modifyPaymentProduct(paymentProductDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.payment.product.success.modify"); // 수정을 완료하였습니다.

        return displayJson(true, "1000", sMessage);
    }
    

    /*************************************************************
     * Modules
     *************************************************************/

    /**
     * datepicker 유효성 검사
     *
     * @param searchDto
     * @return
     */
    private Boolean dateValidator(SearchDto searchDto) {

        String searchStartDate = searchDto.getSearchStartDate(); // 검색 시작일 가져오기
        String searchEndDate = searchDto.getSearchEndDate(); // 검색 종료일 가져오기

        // 검색 시작일 & 검색 종료일 공백 제거
        searchDto.setSearchStartDate(searchStartDate.trim());
        searchDto.setSearchEndDate(searchEndDate.trim());

        // 검색 시작일 & 검색 종료일 -> 빈값 가능
        if (searchStartDate.isEmpty() && searchEndDate.isEmpty()) {
            return Boolean.TRUE;
        }

        // String -> 날짜 타입으로 변환
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false); // 입력한 값이 잘못된 형식일 경우 오류 발생

            // 날짜 형식 변환
            Date startDate = sdf.parse(searchDto.getSearchStartDate());
            Date endDate = sdf.parse(searchDto.getSearchEndDate());

            // 검색 시작일이 검색 종료일을 넘길 경우 false 리턴
            if (startDate != null && startDate.compareTo(endDate) > 0) {
                return Boolean.FALSE;
            }

            // 검색 종료일이 검색 시작일보다 앞설 경우 false 리턴
            if (endDate != null && endDate.compareTo(startDate) < 0) {
                return Boolean.FALSE;
            }

            /** 데이터 누락 방지용 시분초 추가 **/
            // 검색 시작일 시분초 추가 -> 해당 일자의 00시 00분 00초부터 검색하도록 설정
            searchDto.setSearchStartDate(searchStartDate + " 00:00:00");

            // 검색 종료일 시분초 추가 -> 해당 일자의 23시 59분 59초까지 검색하도록 설정
            searchDto.setSearchEndDate(searchEndDate + " 23:59:59");

        } catch (ParseException e) {
            return Boolean.FALSE;
        }

        // 이상 없으면 true 리턴
        return Boolean.TRUE;
    }

    /**
     * 검색 조건 유효성 검사
     *
     * @param searchDto
     * @return
     */
    private void isValidSearchDateType(SearchDto searchDto) {

        // 검색 조건을 설정하지 않고 검색어를 입력한 경우
        if (searchDto.getSearchType().isEmpty() && !searchDto.getSearchWord().isEmpty()) {
            throw new CustomException(CustomError.SEARCH_TYPE_ERROR); // 검색 조건을 설정해주세요.
        }

        // 검색 조건을 설정하지 않고 검색날짜를 입력한 경우
        if (searchDto.getSearchDateType().isEmpty()) {
            if (!searchDto.getSearchStartDate().isEmpty() || !searchDto.getSearchEndDate().isEmpty()) {
                throw new CustomException(CustomError.SEARCH_TYPE_ERROR); // 검색 조건을 설정해주세요.
            }
        }
    }
}
