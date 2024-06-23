package com.architecture.admin.models.dto.menu;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/*****************************************************
 * WEBTOON 회원
 ****************************************************/
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class MenuDto {
    // base attribute
    private Integer idx;
    private Integer menuIdx;
    private Integer parent;
    private Integer sort;
    private Integer level;
    private Integer state;

    private String name;
    private String link;
    private String lang;
    private String regdate;

    // sql
    private Integer insertedIdx;
    private Integer affectedRow;
    private Integer menuInsertedName;
    private Integer menuAffectedRow;

}