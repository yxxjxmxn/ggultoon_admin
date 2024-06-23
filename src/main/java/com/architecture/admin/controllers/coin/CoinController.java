package com.architecture.admin.controllers.coin;

import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.models.dto.SearchDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/coins/")
public class CoinController extends BaseController {

    /**
     * 코인 적립 리스트
     *
     * @param model
     * @param page
     * @param searchDto
     * @return
     */
    @GetMapping("/coin/save")
    public String savedCoinList(Model model,
                                @RequestParam(required = false, defaultValue = "1") int page,
                                @ModelAttribute SearchDto searchDto) {
        // 관리자 접근 권한
        super.adminAccessFail(6);

        model.addAttribute("page", page);
        model.addAttribute("search", searchDto);

        hmImportFile.put("importJs", "coin/list.js.html");

        return "coin/list";
    }

    /**
     * 마일리지 적립 리스트
     *
     * @param model
     * @param page
     * @param searchDto
     * @return
     */
    @GetMapping("/mileage/save")
    public String savedMileageList(Model model,
                                   @RequestParam(required = false, defaultValue = "1") int page,
                                   @ModelAttribute SearchDto searchDto) {
        // 관리자 접근 권한
        super.adminAccessFail(7);

        model.addAttribute("page", page);
        model.addAttribute("search", searchDto);

        hmImportFile.put("importJs", "coin/mileage/list.js.html");

        return "coin/mileage/list";
    }

    /**
     * 코인 사용 리스트
     *
     * @param model     : page & searchDto 담음
     * @param page      : 페이지 디폴트 1
     * @param searchDto : searchCount, searchType, searchWord, state 넘겨 받음
     * @return
     */
    @GetMapping("/coin/use")
    public String usedCoinList(Model model,
                               @RequestParam(required = false, defaultValue = "1") int page,
                               @ModelAttribute SearchDto searchDto) {
        // 관리자 접근 권한
        super.adminAccessFail(13);

        model.addAttribute("page", page);
        model.addAttribute("search", searchDto);

        hmImportFile.put("importJs", "coin/useList.js.html");

        return "coin/useList";
    }

    /**
     * 마일리지 사용 리스트
     *
     * @param model
     * @param page
     * @param searchDto
     * @return
     */
    @GetMapping("/mileage/use")
    public String usedMileageList(Model model,
                                  @RequestParam(required = false, defaultValue = "1") int page,
                                  @ModelAttribute SearchDto searchDto) {
        // 관리자 접근 권한
        super.adminAccessFail(14);

        model.addAttribute("page", page);
        model.addAttribute("search", searchDto);

        hmImportFile.put("importJs", "coin/mileage/useList.js.html");

        return "coin/mileage/useList";
    }

    /**
     * 관리자 지급 & 차감 내역
     *
     * @param model
     * @param page
     * @param searchDto
     * @return
     */
    @GetMapping("/coin/admin")
    public String coinAdminList(Model model,
                                  @RequestParam(required = false, defaultValue = "1") int page,
                                  @ModelAttribute SearchDto searchDto) {
        // 관리자 접근 권한
        super.adminAccessFail(14);

        model.addAttribute("page", page);
        model.addAttribute("search", searchDto);

        hmImportFile.put("importJs", "coin/admin/list.js.html");

        return "coin/admin/list";
    }

}
