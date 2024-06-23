package com.architecture.admin.controllers.event;

import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.models.dto.SearchDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/event")
public class EventController extends BaseController {

    /**
     * 2024 설연휴 이벤트 참여 내역
     *
     * @param model
     * @param page
     * @param searchDto
     * @return
     */
    @GetMapping("/new-year/view")
    public String newYearEventView(Model model,
                                 @RequestParam(required = false, defaultValue = "1") int page,
                                 @ModelAttribute SearchDto searchDto) {
        // 관리자 접근 권한
        super.adminAccessFail(65);

        model.addAttribute("page", page);
        model.addAttribute("search", searchDto);

        hmImportFile.put("importJs", "event/newyear/list.js.html");

        return "event/newyear/list";
    }

    /**
     * 2024 설연휴 이벤트 통계
     *
     * @param model
     * @param page
     * @param searchDto
     * @return
     */
    @GetMapping("/new-year/stat")
    public String newYearEventStat(Model model,
                                @RequestParam(required = false, defaultValue = "1") int page,
                                @ModelAttribute SearchDto searchDto) {
        // 관리자 접근 권한
        super.adminAccessFail(66);

        model.addAttribute("page", page);
        model.addAttribute("search", searchDto);

        hmImportFile.put("importJs", "event/newyear/stat.js.html");

        return "event/newyear/stat";
    }
}
