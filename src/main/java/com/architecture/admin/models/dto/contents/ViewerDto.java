package com.architecture.admin.models.dto.contents;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ViewerDto {

    private Integer idx;
    // episode idx
    private Integer episodeIdx;
    // 소설 내용
    private String detail;
    // 순서
    private Integer sort;
    // 상태값
    private Integer state;
    // 등록일
    private String regdate;
    // 카테고리 idx
    private Integer categoryIdx;

}
