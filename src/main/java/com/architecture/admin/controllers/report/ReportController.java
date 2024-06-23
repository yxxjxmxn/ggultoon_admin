package com.architecture.admin.controllers.report;

import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.models.dto.SearchDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/reports")
public class ReportController extends BaseController {

    /**************************************************************************************
     * 신고 내역
     **************************************************************************************/

    /**
     * 작품 신고 내역
     * @param searchDto
     * @return
     */
    @GetMapping("/contents")
    public String reportContentList(Model model,
                                    @RequestParam(required = false, defaultValue = "1") int page,
                                    @ModelAttribute SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(29);

        model.addAttribute("page", page);
        model.addAttribute("search", searchDto);

        hmImportFile.put("importJs", "report/content/list.js.html");

        return "report/content/list";
    }

    /**
     * 작품 댓글 신고 내역
     * @param searchDto
     * @return
     */
    @GetMapping("/contents/comments")
    public String reportContentCommentList(Model model,
                                    @RequestParam(required = false, defaultValue = "1") int page,
                                    @ModelAttribute SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(30);

        model.addAttribute("page", page);
        model.addAttribute("search", searchDto);

        hmImportFile.put("importJs", "report/comment/content/list.js.html");

        return "report/comment/content/list";
    }

    /**
     * 회차 댓글 신고 내역
     * @param searchDto
     * @return
     */
    @GetMapping("/episodes/comments")
    public String reportEpisodeCommentList(Model model,
                                           @RequestParam(required = false, defaultValue = "1") int page,
                                           @ModelAttribute SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(31);

        model.addAttribute("page", page);
        model.addAttribute("search", searchDto);

        hmImportFile.put("importJs", "report/comment/episode/list.js.html");

        return "report/comment/episode/list";
    }
}
