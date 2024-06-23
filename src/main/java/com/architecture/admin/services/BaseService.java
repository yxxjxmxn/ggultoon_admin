package com.architecture.admin.services;

import com.architecture.admin.config.SessionConfig;
import com.architecture.admin.libraries.*;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dto.admin.AdminDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

import static com.architecture.admin.config.SessionConfig.ADMIN_INFO;

/*****************************************************
 * 코어 서비스
 ****************************************************/
@Service
public class BaseService {
    // 시간 라이브러리 참조
    @Autowired
    protected DateLibrary dateLibrary;

    // 암호화 라이브러리
    @Autowired
    protected SecurityLibrary securityLibrary;

    // 세션
    @Autowired
    protected HttpSession session;

    // 텔레그램
    @Autowired
    protected TelegramLibrary telegramLibrary;

    // Redis 라이브러리
    @Autowired
    protected RedisLibrary redisLibrary;

    // Curl 라이브러리
    @Autowired
    protected CurlLibrary curlLibrary;

    /**
     * 메시지 가져오는 라이브러리
     */
    @Autowired
    protected MessageSource messageSource;

    /*****************************************************
     * 회원정보 레디스 불러오기
     * 리턴되는 회원정보가 String 형식이며, 사용시 불편 => ADMIN_INFO session 사용 할 것
     ****************************************************/
    public String getMemberInfo() {
        // 세션 생성
        String sId = (String) session.getAttribute(SessionConfig.LOGIN_ID);

        // 세션 정보 레디스에 적용
        String sKey = "session_" + sId;
        return getRedis(sKey);
    }

    /*****************************************************
     * 회원정보 SESSION 불러오기
     * session 의 SessionConfig.ADMIN_INFO 이용
     * {"idx":1,"adminIdx":22,"id":"test@test.com","level":1,"state":1,"lastLoginDate":"2022-10-20 14:14:39","regdate":"2022-10-18 17:35:42","insertedId":null,"lastDateRow":null}
     ****************************************************/
    public AdminDto getAdminSessionInfo() throws JsonProcessingException {
        // 세션 생성
        ObjectMapper mapper = new ObjectMapper();
        String sAdminInfo = String.valueOf(session.getAttribute(SessionConfig.ADMIN_INFO));

        return mapper.readValue(sAdminInfo, AdminDto.class);// AdminDto
    }

    /*****************************************************
     * 레디스
     ****************************************************/
    // 레디스 값 생성
    public void setRedis(String key, String value, Integer expiredSeconds) {
        redisLibrary.setData(key, value, expiredSeconds);
    }

    // 레디스 값 불러오기
    public String getRedis(String key) {
        return redisLibrary.getData(key);
    }

    // 레디스 값 삭제하기
    public void removeRedis(String key) {
        redisLibrary.deleteData(key);
    }

    /*****************************************************
     * Curl
     ****************************************************/
    // get
    public String getCurl(String url) {
        return curlLibrary.get(url);
    }

    // post
    public String postCurl(String url, Map dataset) {
        return curlLibrary.post(url, dataset);
    }

    /*****************************************************
     * 암호화 처리
     ****************************************************/
    // 양방향 암호화 암호화
    public String encrypt(String str) throws Exception {
        return securityLibrary.aesEncrypt(str);
    }

    // 양방향 암호화 복호화
    public String decrypt(String str) throws Exception {
        return securityLibrary.aesDecrypt(str);
    }

    // 단방향 암호화
    public String md5encrypt(String str) {
        return securityLibrary.md5Encrypt(str);
    }

    /*****************************************************
     * 디버깅
     ****************************************************/
    public void d() {
        int iSeq = 2;
        System.out.println("======================================================================");
        System.out.println("클래스명 : " + Thread.currentThread().getStackTrace()[iSeq].getClassName());
        System.out.println("메소드명 : " + Thread.currentThread().getStackTrace()[iSeq].getMethodName());
        System.out.println("줄번호 : " + Thread.currentThread().getStackTrace()[iSeq].getLineNumber());
        System.out.println("파일명 : " + Thread.currentThread().getStackTrace()[iSeq].getFileName());
    }

    /*****************************************************
     * Language 값 가져오기
     ****************************************************/
    public String langMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }

    public String langMessage(String code, @Nullable Object[] args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }

    /*****************************************************
     * 세션 값 가져오기
     ****************************************************/
    public String getSession(String id) {
        return (String) session.getAttribute(id);
    }

    /*****************************************************
     * 관리자 정보 불러오기
     ****************************************************/
    public String getAdminInfo(String key) {

        JSONObject json = new JSONObject(getSession(SessionConfig.ADMIN_INFO));

        return json.getString(key);
    }
    /*****************************************************
     * ip 값 가져오기
     * private => public으로 변환
     ****************************************************/
    public String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-FORWARDED-FOR");
        if (ip == null) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    public void pushAlarm(String sendMessage) {
        telegramLibrary.sendMessage(sendMessage);
    }

    public void pushAlarm(String sendMessage, String sChatId) {
        telegramLibrary.sendMessage(sendMessage, sChatId);
    }

    /*****************************************************
     * 적용 설정 언어
     * 현재 적용된 lang 리턴
     ****************************************************/
    public String getLocalLang() {
        return String.valueOf(LocaleContextHolder.getLocale());
    }

    /*****************************************************
     * 관리자 접근 권한 체크
     ****************************************************/
    
    /**
     * 세션에 저장된 관리자 접근 권한 체크
     * 7레벨 이하일 경우 등록, 삭제, 수정 불가
     */
    public void checkAdminAccessLevel() {

        // 로그인한 관리자 정보
        JSONObject adminInfo = new JSONObject(session.getAttribute(ADMIN_INFO).toString());

        // 관리자의 접근 레벨 정보
        Integer adminLevel = (Integer)adminInfo.get("level");

        // 현재 로그인한 관리자의 레벨이 7레벨보다 낮은 경우
        if (adminLevel < 7) {

            // 오류 발생시 텔레그램 알람, 메인페이지 이동 처리
            throw new CustomException(CustomError.ACCESS_FAIL);
        }
    }
}
