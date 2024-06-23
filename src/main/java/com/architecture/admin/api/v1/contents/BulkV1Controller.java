package com.architecture.admin.api.v1.contents;

import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.models.dto.contents.ContentsDto;
import com.architecture.admin.models.dto.contents.EpisodeDto;
import com.architecture.admin.services.contents.BulkService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/cp-admin/bulk")
public class BulkV1Controller extends BaseController {

    private final BulkService bulkService;

    @Value("${spring.servlet.multipart.location}")
    private String ftpPath;


    // TODO timeout 테스트
    @GetMapping("/time/{time}")
    public String timeOut(@PathVariable(name = "time", required = false) Integer time) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            Thread.sleep(time * 1000);
            stringBuilder.append(time + " 초");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }



    // TODO 테스트 (삭제)
    @GetMapping("/path")
    public String filenames() {

        String DATA_DIRECTORY = ftpPath;

        File dir = new File(DATA_DIRECTORY);
        String[] filenames = dir.list();

        StringBuilder stringBuilder = new StringBuilder();
        for (String filename : filenames) {
            stringBuilder.append(filename+ " ");
        }


        stringBuilder.append(" / ");

        DATA_DIRECTORY = ftpPath + "/cont";

        dir = new File(DATA_DIRECTORY);
        filenames = dir.list();

        for (String filename : filenames) {
            stringBuilder.append(filename+ " ");
        }

        return stringBuilder.toString();
    }

    /**
     * 작품 대량 등록
     * @param multipartFile
     * @return
     */
    @PostMapping("/contents")
    public String register(@RequestParam("contentsFile") MultipartFile multipartFile) {

        Integer result = bulkService.registerBulkContents(multipartFile);

        // 등록 실패(default)
        String sMessage = super.langMessage("lang.contents.exception.register_fail");
        // 등록 성공
        if (result > 0) {
            sMessage = super.langMessage("lang.contents.success.register");
        }

        // response object
        JSONObject data = new JSONObject(result);

        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 작품 대량 등록
     * @param multipartFile
     * @return
     */
    @PostMapping("/contents/image")
    public String registerContentsImage(@RequestParam("contentsFile") MultipartFile multipartFile) {

        Integer result = bulkService.registerContentsImage(multipartFile);

        // 등록 실패(default)
        String sMessage = super.langMessage("lang.contents.exception.register_fail");
        // 등록 성공
        if (result > 0) {
            sMessage = super.langMessage("lang.contents.success.register");
        }

        // response object
        JSONObject data = new JSONObject(result);

        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 회차 대량 등록
     * @param multipartFile
     * @return
     */
    @PostMapping("/episode")
    public String registerEpisode(@RequestParam("episodeFile") MultipartFile multipartFile) {

        Integer result = bulkService.registerBulkEpisode(multipartFile);

        // 등록 실패(default)
        String sMessage = super.langMessage("lang.contents.exception.register_fail");
        // 등록 성공
        if (result > 0) {
            sMessage = super.langMessage("lang.contents.success.register");
        }

        // response object
        JSONObject data = new JSONObject(result);

        return displayJson(true, "1000", sMessage, data);
    }


    @PostMapping("/episode/image")
    public String registerEpisodeImage(@RequestParam("episodeFile") MultipartFile multipartFile) {
        Integer result = bulkService.registerBulkEpisodeImage(multipartFile);

        // 등록 실패(default)
        String sMessage = super.langMessage("lang.contents.exception.register_fail");
        // 등록 성공
        if (result > 0) {
            sMessage = super.langMessage("lang.contents.success.register");
        }

        // response object
        JSONObject data = new JSONObject(result);

        return displayJson(true, "1000", sMessage, data);
    }


    /**
     * 뷰어 상세 이미지 등록
     * @param multipartFile
     * @return
     */
    @PostMapping("/viewer")
    public String registerViewer(@RequestParam("episodeFile") MultipartFile multipartFile) {

        Integer result = bulkService.registerBulkViewer(multipartFile);

        // 등록 실패(default)
        String sMessage = super.langMessage("lang.contents.exception.register_fail");
        // 등록 성공
        if (result > 0) {
            sMessage = super.langMessage("lang.contents.success.register");
        }

        // response object
        JSONObject data = new JSONObject(result);

        return displayJson(true, "1000", sMessage, data);
    }

    /******************************************************/

    /**
     * 작품 목록
     */
    @GetMapping("/contentsAll")
    public String contentsAll() {

        // 컨텐츠 목록
        List<ContentsDto> list = bulkService.getListContentsAll();

        Map<String, Object> map = new HashMap<>();
        map.put("list", list);

        JSONObject data = new JSONObject(map);
        String sMessage = "";

        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 회차 목록
     */
    @GetMapping("/episodeAll")
    public String episodeAll() {
        List<EpisodeDto> list = bulkService.getListEpisodeAll();

        Map<String, Object> map = new HashMap<>();
        map.put("list", list);

        JSONObject data = new JSONObject(map);
        String sMessage = "";

        return displayJson(true, "1000", sMessage, data);
    }
}
