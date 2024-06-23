package com.architecture.admin.models.dto.curation;

import com.architecture.admin.libraries.excel.ExcelColumnName;
import com.architecture.admin.libraries.excel.ExcelFileName;
import com.architecture.admin.models.dto.contents.ImageDto;
import com.architecture.admin.models.dto.excel.ExcelDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@ExcelFileName(filename = "lang.curation.title.content.list")
public class CurationContentDto implements ExcelDto {

    /** curation_mapping **/
    @ExcelColumnName(headerName = "lang.curation.idx")
    private Long mappingIdx; // 큐레이션 매핑 idx
    @ExcelColumnName(headerName = "lang.curation.sort")
    private Integer mappingSort; // 작품 매핑 순서
    private Integer mappingState; // 매핑 사용 여부
    @ExcelColumnName(headerName = "lang.curation.mapping.state")
    private String mappingStateText; // 매핑 사용 여부 문자 변환
    private String mappingStateBg; // 매핑 사용 여부 배지
    private Integer sort; // 노출 순서
    @ExcelColumnName(headerName = "lang.curation.content.idx")
    private Long idx; // 작품 idx
    @ExcelColumnName(headerName = "lang.curation.title")
    private String title; // 제목
    private Integer state; // 상태값
    @ExcelColumnName(headerName = "lang.curation.content.state")
    private String stateText; // 상태값 문자 변환
    private String stateBg; // 상태값 배지
    private Integer pavilion; // 이용관
    @ExcelColumnName(headerName = "lang.curation.pavilion")
    private String pavilionText; // 이용관 문자변환
    private String pavilionBg; // 이용관 배지
    private Integer adult; // 성인작 여부
    @ExcelColumnName(headerName = "lang.curation.adult")
    private String adultText; // 성인작 여부 문자변환
    private String adultBg; // 성인작 여부 배지
    @ExcelColumnName(headerName = "lang.curation.category")
    private String category; // 카테고리
    @ExcelColumnName(headerName = "lang.curation.genre")
    private String genre; // 장르
    @ExcelColumnName(headerName = "lang.curation.episode.count")
    private Integer episodeCnt; // 회차 수
    @ExcelColumnName(headerName = "lang.curation.regdate")
    private String regdate; // 등록일
    private String regdateTz; // 등록일 타임존

    /** 기타 **/

    private String curationTitle; // 큐레이션 제목
    private Integer maxSort; // 마지막 순서 번호
    List<HashMap<String, Long>> list; // 순서 재정렬용 리스트
    List<Long> idxList; // 삭제용 리스트
    private String description; // 작품 소개
    private String writer; // 글작가 리스트
    private String painter; // 그림작가 리스트
    private String tag; // 태그 리스트
    private String url; // 이미지 url
    private Integer width; // 이미지 가로 사이즈
    private Integer height; // 이미지 세로 사이즈
    private String type; // 이미지 타입(width / height)
    private List<ImageDto> imageList; // 리사이징 이미지 리스트
    private Long contentsIdx; // 작품 idx
    private Long curationIdx; // 큐레이션 idx

    /** sql **/
    private Long insertedIdx;

    @Override
    public List<String> mapToList() {
        return Arrays.asList(
                String.valueOf(mappingIdx),
                String.valueOf(mappingSort),
                mappingStateText,
                String.valueOf(idx),
                title,
                stateText,
                pavilionText,
                adultText,
                category,
                genre,
                String.valueOf(episodeCnt),
                regdate);
    }
}
