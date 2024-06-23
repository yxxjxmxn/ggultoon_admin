package com.architecture.admin.models.dto.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PromotionDto {
    private Long idx;
    private Integer contentsIdx;        // 컨텐츠 Idx
    private Integer episodeCnt;         // 프로모션 회차 수
    private Integer coin;               // 대여 코인
    private Integer coinRent;           // 소장 코인
    private Integer state;              // 상태값
    private Integer used;               // 적용 여부
    private String promotionStartDate;  // 프모모션 시작일
    private String promotionEndDate;    // 프로모션 종료일
    private String regdate;             // 등록일

    private Long episodeIdx;            // 회차 idx

}
