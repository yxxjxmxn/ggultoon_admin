package com.architecture.admin.api.v1.member;

import com.architecture.admin.config.SessionConfig;
import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.libraries.excel.ExcelXlsxView;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.coin.CoinDto;
import com.architecture.admin.models.dto.member.MemberDto;
import com.architecture.admin.models.dto.member.MemberOttDto;
import com.architecture.admin.models.dto.member.MemberOttListDto;
import com.architecture.admin.models.dto.member.MemberPurchaseDto;
import com.architecture.admin.services.coin.CoinService;
import com.architecture.admin.services.member.MemberService;
import com.architecture.admin.services.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/members")

public class MemberV1Controller extends BaseController {

    private final MemberService memberService;
    private final CoinService coinService;
    private final PaymentService paymentService;

    /**
     * 회원 리스트 엑셀 다운로드
     *
     * @param searchDto
     * @return
     */
    @GetMapping("/excel")
    public ModelAndView memberListExcelDownload(@ModelAttribute("param") SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(4);

        // 뷰에 담을 모델객체를 생성(엑셀 파일에 들어갈 데이터)
        Map<String, Object> mExcelData = memberService.excelMemberList(searchDto);

        // AbstractXlsxView 가 동작
        return new ModelAndView(new ExcelXlsxView(), mExcelData);
    }

    /**
     * 회원 리스트
     *
     * @param searchDto
     * @return
     */
    @GetMapping
    public String memberList(@ModelAttribute SearchDto searchDto) {
        // 관리자 접근 권한
        super.adminAccessFail(4);

        //검색어 공백제거
        searchDto.setSearchWord(searchDto.getSearchWord().trim());

        // 전체 회원 개수 조회
        int totalCnt = memberService.getMemberTotalCnt(searchDto);
        // 내보낼 데이터 set
        String sMessage = ""; // 응답 메시지
        JSONObject joData = new JSONObject();
        List<MemberDto> memberList = null;

        if (totalCnt < 1) {
            sMessage = super.langMessage("lang.common.exception.searchFail"); // 검색 결과가 없습니다.
        } else {
            memberList = memberService.getMemberList(searchDto);
            sMessage = super.langMessage("lang.common.success.search"); // 조회 완료하였습니다.
            joData.put("params", new JSONObject(searchDto));
        }
        // list 담기
        joData.put("list", memberList);

        // 전체 회원 집계
        List<MemberDto> memberCntList = memberService.getMemberCntList();

        // 전체 회원 집계 list 담기
        joData.put("memberCntList", memberCntList);

        return displayJson(true, "1000", sMessage, joData);
    }

