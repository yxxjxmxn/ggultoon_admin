package com.architecture.admin.controllers.comment;

import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.models.dto.SearchDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/comments")
public class CommentController extends BaseController {

    /**
     * 작품 목록
     * @param model
     * @param page
     * @param searchDto
     * @return
     */
    @GetMapping("/contents")
    public String contentList(Model model,
                              @RequestParam(required = false, defaultValue = "1") int page,
                              @ModelAttribute SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(34);

        model.addAttribute("page", page);
        model.addAttribute("search", searchDto);

        hmImportFile.put("importJs", "comment/list.js.html");

        return "comment/list";
    }

    /**
     * 회차 목록
     * @param idx
     * @param page
     * @param searchDto
     * @return
     */
    @GetMapping("/contents/{idx}/episodes")
    public String episodeList(Model model,
                              @PathVariable(required = false) Integer idx,
                              @RequestParam(required = false, defaultValue = "1") int page,
                              @ModelAttribute SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(34);

        model.addAttribute("page", page);
        model.addAttribute("search", searchDto);
        model.addAttribute("idx", idx);

        hmImportFile.put("importJs", "comment/episodeList.js.html");

        return "comment/episodeList";
    }

    /**************************************************************************************
     * 작품 댓글 & 대댓글 목록
     **************************************************************************************/

    /**
     * 작품 댓글 목록
     * @param idx
     * @param searchDto
     * @return
     */
    @GetMapping("/contents/{idx}")
    public String contentCommentList(Model model,
                                      @PathVariable(required = false) Integer idx,
                                      @RequestParam(required = false, defaultValue = "1") int page,
                                      @ModelAttribute SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(34);

        model.addAttribute("page", page);
        model.addAttribute("search", searchDto);
        model.addAttribute("idx", idx);

        hmImportFile.put("importJs", "comment/view.js.html");

        return "comment/view";
    }

    /**
     * 작품 대댓글 목록
     * @param idx
     * @param searchDto
     * @return
     */
    @GetMapping("/contents/replies/{idx}")
    public String contentReplyList(Model model,
                                      @PathVariable(required = false) Integer idx,
                                      @RequestParam(required = false, defaultValue = "1") int page,
                                      @ModelAttribute SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(10);

        model.addAttribute("page", page);
        model.addAttribute("search", searchDto);
        model.addAttribute("idx", idx);

        hmImportFile.put("importJs", "comment/reply.js.html");

        return "comment/reply";
    }

    /**************************************************************************************
     * 회차 댓글 목록
     **************************************************************************************/

    /**
     * 회차 댓글 목록
     * @param contentsIdx
     * @param episodeIdx
     * @param searchDto
     * @return
     */
    @GetMapping("/contents/{contentsIdx}/episodes/{episodeIdx}")
    public String episodeCommentList(Model model,
                                      @PathVariable(required = false) Integer contentsIdx,
                                      @PathVariable(required = false) Long episodeIdx,
                                      @RequestParam(required = false, defaultValue = "1") int page,
                                      @ModelAttribute SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(10);

        model.addAttribute("page", page);
        model.addAttribute("search", searchDto);
        model.addAttribute("contentsIdx", contentsIdx);
        model.addAttribute("episodeIdx", episodeIdx);

        hmImportFile.put("importJs", "comment/episodeView.js.html");

        return "comment/episodeView";
    }
}
