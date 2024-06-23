package com.architecture.admin.controllers.board;

import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.models.dto.SearchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RequiredArgsConstructor
@Controller
@RequestMapping("/boards")
public class BoardController extends BaseController {

    /**
     * 공지사항 리스트
     * @param searchDto
     * @return
     */
    @GetMapping("/notices")
    public String noticeList(Model model,
                              @RequestParam(required = false, defaultValue = "1") int page,
                              @ModelAttribute SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(39);

        model.addAttribute("page", page);
        model.addAttribute("search", searchDto);

        hmImportFile.put("importJs", "board/notice/list.js.html");

        return "board/notice/list";
    }

    /**
     * 공지사항 상세
     *
     * @param idx
     * @param model
     * @return
     */
    @GetMapping("/notices/{idx}")
    public String viewNotice(@PathVariable(required = false) Long idx, Model model) {

        // 관리자 접근 권한
        super.adminAccessFail(39);

        model.addAttribute("idx", idx);

        // 공지사항 타입 구분값 리스트
        HashMap<Integer, String> noticeType = new HashMap<>();
        noticeType.put(1, "TEXT");
        noticeType.put(2, "HTML");

        // set view data
        hmDataSet.put("noticeType", noticeType);

        hmImportFile.put("importJs", "board/notice/view.js.html");

        return "board/notice/view";
    }

    /**
     * 공지사항 등록
     */
    @GetMapping("/notices/register")
    public String registerNotice() {

        // 관리자 접근 권한
        super.adminAccessFail(39);

        // 공지사항 타입 구분값 리스트
        HashMap<Integer, String> noticeType = new HashMap<>();
        noticeType.put(1, "TEXT");
        noticeType.put(2, "HTML");

        // set view data
        hmDataSet.put("noticeType", noticeType);

        hmImportFile.put("importJs", "board/notice/register.js.html");

        return "board/notice/register";
    }
}
