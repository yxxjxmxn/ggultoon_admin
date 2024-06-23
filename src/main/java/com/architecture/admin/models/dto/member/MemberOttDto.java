package com.architecture.admin.models.dto.member;

import com.architecture.admin.libraries.excel.ExcelColumnName;
import com.architecture.admin.libraries.excel.ExcelFileName;
import com.architecture.admin.models.dto.excel.ExcelDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ExcelFileName(filename = "lang.member.title.ott.stat")
public class MemberOttDto implements ExcelDto {
    private Long idx;           // 고유번호
    private Long memberIdx;     // 회원번호
    private String id;          // 꿀툰아이디
    private String ottId;       // OTT아이디
    private String ci;          // 인증정보
    private Integer point=0;    // 포인트
    private Integer state;      // 상태
    private String sendMsg;     // 보낸메세지
    private String returnMsg;   // 결과메세지
    private String returnUrl;   // 결과URL
    private String regdate;     // 등록일
    private String regdateTz;   // 등록일 타임존
    private String today;       // 통계일
    @ExcelColumnName(headerName = "lang.member.ott.date")
    private String date;        // 통계날짜
    @ExcelColumnName(headerName = "lang.member.ott.site")
    private String site;        // OTT사이트
    @ExcelColumnName(headerName = "lang.member.ott.code")
    private String bannerCode;  // 배너코드
    @ExcelColumnName(headerName = "lang.member.ott.visit")
    private Integer visit=0;    // 접속
    @ExcelColumnName(headerName = "lang.member.ott.join")
    private Integer join=0;     // 가입
    @ExcelColumnName(headerName = "lang.member.ott.pay.cnt")
    private Integer payCnt;     // 결제횟수
    @ExcelColumnName(headerName = "lang.member.ott.pay")
    private Integer pay;        // 결제금액

    @Override
    public List<String> mapToList() {
        return Arrays.asList(
                date,
                site,
                bannerCode,
                String.valueOf(visit),
                String.valueOf(join),
                String.valueOf(payCnt),
                String.valueOf(pay)
                );
    }
}