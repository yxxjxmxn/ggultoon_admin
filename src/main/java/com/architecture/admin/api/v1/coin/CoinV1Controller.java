package com.architecture.admin.api.v1.coin;

import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.libraries.excel.ExcelXlsxView;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.coin.CoinDto;
import com.architecture.admin.models.dto.coin.MileageSaveDto;
import com.architecture.admin.models.dto.coin.MileageUseDto;
import com.architecture.admin.services.coin.CoinService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/coins")
public class CoinV1Controller extends BaseController {

    private final CoinService coinService;

    /**
     * 코인 적립 내역 리스트 조회
     *
     * @param searchDto
     * @return
     */
    @GetMapping("/coin/save")
    public String savedCoinList(@ModelAttribute SearchDto searchDto) {
        // 관리자 접근 권한
        super.adminAccessFail(4);

        // 내보낼 데이터 set
        String sMessage = ""; // 응답 메시지
        JSONObject joData = new JSONObject();
        List<CoinDto> savedCoinList = null;

        // 날짜 유효성 검사(빈 문자열이면 유효)
        Boolean isValidDate = dateValidator(searchDto);
        // 날짜 유효
        if (Boolean.TRUE == isValidDate) {
            // 검색어 공백제거
            searchDto.setSearchWord(searchDto.getSearchWord().trim());

            // 전체 코인 적립 내역 조회
            int totalCnt = coinService.getSavedCoinTotalCnt(searchDto);

            // 검색에 해당하는 데이터가 없는 경우
            if (totalCnt < 1) {
                sMessage = super.langMessage("lang.common.exception.searchFail"); // 검색 결과가 없습니다.
            } else {
                savedCoinList = coinService.getSavedCoinTotalList(searchDto); // 코인 리스트 조회
                sMessage = super.langMessage("lang.common.success.search"); // 조회 완료하였습니다.
                joData.put("params", new JSONObject(searchDto));
            }
        }
        // 유효하지 않은 날짜
        else {
            sMessage = super.langMessage("lang.common.exception.date.format");
        }

        joData.put("list", savedCoinList);


        return displayJson(true, "1000", sMessage, joData);
    }

    /**
     * 마일리지 적립 내역 리스트 엑셀 다운로드
     *
     * @param searchDto
     * @return
     */
    @GetMapping("/mileage/save/excel")
    public ModelAndView saveMileageListExcelDownload(@ModelAttribute("param") SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(7);

        // 뷰에 담을 모델객체를 생성(엑셀 파일에 들어갈 데이터)
        Map<String, Object> mExcelData = coinService.excelSaveMileageList(searchDto);

        // AbstractXlsxView 가 동작
        return new ModelAndView(new ExcelXlsxView(), mExcelData);
    }

    /**
     * 마일리지 적립 내역 리스트 조회
     *
     * @param searchDto
     * @return
     */
    @GetMapping("/mileage/save")
    public String saveMileageList(@ModelAttribute SearchDto searchDto) {
        // 관리자 접근 권한
        super.adminAccessFail(7);

        //검색어 공백제거
        searchDto.setSearchWord(searchDto.getSearchWord().trim());

        // 내보낼 데이터 set
        String sMessage = ""; // 응답 메시지
        JSONObject joData = new JSONObject();
        List<MileageSaveDto> savedMileageList = null;

        // 날짜 유효성 검사(빈 문자열이면 유효)
        Boolean isValidDate = dateValidator(searchDto);

        // 날짜 유효
        if (Boolean.TRUE == isValidDate) {
            // 전체 개수 조회
            int totalCnt = coinService.getSavedMileageTotalCnt(searchDto);

            // 검색에 해당하는 마일리지 없는 경우
            if (totalCnt < 1) {
                sMessage = super.langMessage("lang.common.exception.searchFail"); // 검색 결과가 없습니다.

            } else {

                // 마일리지 적립 내역 조회
                savedMileageList = coinService.getSavedMileageTotalList(searchDto);
                sMessage = super.langMessage("lang.common.success.search"); // 조회 완료하였습니다.
                joData.put("params", new JSONObject(searchDto));
            }
        } else { // 유효하지 않은 날짜
            sMessage = super.langMessage("lang.common.exception.date.format");
        }

        joData.put("list", savedMileageList);

        return displayJson(true, "1000", sMessage, joData);
    }

    /**
     * 코인 사용 내역 리스트
     *
     * @param searchDto
     * @return
     */
    @GetMapping("/coin/use")
    public String usedCoinList(@ModelAttribute SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(13);

        // 검색어 공백제거
        searchDto.setSearchWord(searchDto.getSearchWord().trim());

        // 내보낼 데이터 set
        String sMessage = ""; // 응답 메시지
        JSONObject joData = new JSONObject();
        List<CoinDto> usedCoinList = null;

        // 날짜 유효성 검사(빈 문자열이면 유효)
        Boolean isValidDate = dateValidator(searchDto);

        // 날짜 유효
        if (Boolean.TRUE == isValidDate) {

            // 전체 개수 조회
            int totalCnt = coinService.getUsedCoinTotalCnt(searchDto);

            // 검색에 해당하는 코인이 없는 경우
            if (totalCnt < 1) {
                sMessage = super.langMessage("lang.common.exception.searchFail"); // 검색 결과가 없습니다.
            } else {

                // 코인 사용 내역 리스트 조회
                usedCoinList = coinService.getUsedCoinTotalList(searchDto);
                sMessage = super.langMessage("lang.common.success.search"); // 조회 완료하였습니다.
                joData.put("params", new JSONObject(searchDto));
            }

        } else { // 유효하지 않은 날짜
            sMessage = super.langMessage("lang.common.exception.date.format");
        }
        joData.put("list", usedCoinList);

        return displayJson(true, "1000", sMessage, joData);

    }