    /**
     * 회원 상세 정보
     *
     * @param memberIdx
     * @return
     */
    @GetMapping("/{idx}")
    public String memberDetail(@PathVariable(name = "idx") Long memberIdx) {

        // 관리자 접근 권한
        super.adminAccessFail(4);

        JSONObject data = memberService.getMemberDetail(memberIdx);

        String sMessage = super.langMessage("lang.common.success.search"); // 조회 완료하였습니다.

        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 회원 정보 수정
     *
     * @param memberIdx
     * @param memberDto
     * @return
     */
    @PutMapping("/{idx}")
    public String modifyMemberInfo(@PathVariable(name = "idx") Long memberIdx, MemberDto memberDto) {

        // 관리자 접근 권한
        super.adminAccessFail(4);

        memberDto.setIdx(memberIdx);

        memberService.modifyMemberInfo(memberDto);

        String sMessage = super.langMessage("lang.member.success.modify"); // 수정 완료 하였습니다.

        return displayJson(true, "1000", sMessage);
    }

    /**
     * 회원 탈퇴
     *
     * @param memberIdx : 회원 idx
     * @return
     */
    @DeleteMapping("/{idx}")
    public String deleteMember(@PathVariable(name = "idx") Long memberIdx) {

        // 관리자 접근 권한
        super.adminAccessFail(4);

        // 회원 탈퇴
        memberService.deleteMember(memberIdx);

        String sMessage = super.langMessage("lang.member.success.delete"); // 탈퇴 완료하였습니다.

        return displayJson(true, "1000", sMessage);
    }

    /**
     * 회원 탈퇴 복구
     *
     * @param memberIdx
     * @return
     */
    @PatchMapping("/{idx}")
    public String restoreMember(@PathVariable(name = "idx") Long memberIdx) {

        super.adminAccessFail(4);

        memberService.restoreMember(memberIdx);

        String sMessage = super.langMessage("lang.member.success.restore"); // 계정을 복구하였습니다.

        return displayJson(true, "1000", sMessage);
    }


    /**
     * 코인, 무료코인, 마일리지 지급 & 차감
     *
     * @param memberIdx  :  member.idx
     * @param coinDto    :  지급 or 차감 개수 -> coin, coinFree, mileage / paymentType(결제 수단), memberIdx(회원),
     *                   :  지급과 차감 분기 조건 -> 'payment' = 지급 / 'subtract' = 차감
     * @return
     */
    @PutMapping("/{idx}/coin")
    public String memberCoinAndMileageModify(@PathVariable(name = "idx") Long memberIdx,
                                             CoinDto coinDto) {

        // 관리자 idx, 아이디 setting
        adminIdAndIdxSet(coinDto);
        coinDto.setMemberIdx(memberIdx); // 회원 idx set
        String sMessage = "";

        String actionType = coinDto.getActionType();

        // 지급 & 일반 지급
        if (actionType.equals("payment")) {
            if (coinDto.getPaymentType() == null || coinDto.getPaymentType().trim().isEmpty()) {
                throw new CustomException(CustomError.COIN_PAYMENT_TYPE_EMPTY); // 결제 수단을 선택해주세요.
            }
            coinService.coinPaymentByAdmin(coinDto);
            sMessage = super.langMessage("lang.coin.success.payment"); // 지급 완료되었습니다.
        }
        // 차감
        else if (actionType.equals("subtract")) {
            // 관리자 코인 or 무료 코인 or 마일리지 차감
            coinService.subtractCoinAndMileageByAdmin(coinDto);
            sMessage = super.langMessage("lang.coin.success.subtraction"); // 차감 완료되었습니다.
        } 

        return displayJson(true, "1000", sMessage);
    }

    /**
     * 회원 회차구매 내역(결제 사용내역)
     *
     * @param searchDto
     * @return
     */
    @GetMapping("/purchases")
    public String memberPurchaseList(@ModelAttribute SearchDto searchDto) {
        // 관리자 접근 권한
        super.adminAccessFail(4);

        //검색어 공백제거
        searchDto.setSearchWord(searchDto.getSearchWord().trim());

        // 전체 회차 구매내역 개수 조회
        int totalCnt = paymentService.getPurchaseTotalCnt(searchDto);

        // 내보낼 데이터 set
        String sMessage = ""; // 응답 메시지
        JSONObject joData = new JSONObject();
        List<MemberPurchaseDto> purchaseList = null;

        if (totalCnt < 1) {
            sMessage = super.langMessage("lang.common.exception.searchFail"); // 검색 결과가 없습니다.
        } else {
            // 회원 회차구매 내역 리스트 조회 (결제 사용내역)
            purchaseList = paymentService.getPurchaseList(searchDto);
            joData.put("params", new JSONObject(searchDto));
            sMessage = super.langMessage("lang.common.success.search"); // 조회 완료하였습니다.
        }
        joData.put("list", purchaseList); // 시청 내역 리스트

        return displayJson(true, "1000", sMessage, joData);
    }


    /**
     * 회원 리스트
     *
     * @param searchDto
     * @return
     */
    @GetMapping("/ott")
    public String memberOttList(@ModelAttribute SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(40);

        //검색어 공백제거
        searchDto.setSearchWord(searchDto.getSearchWord().trim());

        // 전체 회원 개수 조회
        int totalCnt = memberService.getMemberOttCnt(searchDto);
        // 내보낼 데이터 set
        String sMessage = ""; // 응답 메시지
        JSONObject joData = new JSONObject();
        List<MemberOttListDto> memberList = null;

        if (totalCnt < 1) {
            sMessage = super.langMessage("lang.common.exception.searchFail"); // 검색 결과가 없습니다.
        } else {
            memberList =  memberService.getOttMemberList(searchDto);
            sMessage = super.langMessage("lang.common.success.search"); // 조회 완료하였습니다.
            joData.put("params", new JSONObject(searchDto));
        }

        joData.put("list", memberList);

        return displayJson(true, "1000", sMessage, joData);
    }

    /**
     * 회원 리스트
     *
     * @param searchDto
     * @return
     */
    @GetMapping("/ott/stat")
    public String memberOttStat(@ModelAttribute SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(41);

        //검색어 공백제거
        searchDto.setSearchWord(searchDto.getSearchWord().trim());

        // 전체 회원 개수 조회
        int totalCnt = memberService.getOttMemberStatCnt(searchDto);
        // 내보낼 데이터 set
        String sMessage = ""; // 응답 메시지
        JSONObject joData = new JSONObject();
        List<MemberOttDto> memberList = null;

        if (totalCnt < 1) {
            sMessage = super.langMessage("lang.common.exception.searchFail"); // 검색 결과가 없습니다.
        } else {
            memberList =  memberService.getOttMemberStat(searchDto);
            sMessage = super.langMessage("lang.common.success.search"); // 조회 완료하였습니다.
            joData.put("params", new JSONObject(searchDto));
        }

        joData.put("list", memberList);

        return displayJson(true, "1000", sMessage, joData);
    }

    /**
     * OTT 통계 엑셀 다운로드
     *
     * @param searchDto
     * @return
     */
    @GetMapping("/ott/stat/excel")
    public ModelAndView memberOttStatExcelDownload(@ModelAttribute("param") SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(41);

        // 뷰에 담을 모델객체를 생성(엑셀 파일에 들어갈 데이터)
        Map<String, Object> mExcelData = memberService.excelOtt(searchDto);

        // AbstractXlsxView 가 동작
        return new ModelAndView(new ExcelXlsxView(), mExcelData);
    }

    /**
     * OTT 회원 엑셀 다운로드
     *
     * @param searchDto
     * @return
     */
    @GetMapping("/ott/member/excel")
    public ModelAndView memberOttExcelDownload(@ModelAttribute("param") SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(40);

        // 뷰에 담을 모델객체를 생성(엑셀 파일에 들어갈 데이터)
        Map<String, Object> mExcelData = memberService.excelOttMember(searchDto);

        // AbstractXlsxView 가 동작
        return new ModelAndView(new ExcelXlsxView(), mExcelData);
    }

    /*************************************************************
     * SUB
     *************************************************************/

    /**
     * 관리자 idx, 아이디 coinDto 에 setting
     *
     * @param coinDto -> 넘겨 받은 coinDto 원본에 adminIdx, adminId 세팅
     */
    private void adminIdAndIdxSet(CoinDto coinDto) {
        // 관리자 idx 구하기
        JSONObject memberInfo = new JSONObject(session.getAttribute("adminInfo").toString());
        Integer adminIdx = memberInfo.getInt("idx");
        String adminId = memberInfo.getString(SessionConfig.LOGIN_ID);

        // 관리자 idx set
        coinDto.setAdminIdx(adminIdx);
        coinDto.setAdminId(adminId);
    }
}
