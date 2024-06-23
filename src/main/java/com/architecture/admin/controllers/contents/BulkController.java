package com.architecture.admin.controllers.contents;

import com.architecture.admin.controllers.BaseController;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/bulk")
public class BulkController extends BaseController {

    /**
     * 작품 대량 등록
     * @return
     */
    @GetMapping("/contents")
    public String bulk() {

        hmImportFile.put("importJs", "contents/bulk/view.js.html");

        return "contents/bulk/view";
    }

    /**
     * 회차 대량 등록
     * @return
     */
    @GetMapping("/episode")
    public String bulkEpisode() {

        hmImportFile.put("importJs", "contents/bulk/episode/view.js.html");

        return "contents/bulk/episode/view";
    }

    /**
     * 뷰어 상세 이미지 등록
     * @return
     */
    @GetMapping("/viewer")
    public String bulkViewer() {
        hmImportFile.put("importJs", "contents/bulk/viewer/view.js.html");

        return "contents/bulk/viewer/view";
    }


    /**
     * 컨텐츠 전체 목록
     * @return
     */
    @GetMapping("/listAll/contents")
    public String listsContentsAll() {

        hmImportFile.put("importJs", "contents/listContents.js.html");

        return "contents/listContents";
    }

    /**
     * 회차 전체 목록
     * @return
     */
    @GetMapping("/listAll/episode")
    public String listsEpisodeAll() {

        hmImportFile.put("importJs", "contents/listEpisode.js.html");

        return "contents/listEpisode";
    }
}
