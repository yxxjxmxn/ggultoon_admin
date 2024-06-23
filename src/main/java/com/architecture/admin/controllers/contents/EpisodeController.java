package com.architecture.admin.controllers.contents;

import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.models.dto.SearchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/episode")
public class EpisodeController extends BaseController {


    /**
     * 회차 목록
     * @param searchDto
     * @return
     */
    @GetMapping("/list")
    public String lists(@ModelAttribute("params") SearchDto searchDto, @RequestParam("contentsIdx") String contentsIdx, Model model) {

        model.addAttribute("contentsIdx", Integer.parseInt(contentsIdx));

        hmImportFile.put("importJs", "contents/episode/list.js.html");

        return "contents/episode/list";
    }


    /**
     * 회차 등록
     * @param searchDto
     * @return
     */
    @GetMapping("/register")
    public String register(@ModelAttribute("params") SearchDto searchDto, @RequestParam("contentsIdx") String contentsIdx, Model model) {

        model.addAttribute("contentsIdx", Integer.parseInt(contentsIdx));

        hmImportFile.put("importJs", "contents/episode/register.js.html");

        return "contents/episode/register";
    }


    /**
     * 회차 상세
     * @param searchDto
     * @param idx
     * @return
     */
    @GetMapping("/view/{idx}")
    public String view(@ModelAttribute("params") SearchDto searchDto, @PathVariable("idx") Integer idx, @RequestParam("contentsIdx") String contentsIdx, Model model) {

        model.addAttribute("contentsIdx", Integer.parseInt(contentsIdx));

        hmImportFile.put("importJs", "contents/episode/view.js.html");

        return "contents/episode/view";
    }
}
