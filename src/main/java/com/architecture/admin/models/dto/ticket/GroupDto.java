package com.architecture.admin.models.dto.ticket;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class GroupDto {

    private Long idx;
    private Integer state; // 상태값
    private String stateText; // 상태값 문자 변환
    private String stateBg; // 상태값 배지
    private String name; // 그룹 이름
    private String code; // 그룹 코드
    private String description; // 그룹 설명
    private String regdate; // 등록일
    private String regdateTz; // 등록일 타임존
}
