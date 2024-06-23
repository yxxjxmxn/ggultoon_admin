package com.architecture.admin.models.dto.payment;

import com.architecture.admin.libraries.excel.ExcelColumnName;
import com.architecture.admin.libraries.excel.ExcelFileName;
import com.architecture.admin.models.dto.excel.ExcelDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@ExcelFileName(filename = "lang.payment.product.title.list")
public class PaymentProductDto implements ExcelDto {

    /**
     * product
     **/
    @ExcelColumnName(headerName = "lang.payment.product.idx")
    private Integer idx; // 결제 상품 idx
    private Integer state; // 사용 상태값 (0:미사용 / 1:사용)
    @ExcelColumnName(headerName = "lang.payment.product.state")
    private String stateText; // 사용 상태 문자 변환
    private String stateBg; // 사용 상태 bg 색상
    @ExcelColumnName(headerName = "lang.payment.product.title")
    private String title; // 상품명
    private Integer type; // 상품 구분(첫결제, 재결제, 장기미결제)
    @ExcelColumnName(headerName = "lang.payment.product.type")
    private String typeText; // 상품 구분(첫결제, 재결제, 장기미결제) 문자변환
    @ExcelColumnName(headerName = "lang.payment.product.coin")
    private Integer coin;  // 유료 코인
    @ExcelColumnName(headerName = "lang.payment.product.coinFree")
    private Integer coinFree;  // 보너스 코인(무료)
    @ExcelColumnName(headerName = "lang.payment.product.coinFree2")
    private Integer coinFree2;  // 보너스 코인2(무료)
    @ExcelColumnName(headerName = "lang.payment.product.mileage")
    private Integer mileage;  // 마일리지
    @ExcelColumnName(headerName = "lang.payment.product.coinTotalCnt")
    private Integer coinTotalCnt;  // 코인 합계
    @ExcelColumnName(headerName = "lang.payment.product.price")
    private Integer price;  // 결제 금액
    @ExcelColumnName(headerName = "lang.payment.product.sort")
    private Integer sort;  // 상품 순서
    @ExcelColumnName(headerName = "lang.payment.product.regdate")
    private String regdate; // 등록일(UTC)
    private String regdateTz; // 등록일 타임존

    @Override
    public List<String> mapToList() {
        return Arrays.asList(
                String.valueOf(idx),
                stateText,
                title,
                typeText,
                String.valueOf(coin),
                String.valueOf(coinFree),
                String.valueOf(coinFree2),
                String.valueOf(mileage),
                String.valueOf(price),
                String.valueOf(sort),
                regdate);
    }
}
