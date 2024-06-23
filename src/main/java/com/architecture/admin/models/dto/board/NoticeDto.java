package com.architecture.admin.models.dto.board;

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
@ExcelFileName(filename = "lang.board.notice.title.list")
public class NoticeDto implements ExcelDto {

    @ExcelColumnName(headerName = "lang.board.notice.idx")
    private Long idx;
    private Integer state; // 상태값
    @ExcelColumnName(headerName = "lang.board.notice.state")
    private String stateText; // 상태값 문자 변환
    private String stateBg; // 상태값 배지
    private Integer type; // 타입 구분값
    @ExcelColumnName(headerName = "lang.board.notice.type")
    private String typeText; // 타입 구분값 문자 변환
    private Integer mustRead; // 필독 상태값
    @ExcelColumnName(headerName = "lang.board.notice.mustRead")
    private String mustReadText; // 필독 상태값 문자 변환
    private String mustReadBg; // 필독 상태값 배지
    @ExcelColumnName(headerName = "lang.board.notice.title")
    private String title; // 공지 제목
    @ExcelColumnName(headerName = "lang.board.notice.content")
    private String content; // 공지 내용
    @ExcelColumnName(headerName = "lang.board.notice.regdate")
    private String regdate; // 등록일
    private Integer noticeCnt; // 공지사항 개수

    // sql
    private Long insertedIdx;

    @Override
    public List<String> mapToList() {
        return Arrays.asList(
                String.valueOf(idx),
                stateText,
                mustReadText,
                title,
                content,
                regdate);
    }
}
