package com.architecture.admin.models.dto.admin;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;


/*****************************************************
 * 사용자 설정
 * ---------------------------------------------------
 * @Null Null만 입력 가능
 * @Size(min=,max=) 문자열, 배열등의 크기가 만족하는가?
 * @Pattern(regex=) 정규식을 만족하는가?
 * @Max(숫자) 지정 값 이하인가?
 * @Min(숫자) 지정 값 이상인가
 * @Future 현재 보다 미래인가?
 * @Past 현재 보다 과거인가?
 * @Positive 양수만 가능
 * @PositiveOrZero 양수와 0만 가능
 * @Negative 음수만 가능
 * @NegativeOrZero 음수와 0만 가능
 * @Email 이메일 형식만 가능
 * @Digits(integer=, fraction = )	대상 수가 지정된 정수와 소수 자리 수 보다 작은가?
 * @DecimalMax(value=) 지정된 값(실수) 이하인가?
 * @DecimalMin(value=) 지정된 값(실수) 이상인가?
 * @AssertFalse false 인가?
 * @AssertTrue true 인가?
 ****************************************************/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminDto {
    // base attribute
    private Long idx; // idx
    private String id; // 관리자 id
    private String password; // 패스워드
    private String name; // 이름
    private Integer level; // 관리자 레벨
    private Integer state; // 상태 (0:탈퇴/1:정상/2:대기)
    private String stateText; // 상태 문자 변환
    private String stateBg; // 상태 bg 색상
    private String lastLoginDate; // 마지막 로그인(UTC)
    private String lastLoginDateTz; // 마지막 로그인 타임존
    private String regdate; // 등록일(UTC)
    private String regdateTz; // 등록일 타임존

    // sql
    private Long insertedId;
    private Long lastDateRow;

    // 기타 : 비밀번호 변경 폼
    private String oldPassword; // 이전 비밀번호
    private String newPassword; // 변경할 비밀번호
    private String passwordConfirm; // 변경할 비밀번호 확인

}
