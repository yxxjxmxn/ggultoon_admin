package com.architecture.admin.models.dto.admin;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;


/*****************************************************
 * WEBTOON CP관리자
 ****************************************************/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CpDto {
    // base attribute
    private Integer idx; // idx
    @Pattern(regexp = "^[a-zA-Z0-9_]{5,20}$", message = "{lang.cp.exception.id_fail}")
    private String id; // 관리자 id
    private String password; // 패스워드
    private String name; // 대표이름
    private String ceo; // 대표이름
    private String manager; // 담당자
    private String managerPhone; // 담당자 전화번호
    private String companyName; // 사업자명
    private String businessNumber; // 사업자번호
    private String businessType; // 사업자유형
    private String phone; // 회사전화번호
    @Email
    private String companyEmail; // 회사이메일
    private String bankName; // 은행명
    private String bankNumber; // 계좌번호
    private String holder; // 예금주
    private Integer cpmg; // cp계약금
    @Email
    private String billingEmail; // 계산서 이메일

    private Integer level; // 관리자 레벨
    private Integer state; // 상태 (0:탈퇴/1:정상/2:대기/3:정지)
    private String stateText; // 상태 문자 변환
    private String stateBg; // 상태 bg 색상
    private String lastLoginDate; // 마지막 로그인(UTC)
    private String lastLoginDateTz; // 마지막 로그인 타임존
    private String regdate; // 등록일(UTC)
    private String regdateTz; // 등록일 타임존

    // sql
    private Long insertedId;
    private Long lastDateRow;

    // 업로드 이미지
    private  MultipartFile uploadFile;
    private  MultipartFile businessFile;
    private  MultipartFile bankFile;
    private  String businessFileUrl;
    private  String bankFileUrl;


    // 리사이즈 이미지 사이즈
    private Integer imageSizeMain;
    private Integer imageSizeThumb;

    //이미지 번호
    private Integer imageIdx;

    // 회사명
    private String company_name;
    // 회사 이메일
    private String company_email;
}
