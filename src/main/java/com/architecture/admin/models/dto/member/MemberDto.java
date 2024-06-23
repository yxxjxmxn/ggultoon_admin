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
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@ExcelFileName(filename = "lang.member.title.list")
public class MemberDto implements ExcelDto {

    @ExcelColumnName(headerName = "lang.member.idx")
    private Long idx;               // 회원번호
    private Integer state;          // 상태값
    @ExcelColumnName(headerName = "lang.member.state")
    private String stateText;       // 상태값 문자변환
    @ExcelColumnName(headerName = "lang.member.join.type")
    private String joinType;        // 가입유형
    @ExcelColumnName(headerName = "lang.member.site")
    private String site;            // 유입 사이트
    @ExcelColumnName(headerName = "lang.member.join.device")
    private String joinDevice;      // 가입 구분 app,pc,mobile
    @ExcelColumnName(headerName = "lang.member.login.device")
    private String loginDevice;     // 로그인 구분 app,pc,mobile
    @ExcelColumnName(headerName = "lang.member.id")
    private String id;              // 아이디
    @ExcelColumnName(headerName = "lang.member.nick")
    private String nick;            // 닉네임
    @ExcelColumnName(headerName = "lang.member.name")
    private String name;            // 이름
    @ExcelColumnName(headerName = "lang.member.phone")
    private String phone;           // 전화번호
    @ExcelColumnName(headerName = "lang.member.email")
    private String email;           // 이메일
    @ExcelColumnName(headerName = "lang.member.gender")
    private String genderText;      // 성별 문자변환
    @ExcelColumnName(headerName = "lang.member.birth")
    private String birth;           // 생년월일
    @ExcelColumnName(headerName = "lang.member.marketing")
    private String marketing;       // 마케팅 광고 동의
    @ExcelColumnName(headerName = "lang.member.regdate")
    private String regdate;         // 가입일
    @ExcelColumnName(headerName = "lang.member.loginDate")
    private String logindate;       // 최근 로그인 일자
    private String regdateTz;       // 가입일 타임존
    private Integer auth;           // 본인 인증
    private String txseq;           // 본인 인증 거래 번호
    private Integer adult;          // 성인 인증
    private Integer isSimple;       // 간편가입 여부
    private String simpleType;      // 간편가입 종류(kakao, naver)
    private String joinIp;          // 가입 아이피
    private String gender;          // 성별(M: male, F: female)
    private String ci;              // 개인 식별 고유값
    private String di;              // 업체 중복가입 확인값
    private String lang;            // 사용언어
    private String currency;        // 사용 화폐
    private String policy;          // 회원 정책 정보
    private String policyState;     // 회원 정책 정보 상태값
    private String age;             // 만 14세 이상
    private String privacy;         // 이용약관 및 개인 정보 수집 이용 동의
    private String modifyDate;      // 설정 수정일
    private String modifyDateTz;    // 설정 수정일 타임존
    private String outDate;         // 탈퇴일
    private String outDateTz;       // 탈퇴일 타임존
    private String stateBg;         // 상태 bg 색상
    private String langText;        // 사용언어text
    private String authText;        // 본인 인증
    private String adultText;       // 성인 인증

    // SUB
    private String changeNick;       // 변경할 닉네임(수정 시 사용)
    private Integer outMemberCnt;    // 탈퇴 회원 수
    private Integer normalMemberCnt; // 가입 회원 수

    @Override
    public List<String> mapToList() {
        return Arrays.asList(
                String.valueOf(idx),
                stateText,
                joinType,
                site,
                joinDevice,
                loginDevice,
                id,
                nick,
                name,
                phone,
                email,
                genderText,
                birth,
                marketing,
                regdate,
                logindate);
    }

}
