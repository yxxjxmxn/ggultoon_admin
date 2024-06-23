package com.architecture.admin.models.dto.member;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberGradeDto {

    // member.idx
    private Long idx;
    // 결제 금액
    private Integer amount;
    // 등급
    private Integer grade;
    // 결제 추가 마일리지(%)
    private Integer addMileage;
    // 작품 구매 페이백(%)
    private Integer payback;
    // 등급 최소 금액
    private Integer condition;
}
