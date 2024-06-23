package com.architecture.admin.config;

public interface SessionConfig {
    String LOGIN_ID = "id";
    String ADMIN_LEVEL = "adminLevel";
    String ADMIN_INFO = "adminInfo";
    Integer EXPIRED_TIME = 24 * 60 * 60 * 7 * 1000; // 일주일
}