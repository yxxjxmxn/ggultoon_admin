package com.architecture.admin.services.member;

import com.architecture.admin.libraries.PaginationLibray;
import com.architecture.admin.libraries.excel.ExcelData;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dao.member.MemberDao;
import com.architecture.admin.models.daosub.member.MemberDaoSub;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.coin.CoinDto;
import com.architecture.admin.models.dto.member.MemberDto;
import com.architecture.admin.models.dto.member.MemberOttDto;
import com.architecture.admin.models.dto.member.MemberOttListDto;
import com.architecture.admin.models.dto.member.MemberPurchaseDto;
import com.architecture.admin.models.dto.payment.PaymentDto;
import com.architecture.admin.services.BaseService;
import com.architecture.admin.services.coin.CoinService;
import com.architecture.admin.services.log.AdminActionLogService;
import com.architecture.admin.services.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MemberService extends BaseService {

    private final MemberDaoSub memberDaoSub;
    private final MemberDao memberDao;
    private final CoinService coinService;
    private final AdminActionLogService adminActionLogService; // 관리자 action log 처리용
    private final PaymentService paymentService;
    private final ExcelData excelData;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /****************************************************************
     * SELECT
     ****************************************************************/

    /**
     * 회원 리스트 엑셀 다운로드
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public Map<String, Object> excelMemberList(SearchDto searchDto) {

        // 검색어 공백제거
        if (!searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // send data set
        List<MemberDto> memberList = null;

        // 회원 수 카운트
        int totalCnt = memberDaoSub.getMemberTotalCnt(searchDto);

        // 회원이 있는 경우
        if (totalCnt > 0) {

            // 회원 리스트 조회
            memberList = memberDaoSub.getMemberList(searchDto);

            // 문자 변환
            stateText(memberList);
        }
        // 엑셀 데이터 생성
        return excelData.createExcelData(memberList, MemberDto.class);
    }

    /**
     * 전체 회원 집계 리스트 조회
     */
    @Transactional(readOnly = true)
    public List<MemberDto> getMemberCntList() {

        // 전체 회원 집계 리스트
        List<MemberDto> memberCntList = memberDaoSub.getMemberCntList();

        if (!memberCntList.isEmpty()) {

            int totalOutCnt = 0;
            int totalNormalCnt = 0;

            for (MemberDto dto : memberCntList) {

                if (dto != null) {

                    /** 전체 회원 수 집계 **/
                    totalNormalCnt = totalNormalCnt + dto.getNormalMemberCnt(); // 가입 회원
                    totalOutCnt = totalOutCnt + dto.getOutMemberCnt(); // 탈퇴 회원

                    /** 구분값 문자 변환 **/
                    if (dto.getSimpleType() == null || dto.getSimpleType().equals("")) {
                        dto.setSimpleType("일반");

                    } else if (dto.getSimpleType().equals("kakao")) {
                        dto.setSimpleType("카카오");

                    } else if (dto.getSimpleType().equals("naver")) {
                        dto.setSimpleType("네이버");
                    }
                }
            }

            // 전체 회원 수 set
            MemberDto totalMember = new MemberDto();
            totalMember.setOutMemberCnt(totalOutCnt);
            totalMember.setNormalMemberCnt(totalNormalCnt);
            totalMember.setIsSimple(0);
            totalMember.setSimpleType("전체");
            memberCntList.add(totalMember);
        }

        return memberCntList;
    }

    /**
     * 회원 전체 개수 조회
     *
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public int getMemberTotalCnt(SearchDto searchDto) {
        int totalCnt = memberDaoSub.getMemberTotalCnt(searchDto);

        Integer recordSize = searchDto.getSearchCount();

        if (recordSize != null && recordSize > 0) {
            // 한페이지 보여주는 개수 수정
            searchDto.setRecordSize(recordSize);
        }

        // paging
        PaginationLibray pagination = new PaginationLibray(totalCnt, searchDto);
        searchDto.setPagination(pagination);

        return totalCnt;
    }

    /**
     * 회원 리스트 조회
     *
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public List<MemberDto> getMemberList(SearchDto searchDto) {

        List<MemberDto> memberDtoList = memberDaoSub.getMemberList(searchDto);

        // 문자 변환
        stateText(memberDtoList);

        return memberDtoList;
    }

    /**
     * 회원 기본 정보(상세)
     *
     * @param memberIdx
     * @return
     */
    @Transactional(readOnly = true)
    public JSONObject getMemberDetail(Long memberIdx) {

        JSONObject data = new JSONObject();

       /** 1. 회원 기본 정보 조회 **/
        MemberDto memberDto = memberDaoSub.getMemberBasicInfo(memberIdx);
        // 문자 변환
        stateText(memberDto);

        /** 2. 누적 결제 코인 조회 **/
        CoinDto coinDto = coinService.getMemberTotalCoinInfo(memberIdx);

        if (coinDto != null) {
            data.put("coin", new JSONObject(coinDto));  // 회원 코인 내역(전체코인, 사용가능 코인, 사용 코인.. 마일리지도 포함)
        }

        // 구매내역 개수 조회
        int purchaseCnt = paymentService.getMemberPurchaseTotalCnt(memberIdx);

        if (purchaseCnt > 0) {
            /** 3. 구매 내역 리스트 조회 **/
            List<MemberPurchaseDto> purchaseList = paymentService.getMemberPurchaseInfo(memberIdx);

            if (!purchaseList.isEmpty()) {
                data.put("purchaseList", purchaseList);     // 시청 내역 리스트
            }
        }

        // 전체 일별 결제 내역 카운트
        SearchDto searchDto = new SearchDto();
        searchDto.setIdx(memberIdx);

        int paymentCnt = paymentService.getPaymentTotalCnt(searchDto);

        if (paymentCnt > 0) {
            /** 4. 결제 내역 리스트 조회 **/
            List<PaymentDto> paymentList = paymentService.getPaymentList(searchDto);

            // 결제 취소 버튼 활성화 여부 세팅
            if (!paymentList.isEmpty()) {
                setCancelButton(paymentList);
            }
            data.put("paymentList", paymentList);       // 결제 내역 리스트
        }

        /** 5. 회원 정책 정보 조회 **/
        List<MemberDto> memberPolicyInfo = memberDaoSub.getMemberPolicyInfo(searchDto);
        for (MemberDto dto : memberPolicyInfo) {

            // 마케팅 광고 동의
            if (dto.getPolicy().equals("marketing")) {
                if (dto.getPolicyState().equals("Y")) {
                    memberDto.setMarketing("Y");
                } else {
                    memberDto.setMarketing("N");
                }
                memberDto.setModifyDate(dto.getModifyDate());
            }
        }

        /** 6. 관리자 지급 내역 **/
        searchDto.setIsAdd("Y");
        List<CoinDto> coinSaveList = coinService.getAdminCoinList(searchDto);
        if (!coinSaveList.isEmpty()) {
            data.put("coinSaveList", coinSaveList);// 코인 적립 내역
        }
        int coinSaveCnt = (coinSaveList != null ? coinSaveList.size() : 0);

        /** 7. 코인 차감 내역 **/
        searchDto.setIsAdd("N");
        List<CoinDto> coinSubtractList = coinService.getAdminCoinList(searchDto);
        if (!coinSaveList.isEmpty()) {
            data.put("coinSubtractList", coinSubtractList);// 코인 차감 내역
        }
        int coinSubtractCnt = (coinSubtractList != null ? coinSubtractList.size() : 0);

        data.put("purchaseCount", purchaseCnt);         // 구매내역 건수
        data.put("paymentCount", paymentCnt);           // 결제 내역 건수
        data.put("member", new JSONObject(memberDto));  // 회원 기본 정보
        data.put("coinSaveCount", coinSaveCnt);         // 관리자 적립 개수
        data.put("coinSubtractCount", coinSubtractCnt); // 관리자 차감 개수

        return data;
    }

    /**
     * ott 가입 회원 리스트 조회
     *
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public List<MemberOttListDto>  getOttMemberList(SearchDto searchDto) {

       return memberDaoSub.getMemberOtt(searchDto);
    }

    /**
     * 회원 전체 개수 조회
     *
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public int getMemberOttCnt(SearchDto searchDto) {
        int totalCnt = memberDaoSub.getMemberOttCnt(searchDto);

        Integer recordSize = searchDto.getSearchCount();

        if (recordSize != null && recordSize > 0) {
            // 한페이지 보여주는 개수 수정
            searchDto.setRecordSize(recordSize);
        }

        // paging
        PaginationLibray pagination = new PaginationLibray(totalCnt, searchDto);
        searchDto.setPagination(pagination);

        return totalCnt;
    }

    /**
     * ott 가입 회원 통계
     *
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public List<MemberOttDto> getOttMemberStat(SearchDto searchDto) {

        return memberDaoSub.getOttMemberStat(searchDto);
    }

    /**
     * ott 가입 통계엑셀 다운로드
     * @return
     */
    @Transactional(readOnly = true)
    public Map<String, Object> excelOtt(SearchDto searchDto) {

        // 내보낼 데이터 set
        List<MemberOttDto> ottList = null;

        // 전체 카운트
        int totalCnt = memberDaoSub.getMemberOttCnt(searchDto);

        // 전체 카운트 있는 경우
        if (totalCnt > 0) {

            // 리스트 조회
            ottList = memberDaoSub.getOttMemberStat(searchDto);
        }

        // 엑셀 데이터 생성
        return excelData.createExcelData(ottList, MemberOttDto.class);
    }

    /**
     * ott 가입 회원 리스트 엑셀 다운로드
     * @return
     */
    @Transactional(readOnly = true)
    public Map<String, Object> excelOttMember(SearchDto searchDto) {

        // 내보낼 데이터 set
        List<MemberOttListDto> ottList = null;

        // 전체 카운트
        int totalCnt = memberDaoSub.getMemberOttCnt(searchDto);

        // 전체 카운트 있는 경우
        if (totalCnt > 0) {

            // 리스트 조회
            ottList = memberDaoSub.getMemberOtt(searchDto);
        }

        // 엑셀 데이터 생성
        return excelData.createExcelData(ottList, MemberOttListDto.class);
    }

    /**
     * 회원 전체 개수 조회
     *
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public int getOttMemberStatCnt(SearchDto searchDto) {
        int totalCnt = memberDaoSub.getOttMemberStatCnt(searchDto);

        Integer recordSize = searchDto.getSearchCount();

        if (recordSize != null && recordSize > 0) {
            // 한페이지 보여주는 개수 수정
            searchDto.setRecordSize(recordSize);
        }

        // paging
        PaginationLibray pagination = new PaginationLibray(totalCnt, searchDto);
        searchDto.setPagination(pagination);

        return totalCnt;
    }

    /****************************************************************
     * UPDATE
     ****************************************************************/

    /**
     * 회원 정보 수정
     *
     * @param memberDto
     */
    @Transactional
    public void modifyMemberInfo(MemberDto memberDto) {
        // 기본 유효성 검사
        modifyValidate(memberDto);
        // 공백 제거
        memberDto.setChangeNick(memberDto.getChangeNick().trim());

        String changeNick = memberDto.getChangeNick(); // 변경하고자 하는 닉네임
        String nowNick = memberDto.getNick();       // 현재 닉네임

        // 닉네임 수정한 경우
        if (!changeNick.equals(nowNick)) {
            // 닉네임 중복 검사
            int isDuplicate = memberDaoSub.inspectDuplicateNick(changeNick);

            switch (isDuplicate) {
                // 중복된 경우
                case 1 -> throw new CustomException(CustomError.DUPLICATE_NICK_MEMBER_ERROR);
                // 중복 아닌 경우
                default -> memberDao.modifyMemberNick(memberDto); // 회원정보 수정
            }
        }

        // 회원 정보 수정
        memberDao.modifyMemberInfo(memberDto);

        // 관리자 action log
        adminActionLogService.regist(memberDto, Thread.currentThread().getStackTrace());
    }

    /**
     * 회원 탈퇴
     *
     * @param memberIdx : 회원 idx
     */
    @Transactional
    public void deleteMember(Long memberIdx) {

        // 탈퇴하려는 회원 정보 조회 : idx, id, nick
        MemberDto member = memberDaoSub.getOutMemberInfo(memberIdx);

        // 1. member 테이블 update (state=0)
        memberDao.deleteMemberByAdmin(memberIdx);

        // 2. member_info 테이블 update (state=0)
        memberDao.deleteMemberInfoByAdmin(memberIdx);

        // 3. member_out 테이블 insert
        MemberDto outMember = MemberDto.builder()
                .idx(member.getIdx())
                .id(member.getId())
                .nick(member.getNick())
                .outDate(dateLibrary.getDatetime())
                .build();

        memberDao.insertMemberOutByAdmin(outMember);

        // 4. 관리자 로그
        adminActionLogService.regist(memberIdx, Thread.currentThread().getStackTrace());
    }

    /**
     * 회원 탈퇴 복구
     *
     * @param memberIdx
     */
    @Transactional
    public void restoreMember(Long memberIdx) {

        // 1. member 테이블 (state=1)
        memberDao.restoreMemberByAdmin(memberIdx);

        // 2. member_info 테이블 (state=1)
        memberDao.restoreMemberInfoByAdmin(memberIdx);

        // 3. member_out 테이블에서 row delete
        memberDao.deleteMemberOut(memberIdx);

        // 회원 정보 조회
        MemberDto memberDto = memberDaoSub.getMemberBasicInfo(memberIdx);

        // 해당 회원이 간편가입 회원이라면
        if (memberDto.getJoinType() != null || !memberDto.getJoinType().isEmpty()) {

            // 4. member_simple_out 테이블에서 row delete
            memberDao.deleteMemberSimpleOut(memberIdx);
        }

        // 5. 관리자 로그
        adminActionLogService.regist(memberIdx, Thread.currentThread().getStackTrace());
    }

    /****************************************************************
     * SUB
     ****************************************************************/

    /**
     * 멤버 리스트 문자 변환
     *
     * @param memberDtoList
     */
    private void stateText(List<MemberDto> memberDtoList) {

        for (MemberDto memberDto : memberDtoList) {
            stateText(memberDto);
        }

    }

    /**
     * dto 문자 변환
     *
     * @param dto
     */
    private void stateText(MemberDto dto) {

        // 상태값
        if (dto.getState() != null) {
            if (dto.getState() == 1) {//정상
                dto.setStateText(super.langMessage("lang.member.state.normal"));
                dto.setStateBg("bg-success");
            } else if (dto.getState() == 0 || dto.getState() == 2) {// 탈퇴
                dto.setStateText(super.langMessage("lang.member.state.out"));
                dto.setStateBg("bg-danger");
            }
        }

        // 성별
        if (dto.getGender() != null && !dto.getGender().isEmpty()) {
            if (dto.getGender().equals("M")) {// 남
                dto.setGenderText(super.langMessage("lang.member.gender.male"));
            } else if (dto.getGender().equals("F")) {// 여
                dto.setGenderText(super.langMessage("lang.member.gender.female"));
            }
        } else {
            dto.setGenderText("");
        }

        // 언어 표기
        if (dto.getLang() != null) {
            switch (dto.getLang()) {
                case "ko" -> dto.setLangText(super.langMessage("lang.member.lang.ko"));
                case "en" -> dto.setLangText(super.langMessage("lang.member.lang.en"));
                default -> dto.setLangText("");
            }
        }

        // 본인 인증
        if (dto.getAuth() != null) {
            if (dto.getAuth() == 0) {// 미인증
                dto.setAuthText(super.langMessage("lang.member.auth.no"));
            } else if (dto.getAuth() == 1) { // 인증
                dto.setAuthText(super.langMessage("lang.member.auth.ok"));
            }
        }

        // 성인 인증
        if (dto.getAdult() != null) {
            if (dto.getAdult() == 0) {// 미인증
                dto.setAdultText(super.langMessage("lang.member.auth.no"));
            } else if (dto.getAdult() == 1) { // 인증
                dto.setAdultText(super.langMessage("lang.member.auth.ok"));
            }
        }

        // 간편가입
        if (dto.getJoinType() != null) {
            if (dto.getJoinType().trim().isEmpty()) {// 일반가입
                dto.setJoinType(super.langMessage("lang.member.normalJoin"));
            }
        }
    }

    /**
     * 회원 수정 기본 유효성 검사
     *
     * @param memberDto
     */
    private void modifyValidate(MemberDto memberDto) {
        // 입력값 체크
        // idx
        if (memberDto.getIdx() == null || memberDto.getIdx() < 1L) {
            throw new CustomException(CustomError.IDX_MEMBER_ERROR);
        }
        // 사용언어
        if (memberDto.getLang() == null || memberDto.getLang().trim().isEmpty()) {
            throw new CustomException(CustomError.LANG_MEMBER_ERROR); // 사용언어를 선택해주세요.
        }
        // 전화번호
        if (memberDto.getPhone() == null || memberDto.getPhone().trim().isEmpty()) {
            throw new CustomException(CustomError.PHONE_MEMBER_ERROR); // 전화번호를 입력해주세요.
        }
        // 생년월일
        if (memberDto.getBirth() == null || memberDto.getBirth().trim().isEmpty()) {
            throw new CustomException(CustomError.BIRTH_MEMBER_ERROR); // 생년월일을 입력해주세요.
        }
        // 닉네임
        if (memberDto.getChangeNick() == null || memberDto.getChangeNick().trim().isEmpty()) {
            throw new CustomException(CustomError.NICK_MEMBER_ERROR); // 닉네임을 입력해주세요.
        }
    }

    /*************************************************************
     * Modules
     *************************************************************/
    /**
     * 결제 취소 버튼 활성화 여부 세팅
     * @param paymentList : 결제 취소 버튼을 세팅할 결제 내역
     */
    @SneakyThrows
    public void setCancelButton(List<PaymentDto> paymentList) {

        // 현재 날짜 구하기
        Date now = new Date();

        for (PaymentDto dto : paymentList) {

            // 결제 취소 버튼 상태값(기본값 : 노출X)
            dto.setButtonState("hidden");

            // 취소되지 않은 결제건일 경우
            if (dto.getState() == 1) {

                // 결제일
                Date registerDate = sdf.parse(dto.getRegdate());

                // 결제 취소 가능 기간 만료일 구하기
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(registerDate);
                calendar.add(Calendar.DATE, 7); // 결제일로부터 7일 뒤
                Date expireDate = calendar.getTime();  // 결제 취소 가능 기간 만료일

                // 취소하려는 시점이 결제일 ~ 만료일 사이인 경우
                if (registerDate.compareTo(now) <= 0 && expireDate.compareTo(now) >= 0) {

                    /** 결제 시 지급받은 코인 회수 가능 여부 체크 **/
                    if (dto.getCoin() > 0) {

                        // 코인 회수 가능 여부 set(기본값 : false)
                        dto.setIsCoinTrue(false);

                        if (dto.getCoinState() == 1) {

                            if (dto.getCoin().equals(dto.getRestCoin())) {

                                if (!dto.getCoinExp().isEmpty()) {

                                    // 지급된 코인 만료일
                                    Date coinExp = sdf.parse(dto.getCoinExp());

                                    if (coinExp.compareTo(now) > 0) {
                                        dto.setIsCoinTrue(true); // 코인 회수 가능 set
                                    }
                                }
                            }
                        }
                    }

                    /** 결제 시 지급받은 보너스 회수 가능 여부 체크 **/
                    if (dto.getBonus() > 0) {

                        // 보너스 회수 가능 여부 set(기본값 : false)
                        dto.setIsBonusTrue(false);

                        if (dto.getBonusState() == 1) {

                            if (dto.getBonus().equals(dto.getRestBonus())) {

                                if (!dto.getBonusExp().isEmpty()) {

                                    // 지급된 보너스 만료일
                                    Date bonusExp = sdf.parse(dto.getBonusExp());

                                    if (bonusExp.compareTo(now) > 0) {
                                        dto.setIsBonusTrue(true); // 보너스 회수 가능 set
                                    }
                                }
                            }
                        }
                    }

                    /** 결제 시 지급받은 마일리지 회수 가능 여부 체크 **/
                    if (dto.getMileage() > 0) {

                        // 마일리지 회수 가능 여부 set(기본값 : false)
                        dto.setIsMileageTrue(false);

                        if (dto.getMileageState() == 1) {

                            if (dto.getMileage().equals(dto.getRestMileage())) {

                                if (!dto.getMileageExp().isEmpty()) {

                                    // 지급된 마일리지 만료일
                                    Date mileageExp = sdf.parse(dto.getMileageExp());

                                    if (mileageExp.compareTo(now) > 0) {
                                        dto.setIsMileageTrue(true); // 마일리지 회수 가능 set
                                    }
                                }
                            }
                        }
                    }
                }
                // 코인, 보너스, 마일리지 회수 가능 여부 결과
                List<Boolean> result = new ArrayList<>();
                result.add(dto.getIsCoinTrue());    // 코인 회수 가능 여부
                result.add(dto.getIsBonusTrue());   // 보너스 회수 가능 여부
                result.add(dto.getIsMileageTrue()); // 마일리지 회수 가능 여부

                // 코인, 보너스, 마일리지 모두 결제 취소 가능 조건 충족 시
                if (!result.contains(false)) {
                    dto.setButtonState("button"); // 결제 취소 버튼 노출
                }
            }
        }
    }
}
