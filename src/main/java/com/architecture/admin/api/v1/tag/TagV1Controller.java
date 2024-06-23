package com.architecture.admin.api.v1.tag;

import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.libraries.excel.ExcelXlsxView;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.contents.TagDto;
import com.architecture.admin.services.contents.TagService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/tags")
public class TagV1Controller extends BaseController {

    private final TagService tagService;

    /**
     * 작품 등록용 태그 리스트 조회
     * 페이징 X
     * @return
     */
    @GetMapping("/list")
    public String registerTagList(@ModelAttribute SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(32);

        // 태그 리스트 조회
        JSONObject data = tagService.getRegisterTagList(searchDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.contents.success.search"); // 검색을 완료하였습니다.

        // 태그 리스트가 없는 경우
        if (data.getJSONArray("list").isEmpty()) {
            sMessage = super.langMessage("lang.contents.exception.search_fail"); // 검색 결과가 없습니다.
        }

        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 작품 태그 리스트 엑셀 다운로드
     *
     * @param searchDto
     * @return
     */
    @GetMapping("/excel")
    public ModelAndView tagListExcelDownload(@ModelAttribute("param") SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(32);

        // 뷰에 담을 모델객체를 생성(엑셀 파일에 들어갈 데이터)
        Map<String, Object> mExcelData = tagService.excelTag(searchDto);

        // AbstractXlsxView 가 동작
        return new ModelAndView(new ExcelXlsxView(), mExcelData);
    }

    /**
     * 작품 태그 리스트 조회
     * @return
     */
    @GetMapping()
    public String tagList(@ModelAttribute SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(32);

        // 태그 리스트 조회
        JSONObject data = tagService.getTagList(searchDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.contents.success.search"); // 검색을 완료하였습니다.

        // 태그 리스트가 없는 경우
        if (data.getJSONArray("list").isEmpty()) {
            sMessage = super.langMessage("lang.contents.exception.search_fail"); // 검색 결과가 없습니다.
        }

        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 작품 태그 상세
     * @param idx
     * @return
     */
    @GetMapping("/{idx}")
    public String viewTag(@PathVariable(name = "idx") Integer idx){

        // 관리자 접근 권한
        super.adminAccessFail(32);

        // 결제 수단 상세
        JSONObject joData = tagService.getViewTag(idx);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.contents.success.search"); // 검색을 완료하였습니다.

        // 조회 결과가 없는 경우
        if (joData.isEmpty()) {
            sMessage = super.langMessage("lang.contents.exception.search_fail"); // 검색 결과가 없습니다.
        }

        return displayJson(true, "1000", sMessage, joData);
    }

    /**
     * 작품 태그 등록
     * @param tagDto
     * @return
     */
    @PostMapping()
    public String registerTag(@ModelAttribute("TagDto") TagDto tagDto) {

        // 관리자 접근 권한
        super.adminAccessFail(32);

        // 작품 태그 등록
        tagService.registerTag(tagDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.contents.success.register"); // 등록하였습니다.

        return displayJson(true, "1000", sMessage);
    }

    /**
     * 작품 태그 수정
     * @param idx
     * @return
     */
    @PutMapping("/{idx}")
    public String modifyTag(@PathVariable(name = "idx") Integer idx,
                            @ModelAttribute("TagDto") TagDto tagDto) {

        // 관리자 접근 권한
        super.adminAccessFail(32);

        // idx set
        tagDto.setIdx(idx);

        // 작품 태그 수정
        tagService.modifyTag(tagDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.contents.success.modify"); // 수정하였습니다.

        return displayJson(true, "1000", sMessage);
    }

    /**
     * 작품 태그 삭제
     * @param idx
     * @return
     */
    @DeleteMapping("/{idx}")
    public String deleteTag(@PathVariable(name = "idx") Integer idx) {

        // 관리자 접근 권한
        super.adminAccessFail(32);

        // 작품 태그 삭제
        tagService.deleteTag(idx);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.contents.success.delete"); // 삭제하였습니다.

        return displayJson(true, "1000", sMessage);
    }
}
