package com.architecture.admin.libraries.filter;

import com.architecture.admin.config.SessionConfig;
import com.architecture.admin.libraries.TelegramLibrary;
import com.architecture.admin.models.dto.admin.AdminDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;

/*****************************************************
 * 로그인 인증 체크 필터
 ****************************************************/
@Component
@Slf4j
public class LoginApiCheckFilterLibrary implements Filter {

    // 해당 리스트에 포함된 경로 처리
    private static final String[] blacklist = {
            // api 로그인 체크 필터 적용 리스트
            "/v1/*",
    };

    private static final String[] whitelist = {
            "/v1/admin/session", // 로그인 세션 에러 페이지 [0] 번째 위치 고정
            "/v1/admin/login", // 로그인
            "/v1/admin/join", // 회원가입
    };

    private boolean bUseLog = false;
    private boolean bUseTelegram = false;

    @Autowired
    private TelegramLibrary telegramLibrary;
    // 생성자 추가
    public LoginApiCheckFilterLibrary() {
        this.telegramLibrary = new TelegramLibrary();
    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            logInfo("인증 체크 필터 시작{}", requestURI);
            if (isLoginCheckPath(requestURI)) {
                logInfo("인증 체크 로직 실행 {}", requestURI);
                HttpSession session = httpRequest.getSession(false);
                if (session == null || session.getAttribute(SessionConfig.LOGIN_ID) == null) {
                    logInfo("미인증 사용자 요청 {}", requestURI);
                    //로그인으로 리다이렉트
                    httpResponse.sendRedirect(whitelist[0]);
                    return;
                }

                //회원 로그인 상태 확인
                ObjectMapper mapper = new ObjectMapper();
                String sAdminInfo = (String) session.getAttribute(SessionConfig.ADMIN_INFO);
                if(sAdminInfo == null){
                    //로그인으로 리다이렉트
                    httpResponse.sendRedirect(whitelist[0]);
                    return;
                }

                //관리자 정보 확인  json -> Object
                AdminDto admin = mapper.readValue(sAdminInfo, AdminDto.class);
                if ( session.getAttribute(SessionConfig.LOGIN_ID) == null || admin.getState() != 1) {
                    logInfo("미인증 사용자 요청 {}", requestURI);
                    //로그인으로 리다이렉트
                    httpResponse.sendRedirect(whitelist[0]);
                    return;
                }
            }
            chain.doFilter(request, response);

        } catch (Exception e) {
            log.info("error {}", requestURI);
            throw e;

        } finally {
            logInfo("인증 체크 필터 종료 {}", requestURI);
        }
    }


    private void logInfo(String sMessage, String requestURI) {
        if (bUseLog) {
            log.info(sMessage, requestURI);
        }
        if (bUseTelegram) {
            HashMap<String, String> hMessage = new HashMap<>();
            hMessage.put("sMessage", sMessage);
            hMessage.put("requestURI", requestURI);
            telegramLibrary.sendMessage(hMessage.toString());
        }
    }

    /**
     * blackList 인증 체크
     */
    private boolean isLoginCheckPath(String requestURI) {
        return PatternMatchUtils.simpleMatch(blacklist, requestURI) && !PatternMatchUtils.simpleMatch(whitelist, requestURI);
    }
}
