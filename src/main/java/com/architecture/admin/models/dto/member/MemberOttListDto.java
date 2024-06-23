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
@ExcelFileName(filename = "lang.member.title.ott")
public class MemberOttListDto implements ExcelDto {
    @ExcelColumnName(headerName = "lang.member.idx")
    private Long idx;           // 고유번호
    @ExcelColumnName(headerName = "lang.member.id")
    private String id;          // 꿀툰아이디
    @ExcelColumnName(headerName = "lang.member.ott.site")
    private String site;        // OTT사이트
    @ExcelColumnName(headerName = "lang.member.ott.id")
    private String ottId;       // OTT아이디
    @ExcelColumnName(headerName = "lang.member.ott.code")
    private String bannerCode;  // 배너코드
    @ExcelColumnName(headerName = "lang.member.ott.point")
    private Integer point=0;    // 포인트
    @ExcelColumnName(headerName = "lang.member.ott.sand.msg")
    private String sendMsg;     // 보낸메세지
    @ExcelColumnName(headerName = "lang.member.ott.return.msg")
    private String returnMsg;   // 결과메세지
    @ExcelColumnName(headerName = "lang.member.regdate")
    private String regdate;     // 등록일
    private Long memberIdx;     // 회원번호





    @Override
    public List<String> mapToList() {
        return Arrays.asList(
                String.valueOf(idx),
                id,
                site,
                ottId,
                bannerCode,
                String.valueOf(point),
                sendMsg,
                returnMsg,
                regdate
                );
    }
}