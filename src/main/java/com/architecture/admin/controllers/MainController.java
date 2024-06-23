package com.architecture.admin.controllers;

import com.architecture.admin.config.SessionConfig;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*****************************************************
 * 메인페이지
 ****************************************************/
@Slf4j
@Controller
public class MainController extends BaseController {
    @RequestMapping("/")
    public String main(@RequestParam(value="pid", defaultValue = "") String sPid) {
        JSONObject json = new JSONObject(super.getSession(SessionConfig.ADMIN_INFO));

        hmDataSet.put("utctime", dateLibrary.getDatetime());
        hmDataSet.put("datetime", dateLibrary.utcToLocalTime(dateLibrary.getDatetime()));
        hmDataSet.put("timestamp", dateLibrary.getTimestamp());
        hmDataSet.put("sPid", sPid);
        hmDataSet.put("sSession", super.getSession(SessionConfig.LOGIN_ID));
        hmDataSet.put("sMemberInfo", json.toString());

        return "main";
    }
}
