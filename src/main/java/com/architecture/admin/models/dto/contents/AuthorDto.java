package com.architecture.admin.models.dto.contents;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthorDto {
    private Integer idx;
    // 컨텐츠 idx
    private Integer contentsIdx;
    // 작가명
    private String name;
    // 1:글, 2:그림
    private Integer type;

    // 상태값
    private Integer state;
    // 등록일
    private String regdate;
}
