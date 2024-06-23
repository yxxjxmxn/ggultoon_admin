package com.architecture.admin.models.dto.payment;

import com.architecture.admin.libraries.excel.ExcelColumnName;
import com.architecture.admin.libraries.excel.ExcelFileName;
import com.architecture.admin.models.dto.excel.ExcelDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.Email;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@ExcelFileName(filename = "lang.payment.title.list")
public class PaymentDto implements ExcelDto {

    /**
     * payment
     **/
    @ExcelColumnName(headerName = "lang.payment.idx")
    private Long idx; // payment.idx
    @ExcelColumnName(headerName = "lang.payment.productIdx")
    private Long productIdx; // product.idx
    private Long pgIdx; // pg.idx
    @ExcelColumnName(headerName = "lang.payment.member")
    private Long memberIdx; // member.idx
    @Email
    @ExcelColumnName(headerName = "lang.payment.id")
    private String id; // member.id(이메일)
    @ExcelColumnName(headerName = "lang.payment.nick")
    private String nick; // member.nick
    @ExcelColumnName(headerName = "lang.payment.admin")
    private Long adminIdx; // admin.idx
    @ExcelColumnName(headerName = "lang.payment.payer")
    private String payer; // 지급자
    private String admin; // 관리자
    @ExcelColumnName(headerName = "lang.payment.app")
    private String app; // 앱결제
    @ExcelColumnName(headerName = "lang.payment.paidCoin")
    private Integer coin; // 지급 코인
    @ExcelColumnName(headerName = "lang.payment.usedCoin")
    private Integer usedCoin; // 사용 코인
    @ExcelColumnName(headerName = "lang.payment.restCoin")
    private Integer restCoin; // 잔여 코인
    @ExcelColumnName(headerName = "lang.payment.coin.exp")
    private String coinExp; // 코인 만료일
    @ExcelColumnName(headerName = "lang.payment.paidBonus")
    private Integer bonus; // 지급 보너스 코인
    @ExcelColumnName(headerName = "lang.payment.usedBonus")
    private Integer usedBonus; // 사용 보너스 코인
    @ExcelColumnName(headerName = "lang.payment.restBonus")
    private Integer restBonus; // 잔여 보너스 코인
    @ExcelColumnName(headerName = "lang.payment.bonus.exp")
    private String bonusExp; // 보너스 만료일
    @ExcelColumnName(headerName = "lang.payment.paidMileage")
    private Integer mileage; // 지급 마일리지
    @ExcelColumnName(headerName = "lang.payment.usedMileage")
    private Integer usedMileage; // 사용 마일리지
    @ExcelColumnName(headerName = "lang.payment.restMileage")
    private Integer restMileage; // 잔여 마일리지
    @ExcelColumnName(headerName = "lang.payment.mileage.exp")
    private String mileageExp; // 마일리지 만료일
    @ExcelColumnName(headerName = "lang.payment.first")
    private String firstText; // 첫 결제 문자 변환
    @ExcelColumnName(headerName = "lang.payment.orderIdx")
    private String orderIdx; // 주문번호
    @ExcelColumnName(headerName = "lang.payment.pay")
    private String pay; // 결제 금액

