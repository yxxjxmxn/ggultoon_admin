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
@ExcelFileName(filename = "lang.comment.content.reply.title.list")
public class ContentReplyDto implements ExcelDto {

    /**
     * contents_comment
     **/
    private Integer contentsIdx; // 작품 idx

    @ExcelColumnName(headerName = "lang.comment.idx")
    private Long idx; // 작품 댓글 idx
    private Long writerIdx; // 작성자 idx
    @ExcelColumnName(headerName = "lang.comment.writer.id")
    private String writerId; // 작성자 아이디
    @ExcelColumnName(headerName = "lang.comment.writer.nick")
    private String writerNick; // 작성자 닉네임
    @ExcelColumnName(headerName = "lang.comment.parent.type")
    private String typeText; // 댓글 OR 대댓글 구분값 문자 변환
    private String typeBg; // 댓글 OR 대댓글 구분값 bg 색상
    private Long groupIdx; // 댓글 그룹 idx
    @ExcelColumnName(headerName = "lang.comment.parent.idx")
    private Long parentIdx; // 부모 댓글 idx
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
    @ExcelColumnName(headerName = "lang.comment.regdate")
    private String regdate; // 등록일(UTC)
    private String regdateTz; // 등록일 타임존
    @ExcelColumnName(headerName = "lang.comment.modifydate")
    private String modifyDate; // 수정일(UTC)
    private String modifyDateTz; // 수정일 타임존

    /**
     * contents
     **/
    private Integer categoryIdx; // 카테고리 idx
    private String categoryName; // 카테고리 이름
    private Integer genreIdx; // 장르 idx
    private String genreName; // 장르 이름
    private String title; // 작품 제목
    private String description; // 작품 소개글
    private Long lastEpisodeIdx; // 마지막 회차 idx
    private String lastEpisodeTitle; // 마지막 회차 제목
    private Integer adult;  // 시청 연령(성인유무)
    private String adultText; // 시청 연령 문자변환
    private String adultBg; // 시청 연령 color
    private Integer adultPavilion; // 성인관(0:일반, 1:성인관)
    private String adultPavilionText; // 성인관 여부 문자변환
    private String adultPavilionBg; // 성인관 여부 배지 정보
    private Integer completeTypeIdx; // 완결 타입 idx
    private Integer progress; // 연재정보(작품 상태)
    private String progressText; // 작품상태 문자변환
    private Integer exclusive; // 서비스 유형 - 독점
    private String exclusiveText; // 서비스 유형 - 독점 문자변환
    private Integer publication; // 서비스 유형 - 단행본
    private Integer revision; // 서비스 유형 - 개정판
    private String label; // 작품 라벨
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
                String.valueOf(parentIdx),
                stateText,
                viewText,
                content,
                String.valueOf(likeCnt),
                regdate,
                modifyDate);
    }
}
