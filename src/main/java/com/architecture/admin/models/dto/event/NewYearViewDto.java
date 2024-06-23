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
@ExcelFileName(filename = "lang.event.title.newyear.list")
public class NewYearViewDto implements ExcelDto {

    @ExcelColumnName(headerName = "lang.event.idx")
    private Long idx; // 번호
    private Long memberIdx; // 회원 idx
    @ExcelColumnName(headerName = "lang.member.id")
    private String id; // 회원 아이디
    @ExcelColumnName(headerName = "lang.member.nick")
    private String nick; // 회원 닉네임
    private Integer route; // 참여 기기 정보(1:웹 / 2:어플)
    @ExcelColumnName(headerName = "lang.event.route")
    private String routeText; // 참여 기기 정보 문자 변환
    private String routeBg; // 참여 기기 정보 배지
    private Integer type; // 구매 유형 정보(1:대여 / 2:소장)
    @ExcelColumnName(headerName = "lang.event.type")
    private String typeText; // 구매 유형 정보 문자 변환
    private String typeBg; // 구매 유형 배지
    private Integer userType; // 회원 구분 정보(1:신규 / 2:기존)
    @ExcelColumnName(headerName = "lang.event.userType")
    private String userTypeText; // 회원 구분 정보 문자 변환
    private String userTypeBg; // 회원 구분 배지
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
    private Long episodeIdx; // 회차 idx
    @ExcelColumnName(headerName = "lang.contents.episode")
    private String episode; // 회차
    @ExcelColumnName(headerName = "lang.event.regdate")
    private String regdate; // 감상일
    private String regdateTz; // 감상일 타임존
    @ExcelColumnName(headerName = "lang.event.expiredate")
    private String expiredate; // 종료일
    private String expiredateTz; // 종료일 타임존

    /** sql **/
    private Long insertedIdx;

    @Override
    public List<String> mapToList() {
        return Arrays.asList(
                String.valueOf(idx),
                id,
                nick,
                routeText,
                typeText,
                userTypeText,
                title,
                pavilionText,
                adultText,
                category,
                genre,
                episode,
                regdate,
                expiredate);
    }
}
