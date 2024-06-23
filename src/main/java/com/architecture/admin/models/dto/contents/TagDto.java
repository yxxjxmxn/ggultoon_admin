package com.architecture.admin.models.dto.contents;

import com.architecture.admin.libraries.excel.ExcelColumnName;
import com.architecture.admin.libraries.excel.ExcelFileName;
import com.architecture.admin.models.dto.excel.ExcelDto;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@ExcelFileName(filename = "lang.contents.tag.title.list")
public class TagDto  implements ExcelDto {

    @ExcelColumnName(headerName = "lang.contents.tag.idx")
    private Integer idx;
    private Integer tagIdx; // 태그 idx
    private Integer contentsIdx; // 컨텐츠 idx
    private Integer state; // 상태값
    @ExcelColumnName(headerName = "lang.contents.tag.state")
    private String stateText; // 상태값 문자 변환
    private String stateBg; // 상태값 배지
    @ExcelColumnName(headerName = "lang.contents.tag.group")
    private Integer tagGroupIdx; // 태그 그룹 번호
    @ExcelColumnName(headerName = "lang.contents.tag.name")
    private String name; // 태그 이름

    @ExcelColumnName(headerName = "lang.contents.tag.regdate")
    private String regdate; // 등록일

    @Override
    public List<String> mapToList() {
        return Arrays.asList(
                String.valueOf(idx),
                stateText,
                String.valueOf(tagGroupIdx),
                name,
                regdate);
    }
}