    /**
     * 마일리지 사용 내역 리스트 엑셀 다운로드
     *
     * @param searchDto
     * @return
     */
    @GetMapping("/mileage/use/excel")
    public ModelAndView useMileageListExcelDownload(@ModelAttribute("param") SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(7);

        // 뷰에 담을 모델객체를 생성(엑셀 파일에 들어갈 데이터)
        Map<String, Object> mExcelData = coinService.excelUseMileageList(searchDto);

        // AbstractXlsxView 가 동작
        return new ModelAndView(new ExcelXlsxView(), mExcelData);
    }

    /**
     * 마일리지 사용 내역 리스트
     *
     * @param searchDto
     * @return
     */
    @GetMapping("/mileage/use")
    public String usedMileageList(@ModelAttribute SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(14);

        //검색어 공백제거
        searchDto.setSearchWord(searchDto.getSearchWord().trim());

        // 내보낼 데이터 set
        String sMessage = ""; // 응답 메시지
        JSONObject joData = new JSONObject();
        List<MileageUseDto> usedMileageList = null;

        // 날짜 유효성 검사(빈 문자열이면 유효)
        Boolean isValidDate = dateValidator(searchDto);

        // 날짜 유효
        if (Boolean.TRUE == isValidDate) {
            // 전체 개수 조회
            int totalCnt = coinService.getUsedMileageTotalCnt(searchDto);

            // 검색에 해당하는 마일리지 없는 경우
            if (totalCnt < 1) {

                sMessage = super.langMessage("lang.common.exception.searchFail"); // 검색 결과가 없습니다.
            } else {

                // 마일리지 사용 내역 조회
                usedMileageList = coinService.getUsedMileageTotalList(searchDto);
                sMessage = super.langMessage("lang.common.success.search"); // 조회 완료하였습니다.
                joData.put("params", new JSONObject(searchDto));
            }

        } else { // 유효하지 않은 날짜
            sMessage = super.langMessage("lang.common.exception.date.format");
        }

        joData.put("list", usedMileageList);

        return displayJson(true, "1000", sMessage, joData);

    }

    /**
     * 관리자 코인 지급 & 차감 리스트
     *
     * @param searchDto
     * @return
     */
    @GetMapping("/coin/admin")
    public String coinAdminList(@ModelAttribute SearchDto searchDto) {
        // 관리자 접근 권한
        super.adminAccessFail(14);

        // 내보낼 데이터 set
        String sMessage = ""; // 응답 메시지
        JSONObject joData = new JSONObject();
        List<CoinDto> coinAdminList = null;

        // 날짜 유효성 검사(빈 문자열이면 유효)
        Boolean isValidDate = dateValidator(searchDto);

        // 날짜 유효
        if (Boolean.TRUE == isValidDate) {
            // 전체 개수 조회
            int totalCnt = coinService.getAdminCoinTotalCnt(searchDto);

            // 검색에 해당하는 마일리지 없는 경우
            if (totalCnt < 1) {

                sMessage = super.langMessage("lang.common.exception.searchFail"); // 검색 결과가 없습니다.
            } else {

                coinAdminList = coinService.getAdminCoinList(searchDto);
                sMessage = super.langMessage("lang.common.success.search");  // 조회 완료하였습니다.
                joData.put("params", new JSONObject(searchDto));
            }

        } else { // 유효하지 않은 날짜
            sMessage = super.langMessage("lang.common.exception.date.format");
        }
        joData.put("list", coinAdminList);

        return displayJson(true, "1000", sMessage, joData);
    }

    /*************************************************************
     * SUB
     *************************************************************/

    /**
     * 날짜 유효성 검사
     *
     * @param searchDto
     * @return
     */
    private Boolean dateValidator(SearchDto searchDto) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        // 공백 제거
        searchDto.setSearchStartDate(searchDto.getSearchStartDate().trim());
        searchDto.setSearchEndDate(searchDto.getSearchEndDate().trim());

        String startDateByDto = searchDto.getSearchStartDate();
        String endDateByDto = searchDto.getSearchEndDate();

        // 날짜 모두 빈값으로 들어오면
        if (startDateByDto.isEmpty() && endDateByDto.isEmpty()) {
            return Boolean.TRUE;
        }
        // String 날짜를 날짜 타입으로 변환
        try {
            // 날짜 형식 변환
            Date startDate = sdf.parse(searchDto.getSearchStartDate());
            Date endDate = sdf.parse(searchDto.getSearchEndDate());

            // 시작 날짜가 끝나는 날짜를 넘을 경우 false 리턴
            if (startDate != null && startDate.after(endDate)) {
                return Boolean.FALSE;
            }
            //  시분초 입력 -> 년월일만 있으면 해당 일자 0시0분0초 까지만 조회됨
            searchDto.setSearchEndDate(searchDto.getSearchEndDate() + " 23:59:59");
        } catch (ParseException e) {
            return Boolean.FALSE;
        }
        // 이상 없으면 true 리턴
        return Boolean.TRUE;
    }

}
