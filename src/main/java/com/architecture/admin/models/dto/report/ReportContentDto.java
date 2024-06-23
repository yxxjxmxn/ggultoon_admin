package com.architecture.admin.models.dto.report;

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
@ExcelFileName(filename = "lang.report.content.title.list")
public class ReportContentDto implements ExcelDto {

    /**
     * contents_report
     **/
    @ExcelColumnName(headerName = "lang.report.content.idx")
    private Long idx; // 작품 신고 내역 idx
    private Integer state; // 신고 상태값 (0:취소 / 1:정상)
    @ExcelColumnName(headerName = "lang.report.content.state")
    private String stateText; // 신고 상태 문자 변환
    private String stateBg; // 신고 상태 bg 색상
    private Long memberIdx; // 신고 회원 idx
    @ExcelColumnName(headerName = "lang.report.content.id")
    private String id; // 신고 회원 아이디
    @ExcelColumnName(headerName = "lang.report.content.nick")
    private String nick; // 신고 회원 닉네임
    private Integer contentsIdx; // 신고 작품 idx
    private Integer status; // 작품 상태값 (0:삭제 / 1:정상)
    @ExcelColumnName(headerName = "lang.report.content.status")
    private String statusText; // 작품 상태 문자 변환
    private String statusBg; // 작품 상태 bg 색상
    @ExcelColumnName(headerName = "lang.report.content.title")
    private String title; // 작품 제목
    @ExcelColumnName(headerName = "lang.report.content.regdate")
    private String regdate; // 신고일(UTC)
    private String regdateTz; // 신고일 타임존

    @Override
    public List<String> mapToList() {
        return Arrays.asList(
                String.valueOf(idx),
                stateText,
                id,
                nick,
                statusText,
                title,
                regdate);
    }
}
