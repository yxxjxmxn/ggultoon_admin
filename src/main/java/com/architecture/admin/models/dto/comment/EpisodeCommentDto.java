package com.architecture.admin.models.dto.comment;

import com.architecture.admin.libraries.excel.ExcelColumnName;
import com.architecture.admin.libraries.excel.ExcelFileName;
import com.architecture.admin.models.dto.contents.ImageDto;
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
@ExcelFileName(filename = "lang.comment.episode.title.list")
public class EpisodeCommentDto implements ExcelDto {

    /**
     * episode_comment
     **/
    @ExcelColumnName(headerName = "lang.comment.idx")
    private Long idx; // 회차 댓글 idx
    private Long episodeIdx; // 회차 idx
    private Integer contentsIdx; // 작품 idx
    private Long writerIdx; // 작성자 idx
    @ExcelColumnName(headerName = "lang.comment.writer.id")
    private String writerId; // 작성자 아이디
    @ExcelColumnName(headerName = "lang.comment.writer.nick")
    private String writerNick; // 작성자 닉네임
    private Long groupIdx; // 댓글 그룹 번호
    private Long parentIdx; // 부모 댓글 idx
    @ExcelColumnName(headerName = "lang.comment.parent.type")
    private String typeText; // 댓글 OR 대댓글 구분값 문자 변환
    private String typeBg; // 댓글 OR 대댓글 구분값 bg 색상
    private Integer state; // 댓글 상태값 (0:삭제 / 1:정상)
    @ExcelColumnName(headerName = "lang.comment.state")
    private String stateText; // 댓글 상태 문자 변환
    private String stateBg; // 댓글 상태 bg 색상
    private Integer view; // 댓글 노출 상태값 (0:비노출 / 1:노출)
    @ExcelColumnName(headerName = "lang.comment.view")
    private String viewText; // 댓글 노출 상태 문자 변환
    private String viewBg; // 댓글 노출 상태 bg 색상
    @ExcelColumnName(headerName = "lang.comment.content")
    private String content; // 댓글 내용
    @ExcelColumnName(headerName = "lang.comment.like_cnt")
    private Integer likeCnt; // 댓글 좋아요 개수
    private Integer commentCnt; // 댓글 개수
    @ExcelColumnName(headerName = "lang.comment.regdate")
    private String regdate; // 등록일(UTC)
    private String regdateTz; // 등록일 타임존
    @ExcelColumnName(headerName = "lang.comment.modifydate")
    private String modifyDate; // 수정일(UTC)
    private String modifyDateTz; // 수정일 타임존

    /**
     * episodes
     **/
    private Integer episodeNumber; // 회차 번호
    private Integer sort; // 회차 순서
    private String title; // 회차 제목
    private String pubdate; // 발행일(UTC)
    private String pubdateTz; // 발행일 타임존


    /** list */
    private List<ImageDto> imageList; // 이미지 리스트

    @Override
    public List<String> mapToList() {
        return Arrays.asList(
                String.valueOf(idx),
                writerId,
                writerNick,
                typeText,
                stateText,
                viewText,
                content,
                String.valueOf(likeCnt),
                regdate,
                modifyDate);
    }
}
