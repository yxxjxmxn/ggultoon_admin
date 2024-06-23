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
@ExcelFileName(filename = "lang.report.episode.comment.title.list")
public class ReportEpisodeCommentDto implements ExcelDto {

    /**
     * episode_comment_report
     **/
    @ExcelColumnName(headerName = "lang.report.episode.comment.idx")
    private Long idx; // 회차 댓글 신고 내역 idx
    private Integer state; // 신고 상태값 (0:취소 / 1:정상)
    @ExcelColumnName(headerName = "lang.report.episode.comment.state")
    private String stateText; // 신고 상태 문자 변환
    private String stateBg; // 신고 상태 bg 색상
    private Long reporterIdx; // 신고 회원 idx
    private Long writerIdx; // 작성 회원 idx
    @ExcelColumnName(headerName = "lang.report.episode.comment.reporter.id")
    private String reporterId; // 신고 회원 아이디
    @ExcelColumnName(headerName = "lang.report.episode.comment.reporter.nick")
    private String reporterNick; // 신고 회원 닉네임
    private Integer type; // 댓글 OR 대댓글 구분값
    @ExcelColumnName(headerName = "lang.report.episode.comment.type")
    private String typeText; // 댓글 OR 대댓글 구분값 문자 변환
    private String typeBg; // 댓글 OR 대댓글 구분값 bg 색상
    private Integer status; // 댓글 상태값 (0:삭제 / 1:정상)
    @ExcelColumnName(headerName = "lang.report.episode.comment.status")
    private String statusText; // 댓글 상태 문자 변환
    private String statusBg; // 댓글 상태 bg 색상
    private Integer view; // 댓글 노출 상태값 (0:비노출 / 1:노출)
    @ExcelColumnName(headerName = "lang.report.episode.comment.view")
    private String viewText; // 댓글 노출 상태 문자 변환
    private String viewBg; // 댓글 노출 상태 bg 색상
    @ExcelColumnName(headerName = "lang.report.episode.comment.content")
    private String content; // 신고 댓글 내용
    @ExcelColumnName(headerName = "lang.report.episode.comment.writer.id")
    private String writerId; // 작성 회원 아이디
    @ExcelColumnName(headerName = "lang.report.episode.comment.writer.nick")
    private String writerNick; // 작성 회원 닉네임
    @ExcelColumnName(headerName = "lang.report.episode.comment.regdate")
    private String regdate; // 신고일(UTC)
    private String regdateTz; // 신고일 타임존

    @Override
    public List<String> mapToList() {
        return Arrays.asList(
                String.valueOf(idx),
                stateText,
                reporterId,
                reporterNick,
                typeText,
                statusText,
                viewText,
                content,
                writerId,
                writerNick,
                regdate);
    }
}
