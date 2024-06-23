package com.architecture.admin.api.v1.admin;

import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.admin.CpDto;
import com.architecture.admin.services.admin.CpService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/admin/cp")
public class CpV1Controller extends BaseController {

    private final CpService cpService;
    /**
     * CP관리자 목록
     * @param searchDto
     * @return
     */
    @GetMapping()
    public String cpList(@ModelAttribute SearchDto searchDto){

        // 관리자 접근 권한
        super.adminAccessFail(12);

        //검색어 공백제거
        searchDto.setSearchWord(searchDto.getSearchWord().trim());

        // 전체 관리자 개수 조회
        int totalCount = cpService.getCpTotalCnt(searchDto);

        // 내보낼 데이터 set
        String sMessage = ""; // 응답 메시지
        JSONObject joData = new JSONObject();
        List<CpDto> listCp = null;

        if (totalCount < 1) {
            sMessage = super.langMessage("lang.admin.exception.search_fail"); // 검색 결과가 없습니다.
        } else {
            listCp = cpService.getCpList(searchDto);
            sMessage = super.langMessage("lang.admin.success.search"); // 조회 완료하였습니다.
            joData.put("params", new JSONObject(searchDto));
        }
        joData.put("list", listCp);

        return displayJson(true, "1000", sMessage, joData);
    }
    /**
     * 관리자 상세
     * @param idx
     * @return
     */
    @GetMapping("/{idx}")
    public String view(@PathVariable(name = "idx", required = false) Long idx) {
        // 관리자 접근 권한
        super.adminAccessFail(12);
        // 상세
        CpDto viewCp = cpService.getViewCp(idx);

        // response object
        JSONObject data = new JSONObject(viewCp);

        String sMessage = "";
        if (viewCp == null) {
            // 검색 결과가 없습니다.
            sMessage = super.langMessage("lang.admin.exception.search_fail");

        }
        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * cp관리자 등록
     * @param cpDto
     * @param result
     * @return
     * @throws Exception
     */
    @PostMapping("/regist")
    public String join(@Valid CpDto cpDto, BindingResult result) throws Exception {
        // 관리자 접근 권한
        super.adminAccessFail(12);

        if (result.hasErrors()) {
            return super.displayError(result);
        }

        /*
        // 아이디 중복체크
        Boolean bDuple = cpService.checkDupleId(cpDto);
        if (Boolean.TRUE.equals(bDuple)) { // 증복된 아이디인 경우
            throw new CustomException(CustomError.ID_DUPLE);
        }
         */

        // 관리자 등록
        cpService.registCpAdmin(cpDto);

        // set return data
        JSONObject data = new JSONObject();
        data.put("location", "/cp/list");

        // return value
        String sErrorMessage = "lang.admin.success.regist";
        String message = super.langMessage(sErrorMessage);
        return displayJson(true, "1000", message, data);
    }

    /**
     * 관리자 수정
     * @param cpDto
     * @return
     */
    @PutMapping("/{idx}")
    public String modify(@ModelAttribute("cpDto") CpDto cpDto) throws Exception {
        // 관리자 접근 권한
        super.adminAccessFail(8);

        // 수정
        int result = cpService.modifyCpAdmin(cpDto);

        String sMessage = "";
        // 수정 완료
        if (result > 0) {
            sMessage = super.langMessage("lang.cp.success.modify");
        }
        // 수정 실패
        else {
            sMessage = super.langMessage("lang.cp.exception.modify_fail");
        }
        // response object
        JSONObject data = new JSONObject(result);

        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 회사명 목록
     * @return
     */
    @GetMapping("/company")
    public String companyLists() {

        List<CpDto> list = cpService.getCompanyList();

        Map<String, Object> map = new HashMap<>();
        map.put("list", list);

        JSONObject data = new JSONObject(map);
        String sMessage = "";

        return displayJson(true, "1000", sMessage, data);
    }
}
