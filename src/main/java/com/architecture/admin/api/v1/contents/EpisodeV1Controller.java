package com.architecture.admin.api.v1.contents;

import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.contents.*;
import com.architecture.admin.services.contents.ContentsService;
import com.architecture.admin.services.contents.EpisodeService;
import com.architecture.admin.services.contents.ViewerService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/cp-admin/episode")
public class EpisodeV1Controller extends BaseController {

    private final EpisodeService episodeService;
    private final ContentsService contentsService;
    private final ViewerService viewerService;


    /**
     * 회차 목록
     * @param searchDto
     * @param contentsIdx
     * @param model
     * @return
     */
    @GetMapping()
    public String lists(@ModelAttribute("params") SearchDto searchDto, @RequestParam("contentsIdx") String contentsIdx, Model model) {

        model.addAttribute("contentsIdx", Integer.parseInt(contentsIdx));

        List<EpisodeDto> list = episodeService.getList(Integer.parseInt(contentsIdx));

        Map<String, Object> map = new HashMap<>();
        map.put("list", list);

        JSONObject data = new JSONObject(map);
        String sMessage = "";

        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 회차 등록
     * @param episodeDto
     * @return
     */
    @PostMapping()
    public String register(@ModelAttribute("EpisodeDto") EpisodeDto episodeDto) {

        // 회차 등록
        Long result = episodeService.register(episodeDto);

        HashMap<String, Object> map = new HashMap<>();
        map.put("episodeIdx", result);

        String sMessage = "";
        // 등록 완료
        if (result > 0) {
            sMessage = super.langMessage("lang.episode.success.register");
        }
        // 등록 실패
        else {
            sMessage = super.langMessage("lang.episode.exception.register_fail");
        }
        // response object
        JSONObject data = new JSONObject(map);

        return displayJson(true, "1000", sMessage, data);
    }


    /**
     * 컨텐츠 정보 및 회차 순서 정보
     * @param contentsIdx
     * @return
     */
    @GetMapping("/info")
    public String getInfo(@RequestParam("contentsIdx") String contentsIdx) {

        Integer idx = Integer.parseInt(contentsIdx);

        // 컨텐츠 정보
        ContentsDto contents = contentsService.getView(idx);

        // 회차 등록할 번호(episodeNumber, sort) - 마지막 회차 번호 + 1
        Map<String, Object> map = episodeService.getLastEpisodeOrder(idx);
        map.put("contents", contents);

        // response object
        JSONObject data = new JSONObject(map);
        String sMessage = "";

        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 회차 상세
     * @param contentsIdx
     * @param idx
     * @return
     */
    @GetMapping("/{idx}")
    public String view(@PathVariable(name = "idx", required = false) Integer idx, @RequestParam("contentsIdx") String contentsIdx) {
        // 컨텐츠 정보
        ContentsDto contents = contentsService.getView(Integer.parseInt(contentsIdx));

        // 회차 상세
        EpisodeDto view = episodeService.getView(idx);

        Map<String, Object> map = new HashMap<>();
        map.put("contents", contents);
        map.put("view", view);
        map.put("categoryIdx", contents.getCategoryIdx());
        map.put("categoryName", contents.getCategoryName());

        // 뷰어 웹툰
        if (contents.getCategoryIdx() == 1) {
            List<ImageDto> viewer = viewerService.getViewerWebtoon(idx);
            map.put("viewer", viewer);
        }
        // 만화
        else if (contents.getCategoryIdx() == 2) {
            List<ImageDto> viewer = viewerService.getViewerComic(idx);
            map.put("viewer", viewer);
        }
        // 소설
        else if (contents.getCategoryIdx() == 3) {
            // 뷰어 소설
            List<ViewerDto> viewer = viewerService.getViewerNovel(idx);

            // 뷰어 커버 이미지
            List<ImageDto> images = viewerService.getViewerNovelImg(idx);
            map.put("viewer", viewer);
            map.put("images", images);
        }


        // response object
        JSONObject data = new JSONObject(map);
        String sMessage = "";

        if (view == null) {
            // 검색 결과가 없습니다.
            sMessage = super.langMessage("lang.episode.exception.search_fail");
        }

        return displayJson(true, "1000", sMessage, data);

    }

    /**
     * 회차 수정
     * @param episodeDto
     * @return
     */
    @PutMapping()
    public String modifyEpisode(@ModelAttribute("EpisodeDto") EpisodeDto episodeDto) {

        // 회차 수정
        Integer result = episodeService.modifyEpisode(episodeDto);

        // 수정 실패
        String sMessage = super.langMessage("lang.episode.exception.modify_fail");
        // 수정 성공
        if (result > 0) {
            sMessage = super.langMessage("lang.episode.success.modify");
        }

        // response object
        JSONObject data = new JSONObject(result);

        return displayJson(true, "1000", sMessage, data);

    }

    /**
     * 회차 이미지 삭제
     * @param idx
     * @return
     */
    @DeleteMapping("/image/{idx}")
    public String deleteEpisodeImage(@PathVariable(name = "idx", required = false) Integer idx) {

        Integer result = episodeService.deleteEpisodeImage(idx);

        String sMessage = super.langMessage("lang.episode.exception.image.delete_fail");
        // 삭제 완료
        if (result > 0) {
            sMessage = super.langMessage("lang.episode.success.image.delete");
        }
        // response object
        JSONObject data = new JSONObject(result);

        return displayJson(true, "1000", sMessage, data);
    }

}
