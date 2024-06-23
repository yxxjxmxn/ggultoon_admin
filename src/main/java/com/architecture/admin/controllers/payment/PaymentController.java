package com.architecture.admin.controllers.payment;

import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.models.dto.SearchDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Controller
@RequestMapping("/payments")
public class PaymentController extends BaseController {

    /**************************************************************************************
     * 결제 내역
     **************************************************************************************/

    /**
     * 결제 내역 리스트
     * @param searchDto
     * @return
     */
    @GetMapping()
    public String paymentList(Model model,
                        @RequestParam(required = false, defaultValue = "1") int page,
                        @ModelAttribute SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(10);

        model.addAttribute("page", page);
        model.addAttribute("search", searchDto);

        hmImportFile.put("importJs", "payment/list.js.html");

        return "payment/list";
    }

    /**************************************************************************************
     * 결제 수단
     **************************************************************************************/

    /**
     * 결제 수단 목록
     * @param searchDto
     * @return
     */
    @GetMapping("/methods")
    public String methodList(Model model,
                           @RequestParam(required = false, defaultValue = "1") int page,
                           @ModelAttribute SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(25);

        model.addAttribute("page", page);
        model.addAttribute("search", searchDto);

        hmImportFile.put("importJs", "payment/method/list.js.html");

        return "payment/method/list";
    }

    /**
     * 결제 수단 상세
     * @param idx
     * @return
     */
    @GetMapping("/methods/{idx}")
    public String viewPaymentMethod(@PathVariable(required = false) Long idx, Model model) {

        // 관리자 접근 권한
        super.adminAccessFail(25);

        model.addAttribute("idx", idx);

        hmImportFile.put("importJs", "payment/method/view.js.html");

        return "payment/method/view";
    }

    /**
     * 결제 수단 등록
     */
    @GetMapping("/methods/register")
    public String registerPaymentMethod() {

        // 관리자 접근 권한
        super.adminAccessFail(25);

        // 결제 방식 리스트
        HashMap<Integer, String> autoPayType = new HashMap<>();
        autoPayType.put(0,"일반결제");
        autoPayType.put(1,"자동결제");

        // set view data
        hmDataSet.put("autoPayType", autoPayType);

        hmImportFile.put("importJs", "payment/method/register.js.html");

        return "payment/method/register";
    }

    /**
     * 결제 수단 수정
     */
    @GetMapping("/methods/modify/{idx}")
    public String modifyPaymentMethod(@PathVariable(required = false) Long idx, Model model) {

        // 관리자 접근 권한
        super.adminAccessFail(25);

        model.addAttribute("idx", idx);

        // 결제 방식 리스트
        HashMap<Integer, String> autoPayType = new HashMap<>();
        autoPayType.put(0,"일반결제");
        autoPayType.put(1,"자동결제");

        // set view data
        hmDataSet.put("autoPayType", autoPayType);

        hmImportFile.put("importJs", "payment/method/modify.js.html");

        return "payment/method/modify";
    }

    /**************************************************************************************
     * 결제 상품
     **************************************************************************************/

    /**
     * 결제 상품 목록
     * @param searchDto
     * @return
     */
    @GetMapping("/products")
    public String productList(Model model,
                             @RequestParam(required = false, defaultValue = "1") int page,
                             @ModelAttribute SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(26);

        model.addAttribute("page", page);
        model.addAttribute("search", searchDto);

        hmImportFile.put("importJs", "payment/product/list.js.html");

        return "payment/product/list";
    }

    /**
     * 결제 상품 상세
     * @param idx
     * @return
     */
    @GetMapping("/products/{idx}")
    public String viewPaymentProduct(@PathVariable(required = false) Long idx, Model model) {

        // 관리자 접근 권한
        super.adminAccessFail(26);

        model.addAttribute("idx", idx);

        hmImportFile.put("importJs", "payment/product/view.js.html");

        return "payment/product/view";
    }

    /**
     * 결제 상품 등록
     */
    @GetMapping("/products/register")
    public String registerPaymentProduct() {

        // 관리자 접근 권한
        super.adminAccessFail(26);

        // 결제 상품 구분 리스트
        HashMap<String, String> productType = new HashMap<>();
        productType.put("1", "첫결제");
        productType.put("2", "재결제");
        productType.put("3", "장기미결제");

        // set view data
        hmDataSet.put("productType", productType);

        hmImportFile.put("importJs", "payment/product/register.js.html");

        return "payment/product/register";
    }

    /**
     * 결제 상품 수정
     */
    @GetMapping("/products/modify/{idx}")
    public String modifyPaymentProducts(@PathVariable(required = false) Long idx, Model model) {

        // 관리자 접근 권한
        super.adminAccessFail(26);

        model.addAttribute("idx", idx);

        // 결제 상품 구분 리스트
        HashMap<String, String> productType = new HashMap<>();
        productType.put("1", "첫결제");
        productType.put("2", "재결제");
        productType.put("3", "장기미결제");

        // set view data
        hmDataSet.put("productType", productType);

        hmImportFile.put("importJs", "payment/product/modify.js.html");

        return "payment/product/modify";
    }
}
