package com.architecture.admin.models.dto.ip;

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
@ExcelFileName(filename = "lang.ip.title.list")
public class IpDto implements ExcelDto {

    @ExcelColumnName(headerName = "lang.ip.idx")
    private Long idx;
    private Integer state; // 상태값
    @ExcelColumnName(headerName = "lang.ip.state")
    private String stateText; // 상태값 문자 변환
    private String stateBg; // 상태값 배지
    private String type; // 관리자 구분
    @ExcelColumnName(headerName = "lang.ip.type")
    private String typeText; // 관리자 구분값 문자 변환
    @ExcelColumnName(headerName = "lang.ip.ip")
    private String ip; // ip 주소
    @ExcelColumnName(headerName = "lang.ip.memo")
    private String memo; // ip 상세 내용
    @ExcelColumnName(headerName = "lang.ip.regdate")
    private String regdate; // 등록일
    private String regdateTz; // 등록일 타임존

    @Override
    public List<String> mapToList() {
        return Arrays.asList(
                String.valueOf(idx),
                stateText,
                typeText,
                ip,
                memo,
                regdate);
    }
}
