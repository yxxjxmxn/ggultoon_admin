package com.architecture.admin.models.dto.member;

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
@ExcelFileName(filename = "lang.member.purchase.list")
public class MemberPurchaseDto implements ExcelDto {

    @ExcelColumnName(headerName = "lang.member.purchase.idx")
    private Long idx;               // purchase.idx
    private Long memberIdx;         // member.idx
    @ExcelColumnName(headerName = "lang.member.id")
    private String id;              // 회원 아이디
    @ExcelColumnName(headerName = "lang.member.nick")
    private String nick;            // 회원 닉네임
    private Long contentsIdx;       // 컨텐츠 번호
    @ExcelColumnName(headerName = "lang.contents.title")
    private String title;           // 컨텐츠 제목
    @ExcelColumnName(headerName = "lang.contents.category")
    private String category;        // 카테고리
    @ExcelColumnName(headerName = "lang.contents.genre")
    private String genre;           // 장르
    private Long episodeIdx;       // 회차 번호
    private Integer episodeNumber;  // 회차
    private Integer buyAllIdx;      // 전체 회차 구매 여부
    @ExcelColumnName(headerName = "lang.contents.episode")
    private String episodeNumberText; // 0이면 전체, 0이 아니면 n회차
    @ExcelColumnName(headerName = "lang.contents.episode.title")
    private String episodeTitle; // 회차 제목
    private Integer type;           // 시청 방식 / 1: 대여, 2 : 소장
    @ExcelColumnName(headerName = "lang.contents.buy.type")
    private String typeText;        // 시청 방식 text
    @ExcelColumnName(headerName = "lang.coin.payCoin")
    private Integer coin;           // 유료 코인
    @ExcelColumnName(headerName = "lang.coin.mileage")
    private Integer mileage;        // 마일리지
    @ExcelColumnName(headerName = "lang.coin.ticket")
    private Integer ticket;         // 이용권
    @ExcelColumnName(headerName = "lang.contents.startDate")
    private String regdate;         // 시작일자
    @ExcelColumnName(headerName = "lang.contents.expireDate")
    private String expiredate;      // 종료일자

    // 문자 변환
    private String typeBg;

    @Override
    public List<String> mapToList() {
        return Arrays.asList(
                String.valueOf(idx),
                id,
                nick,
                title,
                category,
                genre,
                episodeNumberText,
                episodeTitle,
                typeText,
                String.valueOf(coin),
                String.valueOf(mileage),
                String.valueOf(ticket),
                regdate,
                expiredate);
    }
}
