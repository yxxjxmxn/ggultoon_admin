package com.architecture.admin.models.dto.contents;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenreDto {

    private Integer idx;

    private Integer categoryIdx;
    // 순서
    private Integer sort;
    // 장르명
    private String name;
    // 상태값
    private Integer state;
    // 등록일
    private String regdate;

}
