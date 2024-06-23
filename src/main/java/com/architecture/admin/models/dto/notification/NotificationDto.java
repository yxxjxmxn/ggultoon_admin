package com.architecture.admin.models.dto.notification;

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
@ExcelFileName(filename = "lang.notification.title.list")
public class NotificationDto implements ExcelDto {

    /** member_notification **/
    @ExcelColumnName(headerName = "lang.notification.idx")
    private Long idx; // 알림 idx
    private Integer state; // 상태값
    private String stateBg; // 상태값 배지
    @ExcelColumnName(headerName = "lang.notification.state")
    private String stateText; // 상태값 문자 변환
    private Long memberIdx; // 회원 idx
    @ExcelColumnName(headerName = "lang.notification.id")
    private String id; // 회원 아이디
    @ExcelColumnName(headerName = "lang.notification.nick")
    private String nick; // 회원 닉네임
    private String category; // 알림 카테고리
    @ExcelColumnName(headerName = "lang.notification.category")
    private String categoryText; // 알림 카테고리 문자변환
    private String type; // 알림 보낼 테이블명
    private Long typeIdx; // 알림 보낼 테이블의 idx
    @ExcelColumnName(headerName = "lang.notification.title")
    private String title; // 알림 내용
    @ExcelColumnName(headerName = "lang.notification.url")
    private String url; // 알림 url
    @ExcelColumnName(headerName = "lang.notification.regdate")
    private String regdate; // 등록일
    private String regdateTz; // 등록일 타임존
    @ExcelColumnName(headerName = "lang.notification.checkDate")
    private String checkDate; // 확인일
    private String checkDateTz; // 확인일 타임존
    @ExcelColumnName(headerName = "lang.notification.delDate")
    private String delDate; // 삭제일
    private String delDateTz; // 삭제일 타임존
    
    @Override
    public List<String> mapToList() {
        return Arrays.asList(
                String.valueOf(idx),
                stateText,
                id,
                nick,
                categoryText,
                title,
                url,
                regdate,
                checkDate,
                delDate);
    }
}
