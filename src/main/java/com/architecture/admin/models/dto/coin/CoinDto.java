package com.architecture.admin.models.dto.coin;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class CoinDto {

    private Long idx;
    private Long memberIdx;      // 회원 번호
    private String id;           // 회원 아이디
    private String nick;         // 회원 닉네임
    private Long achievementIdx; // 업적 번호
    private Integer productIdx;  // 상품 번호
    private Integer coin;        // 유료 코인
    private Integer type;        // 코인 유형(1: 코인, 2: 보너스 코인)
    private Integer coinFree;    // 보너스 코인 (결제 시 추가지급 코인)
    private Integer mileage;     // 마일리지 (업적 달성시 지급되는 코인)
    private Integer usedCoin;    // 사용 코인
    private Integer restCoin;    // 남은 코인
    private Integer usedCoinFree;// 사용 보너스 코인
    private Integer restCoinFree;// 남은 보너스 코인
    private Integer usedMileage; // 사용 마일리지
    private Integer restMileage; // 남은 마일리지
    private Integer ticketCount; // 이용권 개수
    private String position;     // 유입 결로
    private Integer state;       // 상태
    private String expiredate;   // 만료일
    private String expireDateTz; // 만료일 타임존
    private String regdate;
    private String regdateTz;

    // 관리자 코인 지급시 이용
    private Integer adminIdx;     // 관리자 번호
    private String adminId;       // 관리자 아이디
    private String coinType;      // coin, coinFree, mileage
    private Integer paymentPrice; // 지급 금액
    private Integer subResultCoin; // 차감된 결과 코인
    private String paymentType;   // 결제 유형
    private Long coinSaveIdx;     // member_coin_save.idx
    private Long mileageSaveIdx;  // member_mileage_save.idx
    private Long paymentIdx;      // payment.idx
    private String isAdd;         // Y: 적립, N: 차감
    private String title;         // 적립 및 차감 내용
    private String mileageExpireDate; // 마일리지 만료일
    private String coinFreeExpireDate;  // 보너스코인 만료일

    /**
     * admin_coin 테이블
     */
    private Long groupIdx;          // 그룹 번호

    /**
     * admin 테이블
     */
    private String name;            // 관리자 이름
    private String admin;           // 관리자 아이디

    /**
     * 코인 지급시 사용
     */
    private String actionType;      // 지급 방식
    private String reason;          // 지급 사유

    /**
     * 문자 변환
     */
    private String stateText;       // 상태 메시지(0:만료, 1:정상)
    private String stateBg;         // 상태 색상
    private String typeText;        // 1: 코인, 2: 무료 코인
}
