package com.architecture.admin.api.v1.banner;

import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.banner.BannerDto;
import com.architecture.admin.services.banner.BannerService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/banner")
public class BannerV1Controller extends BaseController {

    private final BannerService bannerService;

    @GetMapping()
    public String lists(@ModelAttribute("params") SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(59);

        // 배너 목록
        List<BannerDto> list = bannerService.getList(searchDto);

        Map<String, Object> map = new HashMap<>();
        map.put("list", list);
        map.put("params", searchDto);

        JSONObject data = new JSONObject(map);
        String sMessage = "";

        if (list.isEmpty()) {
            sMessage = super.langMessage("lang.banner.exception.search_fail"); // 검색 결과가 없습니다.
        }

        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 배너 등록
     * @param bannerDto
     * @return
     */
    @PostMapping()
    public String register(@ModelAttribute("BannerDto") BannerDto bannerDto ) {

        // 관리자 접근 권한
        super.adminAccessFail(59);

        // insert idx
        Integer result = bannerService.register(bannerDto);

        HashMap<String, Object> map = new HashMap<>();
        map.put("bannerIdx", result);

        // 등록 실패(default)
        String sMessage = super.langMessage("lang.banner.exception.register_fail");
        // 등록 성공
        if (result != null && result > 0) {
            sMessage = super.langMessage("lang.banner.success.register");
        }

        // response object
        JSONObject data = new JSONObject(map);

        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 배너 상세
     * @param idx
     * @return
     */
    @GetMapping("/{idx}")
    public String view(@PathVariable(name = "idx", required = false) Integer idx) {
        // 컨텐츠 상세
        BannerDto view = bannerService.getView(idx);

        Map<String, Object> map = new HashMap<>();
        map.put("view", view);

        // response object
        JSONObject data = new JSONObject(map);
        String sMessage = "";

        if (view == null) {
            // 검색 결과가 없습니다.
            sMessage = super.langMessage("lang.banner.exception.search_fail");
        }

        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 배너 수정
     * @param bannerDto
     * @return
     */
    @PutMapping()
    public String modify(@ModelAttribute("BannerDto") BannerDto bannerDto) {

        // 배너 수정
        Integer result = bannerService.modify(bannerDto);

        // 수정 실패
        String sMessage = super.langMessage("lang.contents.exception.modify_fail");
        // 수정 성공
        if (result > 0) {
            sMessage = super.langMessage("lang.contents.success.modify");
        }

        // response object
        JSONObject data = new JSONObject(result);

        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 배너 이미지 삭제
     * @param idx
     * @return
     */
    @DeleteMapping("/image/{idx}")
    public String deleteBannerImage(@PathVariable(name = "idx", required = false) Integer idx) {
        Integer result = bannerService.deleteBannerImage(idx);

        String sMessage = super.langMessage("lang.banner.exception.image.delete_fail");
        // 삭제 완료
        if (result > 0) {
            sMessage = super.langMessage("lang.banner.success.image.delete");
        }
        // response object
        JSONObject data = new JSONObject(result);

        return displayJson(true, "1000", sMessage, data);
    }

}
