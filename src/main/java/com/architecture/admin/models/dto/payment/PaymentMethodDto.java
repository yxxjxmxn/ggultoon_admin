package com.architecture.admin.models.dto.payment;

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
@ExcelFileName(filename = "lang.payment.method.title.list")
public class PaymentMethodDto implements ExcelDto {

    /**
     * payment_method
     **/
    @ExcelColumnName(headerName = "lang.payment.method.idx")
    private Integer idx; // 결제 수단 idx
    private Integer state; // 사용 상태값 (0:미사용 / 1:사용)
    @ExcelColumnName(headerName = "lang.payment.method.status")
    private String stateText; // 사용 상태 문자 변환
    private String stateBg; // 사용 상태 bg 색상
    private Integer autoPay; // 결제방식(0:일반결제 / 1:자동결제)
    private String autoPayBg; // 결제방식 배지
    @ExcelColumnName(headerName = "lang.payment.method.autoPay")
    private String autoPayText; // 결제방식 문자변환
    @ExcelColumnName(headerName = "lang.payment.method.pgName")
    private String pgName;      // PG 업체명
    @ExcelColumnName(headerName = "lang.payment.method.mchtId")
    private String mchtId;      // 상점 아이디
    @ExcelColumnName(headerName = "lang.payment.method.licenseKey")
    private String licenseKey;  // 해시체크키
    @ExcelColumnName(headerName = "lang.payment.method.aes256Key")
    private String aes256Key;   // 암호키
    @ExcelColumnName(headerName = "lang.payment.method.methodType")
    private String methodType;  // 결제수단 종류
    @ExcelColumnName(headerName = "lang.payment.method.method")
    private String method;      // 결제수단 코드
    @ExcelColumnName(headerName = "lang.payment.method.corpPayCode")
    private String corpPayCode; // 간편결제코드
    @ExcelColumnName(headerName = "lang.payment.method.methodNoti")
    private String methodNoti; // 결제 결과 메소드
    @ExcelColumnName(headerName = "lang.payment.method.mchtName")
    private String mchtName;    // 서비스명(한글)
    @ExcelColumnName(headerName = "lang.payment.method.mchtEName")
    private String mchtEName;   // 서비스명(영어)
    @ExcelColumnName(headerName = "lang.payment.method.notiUrl")
    private String notiUrl;     // 결제처리 url
    @ExcelColumnName(headerName = "lang.payment.method.paymentServer")
    private String paymentServer; // 결제 서버 url
    @ExcelColumnName(headerName = "lang.payment.method.cancelServer")
    private String cancelServer; // 결제 취소 서버 url
    @ExcelColumnName(headerName = "lang.payment.method.info")
    private String info;        // 설명
    @ExcelColumnName(headerName = "lang.payment.method.regdate")
    private String regdate; // 등록일(UTC)
    private String regdateTz; // 등록일 타임존

    @Override
    public List<String> mapToList() {
        return Arrays.asList(
                String.valueOf(idx),
                stateText,
                autoPayText,
                pgName,
                mchtId,
                licenseKey,
                aes256Key,
                methodType,
                method,
                corpPayCode,
                methodNoti,
                mchtName,
                mchtEName,
                notiUrl,
                paymentServer,
                cancelServer,
                info,
                regdate);
    }
}
