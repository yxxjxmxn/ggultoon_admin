package com.architecture.admin.controllers.notification;

import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.models.dto.SearchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/notifications")
public class NotificationController extends BaseController {

    /**
     * 회원 알림 리스트
     * @param searchDto
     * @return
     */
    @GetMapping()
    public String notificationList(Model model,
                                  @RequestParam(required = false, defaultValue = "1") int page,
                                  @ModelAttribute SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(37);

        model.addAttribute("page", page);
        model.addAttribute("search", searchDto);

        hmImportFile.put("importJs", "notification/list.js.html");

        return "notification/list";
    }

    /**
     * 회원 알림 상세
     *
     * @param idx
     * @param model
     * @return
     */
    @GetMapping("/{idx}")
    public String viewNotification(Model model,
                                   @PathVariable(required = false) Long idx) {

        // 관리자 접근 권한
        super.adminAccessFail(37);

        model.addAttribute("idx", idx);

        hmImportFile.put("importJs", "notification/view.js.html");

        return "notification/view";
    }
}
