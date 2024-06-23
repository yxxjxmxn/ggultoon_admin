package com.architecture.admin.config;

import com.architecture.admin.config.interceptor.ipCheckInterceptor;
import com.architecture.admin.libraries.filter.Filter;
import com.architecture.admin.libraries.filter.LogFilterLibrary;
import com.architecture.admin.libraries.filter.LoginApiCheckFilterLibrary;
import com.architecture.admin.libraries.filter.LoginCheckFilterLibrary;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*****************************************************
 * 필터 & 인터셉터 등록
 ****************************************************/
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Bean
    public FilterRegistrationBean<Filter> logFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new LogFilterLibrary()); // 등록 할 필터를 지정
        filterRegistrationBean.setOrder(1);  // 순서가 낮을수록 먼저 동작한다.
        filterRegistrationBean.addUrlPatterns("/*"); // 필터를 적용할 URL 패턴을 지정

        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean<Filter> loginCheckFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new LoginCheckFilterLibrary());
        filterRegistrationBean.setOrder(2);
        filterRegistrationBean.addUrlPatterns("/*");

        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean<Filter> loginApiCheckFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new LoginApiCheckFilterLibrary());
        filterRegistrationBean.setOrder(3);
        filterRegistrationBean.addUrlPatterns("/*");

        return filterRegistrationBean;
    }

    /*
    @Override
    public void addInterceptors(InterceptorRegistry reg){
        reg.addInterceptor(jwtInterceptor())
                .order(1)
                .addPathPatterns("/**"); // interceptor 작업이 필요한 path를 모두 추가한다.
        // .excludePathPatterns("app/accounts","/app/accounts/auth","app/videos/**");
        // 인가작업에서 제외할 API 경로를 따로 추가할수도 있으나, 일일히 따로 추가하기 어려우므로 어노테이션을 따로 만들어 해결한다.
    }

    @Bean
    public JwtInterceptor jwtInterceptor() {
        JwtLibrary jwtLibrary = new JwtLibrary();
        TelegramLibrary telegramLibrary = new TelegramLibrary();
        return new JwtInterceptor(jwtLibrary , telegramLibrary);
    }
     */

    @Bean
    public ipCheckInterceptor ipCheckInterceptor() {
        return new ipCheckInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry reg){
        reg.addInterceptor(ipCheckInterceptor()) // ip 체크 인터셉터 추가
                .order(1) // 인터셉터 우선순위 -> 1순위로 set
                .addPathPatterns("/**"); // 등록하려는 ip 체크 인터셉터를 적용시킬 경로 -> 전체 경로로 set
    }
}
