package com.architecture.admin.api.v1.code;

import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.models.dto.contents.CodeDto;
import com.architecture.admin.models.dto.contents.TagDto;
import com.architecture.admin.services.code.CodeService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/cp-admin/code")
public class CodeV1Controller extends BaseController {

    private final CodeService codeService;


    /**
     * 코드 목록
     * @return
     */
    @GetMapping()
    public String lists() {

        List<TagDto> list = codeService.getList();

        Map<String, Object> map = new HashMap<>();
        map.put("list", list);

        JSONObject data = new JSONObject(map);
        String sMessage = "";

        return displayJson(true, "1000", sMessage, data);
    }


    /**
     * 코드 등록
     * @param codeDto
     * @return
     */
    @PostMapping()
    public String register(@ModelAttribute("codeDto") CodeDto codeDto) {

        Integer result = codeService.registerCode(codeDto);

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

}
