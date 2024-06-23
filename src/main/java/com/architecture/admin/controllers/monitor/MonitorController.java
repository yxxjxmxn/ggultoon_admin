package com.architecture.admin.controllers.monitor;

import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.models.dto.SearchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/monitor")
public class MonitorController extends BaseController {

    /**
     * Grafana 실서버 모니터링 페이지
     *
     * @param searchDto
     * @return
     */
    @GetMapping()
    public String monitor(@ModelAttribute SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(50);

        return "monitor/prd";
    }
}
