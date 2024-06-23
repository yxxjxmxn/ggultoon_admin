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
@ExcelFileName(filename = "lang.coupon.title.list")
public class CouponDto implements ExcelDto {

    @ExcelColumnName(headerName = "lang.coupon.idx")
    private Long idx; // 쿠폰 번호
    private Long storeIdx; // 쿠폰 업체 번호
    @ExcelColumnName(headerName = "lang.coupon.progress")
    private String progressText; // 쿠폰 지급 상태 문자변환
    private String progressBg; // 쿠폰 지급 상태 배지
    private Integer state; // 쿠폰 상태
    @ExcelColumnName(headerName = "lang.coupon.state")
    private String stateText; // 쿠폰 상태 문자변환
    private String stateBg; // 쿠폰 상태 배지
    @ExcelColumnName(headerName = "lang.coupon.title")
    private String title; // 쿠폰 이름
    @ExcelColumnName(headerName = "lang.coupon.code")
    private String code; // 쿠폰 코드
    private Integer duplication; // 쿠폰 중복 여부(0:미중복, 1:중복)
    @ExcelColumnName(headerName = "lang.coupon.duplication")
    private String duplicationText; // 쿠폰 중복 여부 문자변환
    private String duplicationBg; // 쿠폰 중복 여부 배지
    private Integer type; // 쿠폰 종류(1:한글, 2:영어, 3:한글+난수, 4:영어+난수)
    @ExcelColumnName(headerName = "lang.coupon.type")
    private String typeText; // 쿠폰 종류 문자변환
    @ExcelColumnName(headerName = "lang.coupon.count")
    private Integer couponCnt; // 발급 쿠폰 개수
    @ExcelColumnName(headerName = "lang.coupon.give.mileage")
    private Integer mileage; // 지급 마일리지
    @ExcelColumnName(headerName = "lang.coupon.mileage.period")
    private Integer period; // 마일리지 유효일
    @ExcelColumnName(headerName = "lang.coupon.count.total")
    private Integer totalCoupon; // 총 발급 쿠폰 개수
    @ExcelColumnName(headerName = "lang.coupon.mileage.total")
    private Integer totalMileage; // 총 지급 마일리지
    @ExcelColumnName(headerName = "lang.coupon.store.name")
    private String name; // 쿠폰 업체 이름
    private Integer storeType; // 쿠폰 업체 유형(1:온라인, 2:오프라인)
    @ExcelColumnName(headerName = "lang.coupon.store.type")
    private String storeTypeText; // 쿠폰 업체 유형 문자변환
    private String storeTypeBg; // 쿠폰 업체 유형 배지
    @ExcelColumnName(headerName = "lang.coupon.start.date")
    private String startDate; // 유효기간 시작일
    private String startDateTz; // 유효기간 시작일 타임존
    @ExcelColumnName(headerName = "lang.coupon.end.date")
    private String endDate; // 유효기간 종료일
    private String endDateTz; // 유효기간 종료일 타임존
    @ExcelColumnName(headerName = "lang.coupon.regdate")
    private String regdate; // 등록일
    private String regdateTz; // 등록일 타임존

    /** sql **/
    private Long insertedIdx;

    @Override
    public List<String> mapToList() {
        return Arrays.asList(
                String.valueOf(idx),
                progressText,
                stateText,
                title,
                code,
                duplicationText,
                typeText,
                String.valueOf(couponCnt),
                String.valueOf(mileage),
                String.valueOf(period),
                String.valueOf(totalCoupon),
                String.valueOf(totalMileage),
                name,
                storeTypeText,
                startDate,
                endDate,
                regdate);
    }
}
