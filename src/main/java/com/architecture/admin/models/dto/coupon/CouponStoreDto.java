package com.architecture.admin.models.dto.coupon;

import com.architecture.admin.libraries.excel.ExcelColumnName;
import com.architecture.admin.libraries.excel.ExcelFileName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.architecture.admin.models.dto.excel.ExcelDto;
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
@ExcelFileName(filename = "lang.coupon.store.title.list")
public class CouponStoreDto implements ExcelDto {

    @ExcelColumnName(headerName = "lang.coupon.store.idx")
    private Long idx; // 쿠폰 업체 번호
    private Integer state; // 쿠폰 업체 상태
    @ExcelColumnName(headerName = "lang.coupon.store.state")
    private String stateText; // 쿠폰 업체 상태 문자변환
    private String stateBg; // 쿠폰 업체 상태 배지
    @ExcelColumnName(headerName = "lang.coupon.store.name")
    private String name; // 쿠폰 업체 이름
    @ExcelColumnName(headerName = "lang.coupon.store.manager")
    private String manager; // 쿠폰 업체 담당자
    @ExcelColumnName(headerName = "lang.coupon.store.manager.phone")
    private String phone; // 쿠폰 업체 담당자 연락처
    private Integer type; // 쿠폰 업체 유형(1:온라인, 2:오프라인)
    @ExcelColumnName(headerName = "lang.coupon.store.type")
    private String typeText; // 쿠폰 업체 유형 문자변환
    private String typeBg; // 쿠폰 업체 유형 배지
    @ExcelColumnName(headerName = "lang.coupon.store.regdate")
    private String regdate; // 등록일
    private String regdateTz; // 등록일 타임존

    /** sql **/
    private Long insertedIdx;

    @Override
    public List<String> mapToList() {
        return Arrays.asList(
                String.valueOf(idx),
                stateText,
                name,
                manager,
                phone,
                typeText,
                regdate);
    }
}
