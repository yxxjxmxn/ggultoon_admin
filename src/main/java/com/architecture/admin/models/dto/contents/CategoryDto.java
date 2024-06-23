package com.architecture.admin.models.dto.contents;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryDto {

    private Integer idx;
    // 카테고리 이름
    private String name;
    // 상태값
    private Integer state;
    // 등록일
    private String regdate;


    // 장르 목록
    private List<GenreDto> genreList;

}
