package com.architecture.admin.models.dto.contents;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CodeDto {

    private Integer idx;
    // 컨텐츠 idx
    private Integer contentsIdx;
    // 코드 idx
    private Integer codeIdx;
    // 코드 이름
    private String code;
    // 상태값
    private Integer state;
    // 등록일
    private String regdate;

}
