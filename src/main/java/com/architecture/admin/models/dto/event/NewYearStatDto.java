package com.architecture.admin.models.dto.event;

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
@ExcelFileName(filename = "lang.event.title.newyear.stat")
public class NewYearStatDto implements ExcelDto {

    @ExcelColumnName(headerName = "lang.event.idx")
    private Long idx; // 번호
    @ExcelColumnName(headerName = "lang.event.date")
    private String date; // 날짜
    @ExcelColumnName(headerName = "lang.contents.contentsIdx")
    private Integer contentsIdx; // 작품 idx
    @ExcelColumnName(headerName = "lang.contents.title")
    private String title; // 작품 제목
    private Integer pavilion; // 이용관
    @ExcelColumnName(headerName = "lang.contents.pavilion")
    private String pavilionText; // 이용관 문자 변환
    private String pavilionBg; // 이용관 배지
    private Integer adult; // 성인작 여부
    @ExcelColumnName(headerName = "lang.contents.age.adult")
    private String adultText; // 성인작 여부 문자 변환
    private String adultBg; // 성인작 여부 배지
    @ExcelColumnName(headerName = "lang.contents.category")
    private String category; // 카테고리
    @ExcelColumnName(headerName = "lang.contents.genre")
    private String genre; // 장르
    @ExcelColumnName(headerName = "lang.event.view.cnt")
    private Integer viewCnt; // 조회수

    /** sql **/
    private Long insertedIdx;

    @Override
    public List<String> mapToList() {
        return Arrays.asList(
                String.valueOf(idx),
                date,
                String.valueOf(contentsIdx),
                title,
                pavilionText,
                adultText,
                category,
                genre,
                String.valueOf(viewCnt)
        );
    }
}
