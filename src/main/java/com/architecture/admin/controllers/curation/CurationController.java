package com.architecture.admin.controllers.curation;

import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.models.dto.SearchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/curations")
public class CurationController extends BaseController {

    /**************************************************************************************
     * 큐레이션 리스트
     **************************************************************************************/

    /**
     * 작품 큐레이션 리스트 조회
     * @param model
     * @param page
     * @param searchDto
     * @return
     */
    @GetMapping()
    public String getCurationList(Model model,
                              @RequestParam(required = false, defaultValue = "1") int page,
                              @ModelAttribute SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(56);

        model.addAttribute("page", page);
        model.addAttribute("search", searchDto);

        hmImportFile.put("importJs", "curation/list.js.html");

        return "curation/list";
    }

    /**
     * 작품 큐레이션 상세
     * @param idx
     * @return
     */
    @GetMapping("/{idx}")
    public String getViewCuration(@PathVariable(required = false) Long idx, Model model) {

        // 관리자 접근 권한
        super.adminAccessFail(56);

        model.addAttribute("idx", idx);

        hmImportFile.put("importJs", "curation/view.js.html");

        return "curation/view";
    }


    /**
     * 작품 큐레이션 등록
     */
    @GetMapping("/register")
    public String registerCuration() {

        // 관리자 접근 권한
        super.adminAccessFail(56);

        // set view data
        hmImportFile.put("importJs", "curation/register.js.html");

        return "curation/register";
    }

    /**************************************************************************************
     * 큐레이션 노출 영역 리스트
     **************************************************************************************/

    /**
     * 작품 노출 영역 리스트 조회
     * @param model
     * @param page
     * @param searchDto
     * @return
     */
    @GetMapping("/areas")
    public String getCurationAreaList(Model model,
                                      @RequestParam(required = false, defaultValue = "1") int page,
                                      @ModelAttribute SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(57);

        model.addAttribute("page", page);
        model.addAttribute("search", searchDto);

        hmImportFile.put("importJs", "curation/area/list.js.html");

        return "curation/area/list";
    }

    /**
     * 작품 큐레이션 노출 영역 상세
     * @param idx
     * @return
     */
    @GetMapping("/areas/{idx}")
    public String getViewCurationArea(@PathVariable(required = false) Long idx, Model model) {

        // 관리자 접근 권한
        super.adminAccessFail(57);

        model.addAttribute("idx", idx);

        hmImportFile.put("importJs", "curation/area/view.js.html");

        return "curation/area/view";
    }

    /**
     * 작품 큐레이션 노출 영역 등록
     */
    @GetMapping("/areas/register")
    public String registerCurationArea() {

        // 관리자 접근 권한
        super.adminAccessFail(57);

        // set view data
        hmImportFile.put("importJs", "curation/area/register.js.html");

        return "curation/area/register";
    }

    /**************************************************************************************
     * 큐레이션 매핑 작품 리스트
     **************************************************************************************/

    /**
     * 큐레이션 매핑 작품 리스트 조회
     * @param idx
     * @param page
     * @param searchDto
     * @param model
     * @return
     */
    @GetMapping("/{idx}/contents")
    public String getCurationContentList(@PathVariable(required = false) Long idx,
                                         @RequestParam(required = false, defaultValue = "1") int page,
                                         @ModelAttribute SearchDto searchDto,
                                         Model model) {

        // 관리자 접근 권한
        super.adminAccessFail(56);

        model.addAttribute("idx", idx);
        model.addAttribute("page", page);
        model.addAttribute("search", searchDto);

        hmImportFile.put("importJs", "curation/content/list.js.html");

        return "curation/content/list";
    }

    /**
     * 큐레이션 매핑 작품 상세
     * @param idx
     * @param contentsIdx
     * @return
     */
    @GetMapping("/{idx}/contents/{contentsIdx}")
    public String getViewCurationArea(@PathVariable(required = false) Long idx,
                                      @PathVariable(required = false) Long contentsIdx,
                                      Model model) {

        // 관리자 접근 권한
        super.adminAccessFail(56);

        model.addAttribute("idx", idx);
        model.addAttribute("contentsIdx", contentsIdx);

        hmImportFile.put("importJs", "curation/content/view.js.html");

        return "curation/content/view";
    }

    /**
     * 큐레이션 매핑 작품 등록
     */
    @GetMapping("/{idx}/contents/register")
    public String registerCurationContent(@PathVariable(required = false) Long idx,
                                          Model model) {

        // 관리자 접근 권한
        super.adminAccessFail(56);

        // set view data
        model.addAttribute("idx", idx);
        hmImportFile.put("importJs", "curation/content/register.js.html");

        return "curation/content/register";
    }

    /**
     * 큐레이션 매핑 작품 변경
     */
    @GetMapping("/{idx}/contents/{contentsIdx}/modify")
    public String modifyCurationContent(@PathVariable(required = false) Long idx,
                                        @PathVariable(required = false) Long contentsIdx,
                                        Model model) {

        // 관리자 접근 권한
        super.adminAccessFail(56);

        // set view data
        model.addAttribute("idx", idx);
        model.addAttribute("contentsIdx", contentsIdx);
        hmImportFile.put("importJs", "curation/content/modify.js.html");

        return "curation/content/modify";
    }
}
