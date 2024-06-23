package com.architecture.admin.api.v1.statistics;


import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.libraries.TelegramLibrary;
import com.architecture.admin.services.statistics.StatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/stat")
public class StatV1Controller extends BaseController {

    private final StatService statService;

    private final TelegramLibrary telegramLibrary;

    /**
     * 정산 통계 수동
     * @param nowDate
     * @return
     */
    @GetMapping("/{nowDate}")
    public String stat(@PathVariable(name = "nowDate", required = false) String nowDate) {
        Integer result = 0;

        try {
            if (nowDate.trim() != null && nowDate.trim() != "") {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
                sdf.setLenient(false);

                String inputDate = nowDate.concat(" 03:00:00").toString();
                Date date = sdf.parse(inputDate);

                result = statService.stat(date);
            }
        } catch (ParseException parseException) {
            parseException.printStackTrace();
            telegramLibrary.sendMessage("parseException : " + parseException.toString(), "STAT");
        } catch (Exception e) {
            e.printStackTrace();
            telegramLibrary.sendMessage("exception : " + e.toString(), "STAT");
        }

        String sMessage = super.langMessage("lang.contents.exception.register_fail");
        if (result > 0) {
            sMessage = super.langMessage("lang.contents.success.register");
        }

        return displayJson(true, "1000", sMessage);
    }
}
