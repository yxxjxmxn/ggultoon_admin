package com.architecture.admin.libraries;

import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/*****************************************************
 * 시간 라이브러리
 ****************************************************/
@Component
@Data
public class DateLibrary {
    private SimpleDateFormat formatDatetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat koFormatDatetime = new SimpleDateFormat("yy년 MM월 dd일");

    /**
     * date 형식 시간 구하기
     * @return UTC 기준 시간 yyyy-MM-dd hh:mm:ss
     */
    public String getDatetime() {
        java.util.Date dateNow = new java.util.Date(System.currentTimeMillis());

        // 타임존 UTC 기준
        TimeZone utcZone = TimeZone.getTimeZone("UTC");
        formatDatetime.setTimeZone(utcZone);

        // 현재 날짜 구하기 (시스템 시계, 시스템 타임존)
        return formatDatetime.format(dateNow);
    }

    /**
     * 로컬시간을 UTC 시간으로 변경
     * @param date 로컬 시간 yyyy-MM-dd hh:mm:ss
     * @return UTC 기준 시간 yyyy-MM-dd hh:mm:ss
     */
    public String localTimeToUtc(String date) {
        // 타임존 UTC 기준값
        TimeZone utcZone = TimeZone.getTimeZone("UTC");
        formatDatetime.setTimeZone(utcZone);
        Timestamp timestamp = Timestamp.valueOf(date);

        // 현재 날짜 구하기 (시스템 시계, 시스템 타임존)
        return formatDatetime.format(timestamp);
    }

    /**
     * UTC 시간을 로컬시간으로 변경
     * @param date UTC 시간 yyyy-MM-dd hh:mm:ss
     * @return 로컬 시간 yyyy-MM-dd hh:mm:ss
     */
    public String utcToLocalTime(String date) {
        // 입력시간을 Timestamp 변환
        long utcTime = Timestamp.valueOf( date ).getTime();
        TimeZone z = TimeZone.getDefault();
        int offset = z.getOffset(utcTime); // getRawOffset는 썸머타임 반영 문제로 getOffset 사용
        // Timestamp 변환시 UTC 기준으로 로컬타임과 차이 발생하여 2회 적용
        long localDateTime = utcTime + (offset * 2L) ;

        // 현재 날짜 구하기 (시스템 시계, 시스템 타임존)
        return formatDatetime.format(new Timestamp(localDateTime));
    }

    /**
     * timestamp 형식 시간 구하기
     */
    public String getTimestamp() {
        Timestamp time = new Timestamp(System.currentTimeMillis());
        return String.valueOf(time.getTime() / 1000L);
    }

    /**
     * 조회한 날짜를 00년 00월 00일 형식으로 변환
     *
     * @param inputDate (UTC)
     */
    @SneakyThrows
    public String convertDate(String inputDate) {

        SimpleDateFormat formatDatetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TimeZone utcZone = TimeZone.getTimeZone("UTC");
        formatDatetime.setTimeZone(utcZone);

        // String -> Date
        Date formatDate = formatDatetime.parse(inputDate);

        // Date -> String Format set
        String convertRegdate = koFormatDatetime.format(formatDate);

        return convertRegdate;
    }

    /**
     * 시작일과 종료일을 조회해서 현재 진행 중인지 체크
     * 예약중 -> return -1
     * 진행중 -> return 0
     * 완료 -> return 1
     *
     * @param startDate (시작일)(Asia/Seoul)
     * @param endDate (종료일)(Asia/Seoul)
     */
    @SneakyThrows
    public Integer checkProgressState(String startDate, String endDate) {

        // date format
        SimpleDateFormat formatDatetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TimeZone seoulZone = TimeZone.getTimeZone("Asia/Seoul");
        formatDatetime.setTimeZone(seoulZone);

        // return value
        Integer result = null;

        // 현재 시간 Date
        Date nowDate = new Date();

        // String -> Date
        Date formatStartDate = formatDatetime.parse(startDate);
        Date formatEndDate = formatDatetime.parse(endDate);

        // 날짜 비교
        int checkStart = formatStartDate.compareTo(nowDate);
        int checkEnd = formatEndDate.compareTo(nowDate);

        // 예약
        if (checkStart > 0) {
            result = -1;

            // 진행
        } else if (checkStart < 0 && checkEnd > 0) {
            result = 0;

            // 완료
        } else if (checkEnd < 0) {
            result = 1;
        }
        return result;
    }

    /**
     * 시작일과 종료일을 입력 받아 유효한 기간인지 체크
     *
     * @param startDate (시작일)
     * @param endDate (종료일)
     */
    @SneakyThrows
    public boolean checkIsValidPeriod(String startDate, String endDate) {

        // date format
        SimpleDateFormat formatDatetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TimeZone utcZone = TimeZone.getTimeZone("UTC");
        formatDatetime.setTimeZone(utcZone);

        // return value
        boolean result = false;

        // 현재 시간 Date
        Date nowDate = new Date();

        // String -> Date
        Date formatStartDate = formatDatetime.parse(startDate);
        Date formatEndDate = formatDatetime.parse(endDate);

        // 날짜 비교 : 시작일이 종료일보다 이전인지 + 종료일이 시작일보다 이후인지
        if (formatStartDate.compareTo(formatEndDate) < 0 && formatEndDate.compareTo(formatStartDate) > 0) {
            // 시작일과 종료일 모두 현재 시간보다 이후인지
            if (formatStartDate.compareTo(nowDate) > 0 && formatEndDate.compareTo(nowDate) > 0) {
                result = true;
            }
        }
        return result;
    }
}