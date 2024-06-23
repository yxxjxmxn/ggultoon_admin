package com.architecture.admin.models.dto.coin;

import com.architecture.admin.libraries.excel.ExcelColumnName;
import com.architecture.admin.libraries.excel.ExcelFileName;
import com.architecture.admin.models.dto.excel.ExcelDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@Builder
@ExcelFileName(filename = "lang.coin.title.accumulation.mileage.list")
public class MileageSaveDto implements ExcelDto {

    @ExcelColumnName(headerName = "lang.coin.idx")
    private Long idx;
    private Long achievementIdx; // 업적 번호
    private Long paymentIdx;     // 결제 번호
    private Integer state;       // 상태
    @ExcelColumnName(headerName = "lang.coin.state")
    private String stateText;    // 상태 메시지(0:만료, 1:정상)
    private String stateBg;      // 상태 색상
    private Long memberIdx;      // 회원 번호
    @ExcelColumnName(headerName = "lang.member.id")
    private String id;           // 회원 아이디
    @ExcelColumnName(headerName = "lang.member.nick")
    private String nick;         // 회원 닉네임
    @ExcelColumnName(headerName = "lang.coin.mileage")
    private Integer mileage;     // 마일리지
    @ExcelColumnName(headerName = "lang.coin.position")
    private String position;     // 적립 위치(업적 결제 이벤트 등)
    @ExcelColumnName(headerName = "lang.coin.title")
    private String title;        // 내용
    @ExcelColumnName(headerName = "lang.coin.admin.name")
    private String admin;        // 지급한 관리자 아이디
    @ExcelColumnName(headerName = "lang.coin.regdate")
    private String regdate;      // 적립일
    private String regdateTz;    // 적립일 타임존

    @Override
    public List<String> mapToList() {
        return Arrays.asList(
                String.valueOf(idx),
                stateText,
                id,
                nick,
                String.valueOf(mileage),
                position,
                title,
                admin,
                regdate);
    }
}