    @ExcelColumnName(headerName = "lang.payment.currency")
    private String currency; // 사용 화폐
    @ExcelColumnName(headerName = "lang.payment.country")
    private String country; // 국가
    @ExcelColumnName(headerName = "lang.payment.payMethod")
    private String payMethod; // 결제 수단 (1:mobile / 2:pc / 3:android app / 4:ios app)
    @ExcelColumnName(headerName = "lang.payment.payType")
    private String payType; // 결제 타입 ex)mobile, card, culture...
    @ExcelColumnName(headerName = "lang.payment.payType")
    private String payTypeText; // 결제 타입 ex)휴대전화, 신용카드...
    @ExcelColumnName(headerName = "lang.payment.tid")
    private String tid; // 거래 번호
    @ExcelColumnName(headerName = "lang.payment.transactionNo")
    private String transactionNo; // 거래 고유 번호
    @ExcelColumnName(headerName = "lang.payment.provider")
    private String provider; // 통신사
    @ExcelColumnName(headerName = "lang.payment.payTypeNumber")
    private String payTypeNumber; // 결제 수단에 사용된 결제 매개체 번호 ex)핸드폰 번호, 카드사 코드
    @ExcelColumnName(headerName = "lang.payment.cpID")
    private String cpID; // 결제 시 사용된 cp 코드
    @ExcelColumnName(headerName = "lang.payment.otp")
    private String otp; // 결제 시 사용된 인증번호
    @ExcelColumnName(headerName = "lang.payment.memberIP")
    private String memberIP; // 아이피
    private Integer state; // 상태값 (0:취소 / 1:정상)
    @ExcelColumnName(headerName = "lang.payment.status")
    private String stateText; // 상태 문자 변환
    private String stateBg; // 상태 bg 색상
    @ExcelColumnName(headerName = "lang.payment.regdate")
    private String regdate; // 등록일(UTC)
    private String regdateTz; // 등록일 타임존
    @ExcelColumnName(headerName = "lang.payment.moddate")
    private String moddate; // 취소일(UTC)
    private String moddateTz; // 취소일 타임존
    private String orderNo; // 결제번호


    /**
     * payment_info
     **/
    private Integer first; // 첫 결제 여부
    private String firstBg; // 첫 결제 bg 색상

    /**
     * payment_log
     **/
    private Integer paymentType; // 결제타입(0:결제취소, 1:결제시도, 2: 결제완료)

    /**
     * member_coin_save
     **/
    private Long coinSaveIdx; // member_coin_save.idx

    /**
     * member_coin_used
     **/
    private Long coinUsedIdx; // member_coin_used.idx (type : 1)
    private Long bonusUsedIdx; // member_coin_used.idx (type : 2)
    private Integer type; // 1: coin, 2: bonus
    private Integer coinState; // 코인 상태값(0: 사용불가, 1: 사용 가능)
    private Integer bonusState; // 보너스코인 상태값(0: 사용불가, 1: 사용 가능)

    /**
     * member_mileage_save
     **/
    private Long mileageSaveIdx; // member_mileage_save.idx

    /**
     * member_mileage_used
     **/
    private Long mileageUsedIdx; // member_mileage_used.idx
    private Integer mileageState; // 마일리지 상태값(0: 사용불가, 1: 사용 가능)


    /**
     * member_coin_used_log
     * member_mileage_used_log
     **/
    private String title; // 지급 or 차감 내용

    // sql
    private Integer insertedId;// 입력된 idx
    private Integer affectedRow;// 처리 row 수
    
    // 기타
    private String buttonState; // 버튼 상태
    private Boolean isCoinTrue; // 코인 회수 가능 여부
    private Boolean isBonusTrue; // 보너스 회수 가능 여부
    private Boolean isMileageTrue; // 마일리지 회수 가능 여부




    @Override
    public List<String> mapToList() {
        return Arrays.asList(
                String.valueOf(idx),
                String.valueOf(productIdx),
                String.valueOf(memberIdx),
                id,
                nick,
                String.valueOf(adminIdx),
                payer,
                app,
                String.valueOf(coin),
                String.valueOf(usedCoin),
                String.valueOf(restCoin),
                coinExp,
                String.valueOf(bonus),
                String.valueOf(usedBonus),
                String.valueOf(restBonus),
                bonusExp,
                String.valueOf(mileage),
                String.valueOf(usedMileage),
                String.valueOf(restMileage),
                mileageExp,
                firstText,
                String.valueOf(orderIdx),
                pay,
                currency,
                country,
                payMethod,
                payType,
                tid,
                transactionNo,
                provider,
                payTypeNumber,
                cpID,
                otp,
                memberIP,
                stateText,
                regdate,
                moddate);
    }
}
