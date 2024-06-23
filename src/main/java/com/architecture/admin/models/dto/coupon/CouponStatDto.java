package com.architecture.admin.models.dto.coupon;

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
@ExcelFileName(filename = "lang.coupon.stat.title.list")
public class CouponStatDto implements ExcelDto {

    private Long idx; // 번호
    @ExcelColumnName(headerName = "lang.coupon.stat.date")
    private String date; // 날짜
    @ExcelColumnName(headerName = "lang.coupon.stat.register")
    private double registerCnt; // 등록수
    @ExcelColumnName(headerName = "lang.coupon.stat.new")
    private double newCnt; // 신규수
    @ExcelColumnName(headerName = "lang.coupon.stat.old")
    private double oldCnt; // 기존수
    @ExcelColumnName(headerName = "lang.coupon.stat.re-register")
    private double reRegisterCnt; // 재등록수
    @ExcelColumnName(headerName = "lang.coupon.stat.out")
    private double outCnt; // 탈퇴수
    @ExcelColumnName(headerName = "lang.coupon.stat.new.percent")
    private double newPercent; // 신규율
    @ExcelColumnName(headerName = "lang.coupon.stat.out.percent")
    private double outPercent; // 탈퇴율
    @ExcelColumnName(headerName = "lang.coupon.stat.new.payment.cnt")
    private double newPaymentCnt; // 신규결제수
    @ExcelColumnName(headerName = "lang.coupon.stat.new.payment.price")
    private Integer newPaymentPrice; // 신규결제액
    @ExcelColumnName(headerName = "lang.coupon.stat.old.payment.cnt")
    private double oldPaymentCnt; // 기존결제수
    @ExcelColumnName(headerName = "lang.coupon.stat.old.payment.price")
    private Integer oldPaymentPrice; // 기존결제액
    @ExcelColumnName(headerName = "lang.coupon.stat.payment.percent")
    private double paymentPercent; // 결제율

    /** sql **/
    private Long insertedIdx;

    @Override
    public List<String> mapToList() {
        return Arrays.asList(
                date,
                String.valueOf(registerCnt),
                String.valueOf(newCnt),
                String.valueOf(oldCnt),
                String.valueOf(reRegisterCnt),
                String.valueOf(outCnt),
                String.valueOf(newPercent),
                String.valueOf(outPercent),
                String.valueOf(newPaymentCnt),
                String.valueOf(newPaymentPrice),
                String.valueOf(oldPaymentCnt),
                String.valueOf(oldPaymentPrice),
                String.valueOf(paymentPercent)
        );
    }
}
