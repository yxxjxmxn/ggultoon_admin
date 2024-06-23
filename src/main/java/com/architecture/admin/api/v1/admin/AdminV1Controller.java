package com.architecture.admin.api.v1.admin;

import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.admin.AdminDto;
import com.architecture.admin.services.admin.AdminService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/admin")
public class AdminV1Controller extends BaseController {

    private final AdminService adminService;

    /**
     * 관리자 목록
     * @param searchDto
     * @return
     */
    @GetMapping()
    public String adminList(@ModelAttribute SearchDto searchDto){

        // 관리자 접근 권한
        super.adminAccessFail(8);

        // 내보낼 데이터 set
        String sMessage = ""; // 응답 메시지
        JSONObject joData = new JSONObject();
        List<AdminDto> listAdmin = null;

        // 날짜 유효성 검사
        Boolean isValidDate = dateValidator(searchDto);

        // 유효한 날짜일 경우
        if (Boolean.TRUE == isValidDate) {

            //검색어 공백제거
            searchDto.setSearchWord(searchDto.getSearchWord().trim());

            // 전체 관리자 개수 조회
            int totalCnt = adminService.getAdminTotalCnt(searchDto);

            if (totalCnt < 1) { // 검색 결과가 없는 경우

                sMessage = super.langMessage("lang.admin.exception.search_fail"); // 검색 결과가 없습니다.

            } else { // 검색 결과가 있는 경우

                listAdmin = adminService.getAdminList(searchDto);
                sMessage = super.langMessage("lang.admin.success.search"); // 조회 완료하였습니다.
                joData.put("params", new JSONObject(searchDto));

            }
        } else { // 유효하지 않은 날짜일 경우
            sMessage = super.langMessage("lang.admin.exception.date.format"); // 잘못된 날짜 형식입니다.
        }
        
        joData.put("list", listAdmin);

        return displayJson(true, "1000", sMessage, joData);
    }

