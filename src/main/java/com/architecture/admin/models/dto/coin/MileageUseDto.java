package com.architecture.admin.models.dto.coin;

import com.architecture.admin.libraries.excel.ExcelColumnName;
import com.architecture.admin.libraries.excel.ExcelFileName;
import com.architecture.admin.models.dto.excel.ExcelDto;
import lombok.*;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@ExcelFileName(filename = "lang.coin.title.use.mileage.list")
public class MileageUseDto implements ExcelDto {

    @ExcelColumnName(headerName = "lang.coin.idx")
    private Long idx;
    private Integer state;       // 상태
    @ExcelColumnName(headerName = "lang.coin.state")
    private String stateText;       // 상태 메시지(0:만료, 1:정상)
    private String stateBg;         // 상태 색상
    private Long memberIdx;      // 회원 번호
    @ExcelColumnName(headerName = "lang.member.id")
    private String id;           // 회원 아이디
    @ExcelColumnName(headerName = "lang.member.nick")
    private String nick;         // 회원 닉네임
    @ExcelColumnName(headerName = "lang.member.site")
    private String site;            // 유입 사이트
    @ExcelColumnName(headerName = "lang.coin.title")
    private String title;         // 내용
    @ExcelColumnName(headerName = "lang.coin.pay.mileage")
    private Integer mileage;     // 지급 마일리지
    @ExcelColumnName(headerName = "lang.coin.use.mileage")
    private Integer usedMileage; // 사용 마일리지
    @ExcelColumnName(headerName = "lang.coin.rest.mileage")
    private Integer restMileage; // 남은 마일리지
    @ExcelColumnName(headerName = "lang.coin.regdate")
    private String regdate;      // 등록일
    private String regdateTz;    // 등록일 타임존
    @ExcelColumnName(headerName = "lang.coin.expireDate")
    private String expiredate;   // 만료일
    private String expiredateTz; // 만료일 타임존

    @Override
    public List<String> mapToList() {
        return Arrays.asList(
                String.valueOf(idx),
                stateText,
                id,
                nick,
                site,
                title,
                String.valueOf(mileage),
                String.valueOf(usedMileage),
                String.valueOf(restMileage),
                regdate);
    }
}
