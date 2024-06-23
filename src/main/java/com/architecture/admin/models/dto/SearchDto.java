package com.architecture.admin.models.dto;

import com.architecture.admin.libraries.PaginationLibray;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 공통 페이징, 검색 Dto
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchDto {
    private String searchDateType; // 날짜 종류(등록일, 만료일)
    private String searchType;  // 검색 종류
    private String isAdd;       // Y: 지급, N : 차감
    private String searchWord;  // 검색어
    private Integer searchCount;  // 검색 개수
    private String nowDate;  // 현재 날짜 및 시간
    private String searchDate;  // 검색 날짜
    private String searchStartDate;  // 검색 시작 날짜
    private String searchEndDate;  // 검색 마지막 날짜
    private Integer state;  // 상태값
    private Integer view;  // 노출 상태
    private Integer type;  // 타입
    private Integer form;  // 형식
    private Integer area;  // 노출 영역
    private String adminType;  // 관리자 구분
    private Long idx;  // idx
    private Long memberIdx;  // 회원 idx
    private Integer contentsIdx;  // idx
    private PaginationLibray pagination;  // 페이징
    private String site;  // OTT 사이트
    private Integer categoryIdx;       // 카테고리(1: 웹툰, 2: 만화, 3: 소설)
    private Integer genreIdx;          // 장르
    private Integer adultPavilion;     // 성인관 유무(0: 일반관, 1: 성인관)
    private Integer pavilionIdx;       // 성인관 유무(0: 일반관, 1: 성인관)
    private Integer adult;             // 성인작 유무(0: 일반작, 1: 성인작)
    private Integer progress;          // 진행상황(1:연재, 2:휴재, 3:완결)
    private String list;               // 리스트

    // default paging
    public SearchDto() {
        this.page = 1; // 시작번호
        this.offset = 0; // DB 조회 갯수
        this.limit = 10; // 한 페이지 리스트 수
        this.recordSize = this.limit; // 최대 표시 페이징 갯수
        this.pageSize = 10;
    }
    public int getOffset() {
        return (page -1) * recordSize;
    }

    private int page; // 시작위치
    private int offset; // 리스트 갯수
    private int limit; // 한 페이지 리스트 수
    private int recordSize; // 최대 표시 페이징 갯수
    private int pageSize;

}