    /**
     * 관리자 상세
     * @param idx (admin.idx)
     * @return
     */
    @GetMapping("/{idx}")
    public String view(@PathVariable(name = "idx", required = false) Long idx) {

        // 관리자 접근 권한
        super.adminAccessFail(8);

        // 상세
        AdminDto viewAdmin = adminService.getViewAdmin(idx);

        // response object
        JSONObject data = new JSONObject(viewAdmin);

        String sMessage = "";
        
        if (viewAdmin == null) {
            // 검색 결과가 없습니다.
            sMessage = super.langMessage("lang.admin.exception.search_fail");

        }
        
        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 관리자 등록
     * @param adminDto
     * @param result
     * @return
     * @throws Exception
     */
    @PostMapping("/join")
    public String join(@Valid AdminDto adminDto, BindingResult result) throws Exception {

        if (result.hasErrors()) {
            return super.displayError(result);
        }

        // 아이디 중복체크
        Boolean bDuple = adminService.checkDupleId(adminDto);
        if (Boolean.TRUE.equals(bDuple)) { // 증복된 아이디인 경우
            throw new CustomException(CustomError.ID_DUPLE);
        }

        // 관리자 등록
        adminService.regist(adminDto);

        // set return data
        JSONObject data = new JSONObject();
        data.put("location", "/admin/login");

        // return value
        String sErrorMessage = "lang.admin.success.regist";
        String message = super.langMessage(sErrorMessage);
        return displayJson(true, "1000", message, data);
    }

    /**
     * 관리자 로그인
     * @param adminDto
     * @param result
     * @param httpRequest
     * @param httpResponse
     * @return
     * @throws Exception
     */
    @PostMapping("/login")
    public String login(@Valid AdminDto adminDto,
                        BindingResult result,
                        HttpServletRequest httpRequest,
                        HttpServletResponse httpResponse) throws Exception {

        if (result.hasErrors()) {
            return super.displayError(result);
        }

        // 관리자 로그인 처리
        Boolean bIsLogin = adminService.login(adminDto, httpRequest, httpResponse);
        if (Boolean.FALSE.equals(bIsLogin)) {
            throw new CustomException(CustomError.LOGIN_FAIL);
        }

        // set return data
        JSONObject data = new JSONObject();
        data.put("location", "/");

        // return value
        String sErrorMessage = "lang.admin.success.login";
        String message = super.langMessage(sErrorMessage);
        return displayJson(true, "1000", message, data);
    }

    /**
     * 관리자 수정
     * @param adminDto
     * @return
     */
    @PutMapping("/modify/{idx}")
    public String modify(@ModelAttribute("adminDto") AdminDto adminDto) {

        // 관리자 접근 권한
        super.adminAccessFail(7);

        // 관리자 권한 체크
        super.checkAdminAccessLevel();

        // 수정
        int result = adminService.modifyAdmin(adminDto);

        String sMessage = "";

        if (result > 0) { // 수정 완료
            sMessage = super.langMessage("lang.admin.success.modify");
        } else { // 수정 실패
            sMessage = super.langMessage("lang.admin.exception.modify_fail");
        }

        // response object
        JSONObject data = new JSONObject(result);

        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 내 정보 수정
     * @param adminDto
     * @return
     */
    @PutMapping("/mypage/{idx}")
    public String modifyAdminMyPage(@ModelAttribute("adminDto") AdminDto adminDto) {

        // 관리자 접근 권한
        super.adminAccessFail(7);

        // 내 정보 수정
        int result = adminService.modifyAdminMyPage(adminDto);

        String sMessage = "";

        if (result > 0) { // 수정 완료
            sMessage = super.langMessage("lang.admin.success.modify");
        } else { // 수정 실패
            sMessage = super.langMessage("lang.admin.exception.modify_fail");
        }

        // response object
        JSONObject data = new JSONObject(result);

        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 비밀번호 변경
     * @param adminDto
     * @return
     */
    @PutMapping("/mypage/password/{idx}")
    public String modifyPassword(@PathVariable(name = "idx", required = false) Long idx,
                                 @ModelAttribute("adminDto") AdminDto adminDto) {

        // 관리자 접근 권한
        super.adminAccessFail(7);

        // 비밀번호 변경
        adminDto.setIdx(idx); // 관리자 idx set
        int result = adminService.modifyAdminPassword(adminDto);

        String sMessage = "";

        if (result > 0) { // 수정 완료
            sMessage = super.langMessage("lang.admin.success.modify");
        } else { // 수정 실패
            sMessage = super.langMessage("lang.admin.exception.modify_fail");
        }

        // response object
        JSONObject data = new JSONObject(result);

        return displayJson(true, "1000", sMessage, data);
    }

    /*************************************************************
     * Modules
     *************************************************************/

    /**
     * datepicker 유효성 검사
     *
     * @param searchDto
     * @return
     */
    private Boolean dateValidator(SearchDto searchDto) {

        String searchStartDate = searchDto.getSearchStartDate(); // 검색 시작일 가져오기
        String searchEndDate = searchDto.getSearchEndDate(); // 검색 종료일 가져오기

        // 검색 시작일 & 검색 종료일 공백 제거
        searchDto.setSearchStartDate(searchStartDate.trim());
        searchDto.setSearchEndDate(searchEndDate.trim());

        // 검색 시작일 & 검색 종료일 -> 빈값 가능
        if (searchStartDate.isEmpty() && searchEndDate.isEmpty()) {
            return Boolean.TRUE;
        }

        // String -> 날짜 타입으로 변환
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false); // 입력한 값이 잘못된 형식일 경우 오류 발생

            // 날짜 형식 변환
            Date startDate = sdf.parse(searchDto.getSearchStartDate());
            Date endDate = sdf.parse(searchDto.getSearchEndDate());

            // 검색 시작일이 검색 종료일을 넘길 경우 false 리턴
            if (startDate != null && startDate.compareTo(endDate) > 0) {
                return Boolean.FALSE;
            }

            // 검색 종료일이 검색 시작일보다 앞설 경우 false 리턴
            if (endDate != null && endDate.compareTo(startDate) < 0) {
                return Boolean.FALSE;
            }

            /** 데이터 누락 방지용 시분초 추가 **/
            // 검색 시작일 시분초 추가 -> 해당 일자의 00시 00분 00초부터 검색하도록 설정
            searchDto.setSearchStartDate(searchStartDate + " 00:00:00");

            // 검색 종료일 시분초 추가 -> 해당 일자의 23시 59분 59초까지 검색하도록 설정
            searchDto.setSearchEndDate(searchEndDate + " 23:59:59");

        } catch (ParseException e) {
            return Boolean.FALSE;
        }
        // 이상 없으면 true 리턴
        return Boolean.TRUE;
    }

    @GetMapping("/ttt")
    public void ttt() {
        LocalDate yesterDay = LocalDate.now().minusDays(1);

        long money = 999999999;

        DecimalFormat df = new DecimalFormat("###,###");
        String formatMoney = df.format(money);
        pushAlarm("가입수 : "+formatMoney+"\n탈퇴 : 2\n결제 : "+ yesterDay.toString()+"","LJH");
    }
}
