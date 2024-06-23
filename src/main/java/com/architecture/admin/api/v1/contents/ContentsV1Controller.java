package com.architecture.admin.api.v1.contents;

import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.contents.ContentsDto;
import com.architecture.admin.services.contents.ContentsService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/cp-admin/contents")
public class ContentsV1Controller extends BaseController {

    private final ContentsService contentsService;

    /**
     * 컨텐츠 목록
     * @param searchDto
     * @return
     */
    @GetMapping()
    public String lists(@ModelAttribute("params") SearchDto searchDto) {

        // 컨텐츠 목록
        List<ContentsDto> list = contentsService.getList(searchDto);

        Map<String, Object> map = new HashMap<>();
        map.put("list", list);
        map.put("params", searchDto);

        JSONObject data = new JSONObject(map);
        String sMessage = "";

        if (list.isEmpty()) {
            sMessage = super.langMessage("lang.contents.exception.search_fail"); // 검색 결과가 없습니다.
        }

        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 컨텐츠 등록
     * @param contentsDto
     * @return
     */
    @PostMapping()
    public String register(@ModelAttribute("ContentsDto") ContentsDto contentsDto) {

        // 컨텐츠 등록
        Integer result = contentsService.register(contentsDto);

        HashMap<String, Object> map = new HashMap<>();
        map.put("contentsIdx", result);

        // 등록 실패(default)
        String sMessage = super.langMessage("lang.contents.exception.register_fail");
        // 등록 성공
        if (result > 0) {
            sMessage = super.langMessage("lang.contents.success.register");
        }

        // response object
        JSONObject data = new JSONObject(map);

        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 컨텐츠 상세
     * @param idx
     * @return
     */
    @GetMapping("/{idx}")
    public String view(@PathVariable(name = "idx", required = false) Integer idx) {

        // 컨텐츠 상세
        ContentsDto view  = contentsService.getView(idx);

        Map<String, Object> map = new HashMap<>();
        map.put("view", view);

        // response object
        JSONObject data = new JSONObject(map);
        String sMessage = "";

        if (view == null) {
            // 검색 결과가 없습니다.
            sMessage = super.langMessage("lang.contents.exception.search_fail");
        }

        return displayJson(true, "1000", sMessage, data);
    }


    /**
     * 컨텐츠 수정
     * @param contentsDto
     * @return
     */
    @PutMapping()
    public String modifyContent(@ModelAttribute("ContentsDto") ContentsDto contentsDto) {

        // 컨텐츠 수정
        Integer result = contentsService.modifyContent(contentsDto);

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
     * 컨텐츠 이미지 삭제
     * @param idx
     * @return
     */
    @DeleteMapping("/image/{idx}")
    public String deleteContentImage(@PathVariable(name = "idx", required = false) Integer idx) {

        Integer result = contentsService.deleteContentImage(idx);

        String sMessage = super.langMessage("lang.contents.exception.image.delete_fail");
        // 삭제 완료
        if (result > 0) {
            sMessage = super.langMessage("lang.contents.success.image.delete");
        }
        // response object
        JSONObject data = new JSONObject(result);

        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 카테고리별 장르 목록
     * @param categoryIdx
     * @return
     */
    @GetMapping("/category/{idx}")
    public String genreList(@PathVariable(name = "idx", required = false) Integer categoryIdx) {

        // 선택한 카테고리 idx set
        SearchDto searchDto = new SearchDto();
        searchDto.setCategoryIdx(categoryIdx);

        // 카테고리별 장르 목록
        List<ContentsDto> list = contentsService.getGenreList(searchDto);

        // map set
        Map<String, Object> map = new HashMap<>();
        map.put("list", list);

        JSONObject data = new JSONObject(map);
        String sMessage = "";

        if (list.isEmpty()) {
            sMessage = super.langMessage("lang.contents.exception.search_fail"); // 검색 결과가 없습니다.
        }
        return displayJson(true, "1000", sMessage, data);
    }

}
