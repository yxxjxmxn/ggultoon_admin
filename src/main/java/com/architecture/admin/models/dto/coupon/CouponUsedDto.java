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
@ExcelFileName(filename = "lang.coupon.use.title.list")
public class CouponUsedDto implements ExcelDto {

    @ExcelColumnName(headerName = "lang.coupon.use.idx")
    private Long idx; // 사용 내역 번호
    private Long storeIdx; // 쿠폰 업체 번호
    private Integer state; // 사용 내역 상태
    private Integer type; // 회원 구분(1: 신규, 2:기존)
    @ExcelColumnName(headerName = "lang.coupon.use.member")
    private String typeText; // 회원 구분 정보 문자변환
    private String typeBg; // 회원 구분 정보 배지
    @ExcelColumnName(headerName = "lang.coupon.use.member.idx")
    private Long memberIdx; // 회원 번호
    @ExcelColumnName(headerName = "lang.coupon.use.member.id")
    private String memberId; // 회원 아이디
    @ExcelColumnName(headerName = "lang.coupon.use.member.route")
    private String routeText; // 접속 기기 정보 문자변환
    private String routeBg; // 접속 기기 정보 배지
    @ExcelColumnName(headerName = "lang.coupon.number")
    private Long couponIdx; // 쿠폰 번호
    private Integer storeType; // 쿠폰 업체 유형(1:온라인, 2:오프라인)
    @ExcelColumnName(headerName = "lang.coupon.store.type")
    private String storeTypeText; // 쿠폰 업체 유형 문자변환
    private String storeTypeBg; // 쿠폰 업체 유형 배지
    @ExcelColumnName(headerName = "lang.coupon.store.name")
    private String storeName; // 쿠폰 업체 이름
    @ExcelColumnName(headerName = "lang.coupon.give.mileage")
    private Integer mileage; // 지급 마일리지
    @ExcelColumnName(headerName = "lang.coupon.use.mileage")
    private Integer usedMileage; // 사용 마일리지
    @ExcelColumnName(headerName = "lang.coupon.rest.mileage")
    private Integer restMileage; // 잔여 마일리지
    @ExcelColumnName(headerName = "lang.coupon.use.member.ip")
    private String memberIp; // 사용 IP
    @ExcelColumnName(headerName = "lang.coupon.use.member.area")
    private String memberArea; // 접속 지역
    @ExcelColumnName(headerName = "lang.coupon.use.regdate")
    private String regdate; // 사용일
    private String regdateTz; // 사용일 타임존
    @ExcelColumnName(headerName = "lang.coupon.use.expiredate")
    private String expiredate; // 만료일
    private String expiredateTz; // 만료일 타임존

    /** sql **/
    private Long insertedIdx;

    @Override
    public List<String> mapToList() {
        return Arrays.asList(
                String.valueOf(idx),
                typeText,
                String.valueOf(memberIdx),
                memberId,
                routeText,
                String.valueOf(couponIdx),
                storeTypeText,
                storeName,
                String.valueOf(mileage),
                String.valueOf(usedMileage),
                String.valueOf(restMileage),
                memberIp,
                memberArea,
                regdate,
                expiredate);
    }
}
