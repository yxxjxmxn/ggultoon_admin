package com.architecture.admin.api.v1.contents;

import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.models.dto.contents.EpisodeDto;
import com.architecture.admin.models.dto.contents.ViewerDto;
import com.architecture.admin.services.contents.ViewerService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.json.ParseException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/cp-admin/viewer")
public class ViewerV1Controller extends BaseController {

    private final ViewerService viewerService;

    /**
     * 회차 viewer image 등록(zip)
     * @param episodeDto
     * @return
     * @throws Exception
     */
    @PostMapping()
    public String upload(@ModelAttribute("EpisodeDto") EpisodeDto episodeDto) throws Exception {
        Integer result = 0;

        // TODO 뷰어 만화, 소설 등록
        if (episodeDto.getCategoryIdx() == 1) {
            // 웹툰 등록
            result = viewerService.webtoon(episodeDto);
        } else if (episodeDto.getCategoryIdx() == 2) {
            // 만화 등록
            result = viewerService.comic(episodeDto);
        } else if (episodeDto.getCategoryIdx() == 3) {
            // 소설 등록
            result = viewerService.novel(episodeDto);
        }

        String sMessage = "";
        // 등록 완료
        if (result > 0) {
            sMessage = super.langMessage("lang.contents.success.register");
        }
        // 파일 없음
        else if (result == 0) {
            sMessage = super.langMessage("lang.contents.exception.no_file");
        }
        // 등록 실패
        else {
            sMessage = super.langMessage("lang.contents.exception.register_fail");
        }
        // response object
        JSONObject data = new JSONObject(result);

        return displayJson(true, "1000", sMessage, data);
    }


    /**
     * 뷰어 이미지 삭제
     * @param episodeIdx
     * @return
     */
    @DeleteMapping("/image/{idx}")
    public String deleteViewerImage(@PathVariable(name = "idx", required = false) Integer episodeIdx, @RequestParam("categoryIdx") Integer categoryIdx) {

        Integer result = viewerService.deleteViewerImage(episodeIdx, categoryIdx);

        String sMessage = super.langMessage("lang.episode.exception.viewer.image.delete_fail");
        // 삭제 완료
        if (result > 0) {
            sMessage = super.langMessage("lang.episode.success.viewer.image.delete");
        }
        // response object
        JSONObject data = new JSONObject(result);

        return displayJson(true, "1000", sMessage, data);

    }


    /**
     * 뷰어 소설 내용 수정
     * @param viewerList
     * @return
     * @throws ParseException
     */
    @PutMapping()
    public String modifyNovelViewer(@RequestBody List<ViewerDto> viewerList) throws ParseException {

        Integer result = viewerService.modifyNovelViewer(viewerList);

        String sMessage = "";
        // 수정 완료
        if (result > 0) {
            sMessage = super.langMessage("lang.episode.success.viewer.modify");
        }
        // 수정 실패
        else {
            sMessage = super.langMessage("lang.episode.exception.viewer.modify_fail");
        }
        // response object
        JSONObject data = new JSONObject(result);

        return displayJson(true, "1000", sMessage, data);
    }

}
