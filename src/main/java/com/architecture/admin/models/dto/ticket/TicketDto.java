package com.architecture.admin.models.dto.ticket;

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
@ExcelFileName(filename = "lang.ticket.complete.title.list")
public class TicketDto implements ExcelDto {

    @ExcelColumnName(headerName = "lang.ticket.idx")
    private Long idx;
    private Integer state; // 상태값
    private String stateText; // 상태값 문자 변환
    private String stateBg; // 상태값 배지
    private Long groupIdx; // 그룹 idx
    @ExcelColumnName(headerName = "lang.ticket.group.name")
    private String name; // 그룹 이름
    private String codeIdx; // 그룹 코드 idx
    @ExcelColumnName(headerName = "lang.ticket.group.code")
    private String code; // 그룹 코드
    @ExcelColumnName(headerName = "lang.contents.category")
    private String category; // 작품 카테고리
    @ExcelColumnName(headerName = "lang.contents.genre")
    private String genre; // 작품 장르
    @ExcelColumnName(headerName = "lang.contents.contentsIdx")
    private Integer contentsIdx; // 작품 IDX
    @ExcelColumnName(headerName = "lang.contents.title")
    private String title; // 작품 제목
    private Integer sellType; // 작품 판매 유형
    private Integer adult; // 지급 연령 성인 여부
    @ExcelColumnName(headerName = "lang.ticket.group.age")
    private String adultText; // 지급 연령 성인 여부 문자 변환
    private String adultBg; // 지급 연령 성인 여부 배지
    private Integer count; // 이용권 개수
    private Integer except; // 이용권 지급 제외 회차 수
    private Integer period; // 이용권 유효기간
    @ExcelColumnName(headerName = "lang.ticket.give.count")
    private double giveCnt; // 이용권 발급 개수
    @ExcelColumnName(headerName = "lang.ticket.use.count")
    private double useCnt; // 이용권 사용 개수
    @ExcelColumnName(headerName = "lang.ticket.use.percent")
    private double usePercent; // 이용권 사용량
    @ExcelColumnName(headerName = "lang.ticket.startDate")
    private String startDate; // 유효기간 시작일
    private String startDateTz; // 유효기간 시작일 타임존
    @ExcelColumnName(headerName = "lang.ticket.endDate")
    private String endDate; // 유효기간 종료일
    private String endDateTz; // 유효기간 종료일 타임존
    @ExcelColumnName(headerName = "lang.ticket.regdate")
    private String regdate; // 등록일
    private String regdateTz; // 등록일 타임존

    /** sql **/
    private Long insertedIdx;

    @Override
    public List<String> mapToList() {
        return Arrays.asList(
                String.valueOf(idx),
                name,
                code,
                category,
                genre,
                String.valueOf(contentsIdx),
                title,
                adultText,
                String.valueOf((int)giveCnt),
                String.valueOf((int)useCnt),
                String.valueOf(usePercent),
                startDate,
                endDate,
                regdate);
    }
}
