package com.architecture.admin.config.interceptor;

import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.models.daosub.ip.IpDaoSub;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequiredArgsConstructor
public class ipCheckInterceptor implements HandlerInterceptor {

    @Autowired
    private IpDaoSub ipDaoSub;

    /**
     * IP 허용 OR 차단 여부 체크
     *
     * @param httpRequest
     * @param httpResponse
     * @param handler
     * @exception
     * @return
     */
    @Override
    public boolean preHandle(HttpServletRequest httpRequest,
                             HttpServletResponse httpResponse,
                             Object handler) throws Exception {

        // 현재 접근하고 있는 ip 주소 조회
        String ip = getClientIP(httpRequest);

        // DB에 저장된 ip 주소가 맞는지 체크
        int ipCnt = ipDaoSub.checkIsAccessibleIP(ip);

        // 해당 ip를 찾지 못했을 경우
        if (ipCnt <= 0) {

            // 예외 처리
            String message = CustomError.ADMIN_IP_ACCESS_ERROR.getMessage();
            throw new Exception(message + " 현재 접근 중인 IP는 " + ip + " 입니다.");
        }

        // ip를 찾았다면 true 리턴
        return true;
    }

    /**
     * 현재 접근하고 있는 IP 주소 조회
     *
     * @param httpRequest
     * @return
     */
    private static String getClientIP(HttpServletRequest httpRequest) {

        String ip = httpRequest.getHeader("X-Forwarded-For");
        log.info("> X-FORWARDED-FOR : " + ip);

        if (ip == null) {
            ip = httpRequest.getHeader("Proxy-Client-IP");
            log.info("> Proxy-Client-IP : " + ip);
        }

        if (ip == null) {
            ip = httpRequest.getHeader("WL-Proxy-Client-IP");
            log.info(">  WL-Proxy-Client-IP : " + ip);
        }

        if (ip == null) {
            ip = httpRequest.getHeader("HTTP_CLIENT_IP");
            log.info("> HTTP_CLIENT_IP : " + ip);
        }

        if (ip == null) {
            ip = httpRequest.getHeader("HTTP_X_FORWARDED_FOR");
            log.info("> HTTP_X_FORWARDED_FOR : " + ip);
        }

        if (ip == null) {
            ip = httpRequest.getRemoteAddr();
            log.info("> getRemoteAddr : " + ip);
        }

        log.info("> Result : IP Address : " + ip);
        return ip;
    }
}
