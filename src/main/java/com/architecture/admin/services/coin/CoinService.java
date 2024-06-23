package com.architecture.admin.services.coin;

import com.architecture.admin.libraries.PaginationLibray;
import com.architecture.admin.libraries.excel.ExcelData;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dao.coin.CoinDao;
import com.architecture.admin.models.dao.payment.PaymentDao;
import com.architecture.admin.models.daosub.coin.CoinDaoSub;
import com.architecture.admin.models.daosub.payment.PaymentDaoSub;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.coin.CoinDto;
import com.architecture.admin.models.dto.coin.MileageSaveDto;
import com.architecture.admin.models.dto.coin.MileageUseDto;
import com.architecture.admin.models.dto.payment.PaymentDto;
import com.architecture.admin.services.BaseService;
import com.architecture.admin.services.log.AdminActionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CoinService extends BaseService {

    private static final String MILEAGE = "mileage";
    private static final String COIN = "coin";
    private static final String COIN_FREE = "coinFree";
    private static final String PAY = "Y";      // 지급
    private static final String SUBTRACT = "N"; // 차감

    private final CoinDao coinDao;
    private final CoinDaoSub coinDaoSub;
    private final PaymentDao paymentDao;
    private final PaymentDaoSub paymentDaoSub;
    private final AdminActionLogService adminActionLogService;// 관리자 action log 처리용
    private final ExcelData excelData;

    /*********************************************************************
     * SELECT
     *********************************************************************/

    /**
     * 코인 적립내역 개수
     *
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public int getSavedCoinTotalCnt(SearchDto searchDto) {

        int totalCnt = coinDaoSub.getSavedCoinTotalCnt(searchDto);

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
     * 코인 사용내역 개수
     *
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public int getUsedCoinTotalCnt(SearchDto searchDto) {

        int totalCnt = coinDaoSub.getUsedCoinTotalCnt(searchDto);

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
     * 관리자 코인 지급 & 차감 개수
     *
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public int getAdminCoinTotalCnt(SearchDto searchDto) {
        // 관리자 지급 or 차감 리스트 총 개수
        int totalCnt = coinDaoSub.getAdminCoinTotalCnt(searchDto);

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
     * 마일리지 적립 내역 개수
     *
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public int getSavedMileageTotalCnt(SearchDto searchDto) {

        int totalCnt = coinDaoSub.getSavedMileageTotalCnt(searchDto);

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
     * 마일리지 사용 내역 개수
     *
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public int getUsedMileageTotalCnt(SearchDto searchDto) {

        int totalCnt = coinDaoSub.getUsedMileageTotalCnt(searchDto);

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
     * 코인 적립 내역 리스트
     *
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public List<CoinDto> getSavedCoinTotalList(SearchDto searchDto) {

        // 코인 적립 내역 조회
        List<CoinDto> savedCoinDtoList = coinDaoSub.getSavedCoinTotalList(searchDto);

        // 문자 변환
        coinStateText(savedCoinDtoList);

        return savedCoinDtoList;
    }

    /**
     * 코인 사용 내역 리스트
     *
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public List<CoinDto> getUsedCoinTotalList(SearchDto searchDto) {

        // 코인 사용 내역 조회
        List<CoinDto> usedCoinDtoList = coinDaoSub.getUsedCoinTotalList(searchDto);

        // 문자 변환
        coinStateText(usedCoinDtoList);

        return usedCoinDtoList;
    }

    /**
     * 마일리지 적립 내역 리스트 엑셀 다운로드
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public Map<String, Object> excelSaveMileageList(SearchDto searchDto) {

        // 검색어 공백제거
        if (!searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // send data set
        List<MileageSaveDto> saveMileageList = null;

        // 마일리지 적립 내역 카운트
        int totalCnt = coinDaoSub.getSavedMileageTotalCnt(searchDto);

        // 마일리지 적립 내역이 있는 경우
        if (totalCnt > 0) {

            // 마일리지 적립 내역 조회
            saveMileageList = coinDaoSub.getSavedMileageTotalList(searchDto);

            // 문자 변환
            saveMileageStateText(saveMileageList);
        }
        // 엑셀 데이터 생성
        return excelData.createExcelData(saveMileageList, MileageSaveDto.class);
    }

    /**
     * 마일리지 적립 내역 리스트
     *
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public List<MileageSaveDto> getSavedMileageTotalList(SearchDto searchDto) {

        // 마일리지 적립 내역 조회
        List<MileageSaveDto> saveMileageList = coinDaoSub.getSavedMileageTotalList(searchDto);

        // 문자 변환
        saveMileageStateText(saveMileageList);

        return saveMileageList;
    }

    /**
     * 마일리지 사용 내역 리스트 엑셀 다운로드
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public Map<String, Object> excelUseMileageList(SearchDto searchDto) {

        // 검색어 공백제거
        if (!searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // send data set
        List<MileageUseDto> usedMileageList = null;

        // 마일리지 사용 내역 카운트
        int totalCnt = coinDaoSub.getUsedMileageTotalCnt(searchDto);

        // 마일리지 사용 내역이 있는 경우
        if (totalCnt > 0) {

            // 마일리지 사용 내역 조회
            usedMileageList = coinDaoSub.getUsedMileageTotalList(searchDto);

            // 문자 변환
            usedMileageStateText(usedMileageList);
        }
        // 엑셀 데이터 생성
        return excelData.createExcelData(usedMileageList, MileageUseDto.class);
    }


    /**
     * 마일리지 사용 내역 리스트
     *
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public List<MileageUseDto> getUsedMileageTotalList(SearchDto searchDto) {

        // 마일리지 사용 내역 조회
        List<MileageUseDto> usedMileageList = coinDaoSub.getUsedMileageTotalList(searchDto);

        // 문자 변환
        usedMileageStateText(usedMileageList);

        return usedMileageList;
    }

    /**
     * 코인 종합 정보 조회(회원 상세보기 시)
     *
     * @param memberIdx
     * @return
     */
    @Transactional(readOnly = true)
    public CoinDto getMemberTotalCoinInfo(Long memberIdx) {

        // 누적 결제 코인 & 누적 사용 코인 & 사용 가능 코인 조회
        CoinDto coinInfoDto = coinDaoSub.getMemberCoinInfo(memberIdx);

        // 누적 마일리지 & 누적 사용 마일리지 & 사용 가능 마일리지 조회
        CoinDto mileageInfoDto = coinDaoSub.getMemberMileageInfo(memberIdx);

        // 누적 마일리지 set
        coinInfoDto.setMileage(mileageInfoDto.getMileage());
        // 사용 가능 마일리지 set
        coinInfoDto.setRestMileage(mileageInfoDto.getRestMileage());
        // 누적 사용 마일리지 set
        coinInfoDto.setUsedMileage(mileageInfoDto.getUsedMileage());

        return coinInfoDto;
    }

    /**
     * coin_used 테이블 등록(코인 지급 시)
     *
     * @param coinDto
     */
    private void insertCoinUsed(CoinDto coinDto) {

        // 구매 코인이 지급되었을 경우
        if (coinDto.getCoin() > 0) {
            coinDto.setCoinType(COIN);
            coinDto.setExpiredate(getExpireDate(COIN));
            coinDao.insertMemberCoinUsed(coinDto);
        }

        // 보너스 코인이 지급 되었을 경우
        if (coinDto.getCoinFree() > 0) {
            coinDto.setCoinType(COIN_FREE);
            coinDto.setExpiredate(getExpireDate(COIN_FREE));
            coinDao.insertMemberCoinUsed(coinDto);
        }
        coinDto.setType(null);
    }

    /**
     * 기존 코인, 보너스코인, 마일리지에 추가 지급금 합산하여
     * coinDto setting
     *
     * @param coinDto
     * @return : coinDto 에 기존 코인, 마일리지 차감 및 지급하여 coinDto set 하여 반환
     */
    private void existingCoinAndMileageSum(CoinDto coinDto, String action) {

        long memberIdx = coinDto.getMemberIdx();
        int sumCoin = 0;
        int sumCoinFree = 0;
        int sumMileage = 0;

        // 기존 코인 조회(restCoinDto -> 기존 코인 & 보너스 코인 & 마일리지 담겨있음)
        CoinDto restCoinDto = coinDaoSub.getRestCoinAndCoinFree(memberIdx); // 접두어 rest 에 해당하는 코인, 보너스코인, 마일리지 담겨옴
        CoinDto restMileageDto = coinDaoSub.getRestMileage(memberIdx); // 잔여 마일리지 조회

        // 차감인 경우
        if (action.equals("subtract")) {

            // 기존 코인 - 차감할 코인 합산
            sumCoin = restCoinDto.getRestCoin() - coinDto.getCoin();
            sumCoinFree = restCoinDto.getRestCoinFree() - coinDto.getCoinFree();
            sumMileage = restMileageDto.getRestMileage() - coinDto.getMileage();

        } else if (action.equals("sum")) { // 지급인 경우

            // 기존 코인 + 지급된 코인 합산
            sumCoin = restCoinDto.getRestCoin() + coinDto.getCoin();
            sumCoinFree = restCoinDto.getRestCoinFree() + coinDto.getCoinFree();
            sumMileage = restMileageDto.getRestMileage() + coinDto.getMileage();

        }

        // 합산한 금액 coinDto 에 set
        coinDto.setCoin(sumCoin);
        coinDto.setCoinFree(sumCoinFree);
        coinDto.setMileage(sumMileage);
    }


    /*********************************************************************
     * INSERT / UPDATE
     *********************************************************************/

    /**
     * 코인 & 무료 코인 & 마일리지 지급
     *
     * @param coinDto
     */
    @Transactional
    public void coinPaymentByAdmin(CoinDto coinDto) {

        // 유효성 검사
        paymentCoinValidate(coinDto);

        // dto setting
        coinDto.setExpiredate(getExpireDate(COIN)); // 구매 코인 만료일
        coinDto.setRegdate(dateLibrary.getDatetime()); // 등록일
        coinDto.setIsAdd("Y"); // 적립
        coinDto.setTitle(coinDto.getReason()); // 지급사유
        coinDto.setAdmin(coinDto.getAdminId()); // 지급한 관리자 ID

        String paymentType = coinDto.getPaymentType(); // 결제 지급 & 일반 지급 구별

        // 결제 지급
        if (!paymentType.equals("normal")) {
            /** 1. payment(결제) 테이블에 등록 **/
            paymentDao.insertPaymentByAdmin(coinDto); // useGeneratedKey -> payment_idx

            // 첫 결제인 지 조회
            int paymentCnt = paymentDaoSub.getPaymentCnt(coinDto.getMemberIdx());

            PaymentDto paymentDto = PaymentDto.builder()
                    .idx(coinDto.getPaymentIdx())
                    .first(0)
                    .regdate(dateLibrary.getDatetime()).build();

            // 첫 결제
            if (paymentCnt < 1) {
                paymentDto.setFirst(1); // 첫 결제 set
            }

            /** payment_info(결제) 테이블에 등록 **/
            paymentDao.insertPaymentInfo(paymentDto);

            /** payment_log(로그) 테이블에 등록 **/
            paymentDto.setPaymentType(2); // 결제 완료 set
            paymentDto.setProductIdx(0L);
            paymentDto.setMemberIdx(coinDto.getMemberIdx());
            paymentDto.setPay(String.valueOf(coinDto.getPaymentPrice())); // 결제 금액
            paymentDao.insertPaymentLog(paymentDto);

            // 일반 지급
        } else {
            coinDto.setPaymentIdx(0L);
        }

        // 구매 코인, 보너스 코인, 마일리지 분기
        /** save & log 테이블 등록 **/
        if (coinDto.getMileage() > 0) { // 마일리지 지급 입력 시 등록

            coinDto.setAchievementIdx(0L);                        // 관리자 지급일 경우 업적번호는 0번으로 set
            coinDto.setMileageExpireDate(getExpireDate(MILEAGE)); // 마일리지 만료일 set

            /** 2. member_mileage_save 등록 **/
            coinDto.setPosition(super.langMessage("lang.coin.admin")); // 유입 경로 "관리자"
            coinDao.insertMemberMileageSave(coinDto);

            /** 3. member_mileage_save_log 등록 **/
            coinDao.insertMileageSaveLog(coinDto);

            /** 4. member_mileage_used 등록 **/
            coinDao.insertMemberMileageUsed(coinDto);
        }

        // 구매 코인 or 보너스 코인이 입력 되었을 경우
        if (coinDto.getCoin() > 0 || coinDto.getCoinFree() > 0) {

            coinDto.setProductIdx(0); // 관리자 지급일 경우 상품 번호 0번으로 set
            coinDto.setCoinFreeExpireDate(getExpireDate(COIN_FREE)); // 보너스 코인 만료일 set
            /** 2. member_coin_save 등록 **/
            coinDao.insertMemberCoinSave(coinDto); // useGeneratedKey -> insertedIdx

            /** 3. member_coin_save_log 등록 **/
            coinDao.insertCoinSaveLog(coinDto);

            /** 4. member_coin_used 등록 **/
            insertCoinUsed(coinDto);
        }
        /** 5. admin_coin 등록(관리자 지급) **/
        insertAdminCoin(coinDto);

        /** 6. admin_coin_log 등록 (관리자 지급 로그) **/
        insertAdminCoinLog(coinDto);

        /** 7. 기존 코인, 보너스 코인, 마일리지 + 지급 금액 합산하여 coinDto 에 담아서 반환 **/
        existingCoinAndMileageSum(coinDto, "sum"); // 합산일 경우 action = "sum"

        /** 8. member_coin 테이블 업데이트 **/
        coinDao.updateMemberCoin(coinDto);

        // 관리자 action log
        adminActionLogService.regist(coinDto, Thread.currentThread().getStackTrace());
    }

    /**
     * 코인 & 무료 코인 & 마일리지 차감
     *
     * @param coinDto
     */
    @Transactional
    public void subtractCoinAndMileageByAdmin(CoinDto coinDto) {

        // 유효성 검사
        subtractCoinValidate(coinDto);

        // dto setting
        String adminId = coinDto.getAdminId();
        long memberIdx = coinDto.getMemberIdx();
        coinDto.setRegdate(dateLibrary.getDatetime()); // 등록일
        coinDto.setIsAdd("N"); // 차감
        coinDto.setTitle(coinDto.getReason() + "(" + adminId + " " + super.langMessage("lang.coin.subtract") + ")"); // 차감 사유 & 차감자 id

        /** 1. member_coin 테이블에서 차감 되기전 개수 조회 **/
        CoinDto restCoinDto = coinDaoSub.getRestCoinAndCoinFree(memberIdx); // 잔여 코인 & 보너스 코인 조회
        CoinDto restMileageDto = coinDaoSub.getRestMileage(memberIdx);      // 잔여 마일리지 조회

        // 차감 개수가 잔여 코인 & 마일리지보다 크다면 Exception
        if (coinDto.getCoin() > restCoinDto.getRestCoin()) {
            throw new CustomException(CustomError.COIN_AMOUNT_OVER);        // 보유 코인보다 차감액이 큽니다.
        }
        if (coinDto.getCoinFree() > restCoinDto.getRestCoinFree()) {
            throw new CustomException(CustomError.COIN_AMOUNT_OVER);        // 보유 코인보다 차감액이 큽니다.
        }
        if (coinDto.getMileage() > restMileageDto.getRestMileage()) {
            throw new CustomException(CustomError.COIN_MILEAGE_AMOUNT_OVER); // 보유 마일리지보다 차감액이 큽니다.
        }

        /** 2. coin_used 테이블 update (코인 차감)**/
        if (coinDto.getCoin() > 0) {
            subtractCoinOrCoinFree(coinDto, 1);
        }

        /** 3. coin_used 테이블 update (무료 코인 차감)**/
        if (coinDto.getCoinFree() > 0) {
            subtractCoinOrCoinFree(coinDto, 2);
        }
        /** 4. mileage_used 테이블 update (마일리지 차감)**/
        if (coinDto.getMileage() > 0) {
            subtractMileage(coinDto);
        }

        /** 5. admin_coin 테이블 등록 **/
        insertAdminCoin(coinDto);

        /** 6. admin_coin_log 테이블 등록(공통) **/
        insertAdminCoinLog(coinDto);

        /** 7. 기존 잔여 금액(member_coin 테이블) 조회 후 잔여금액 - 차감할 금액 계산하여 coinDto 에 setting **/
        existingCoinAndMileageSum(coinDto, "subtract"); // 차감인 경우 action = subtract

        /** 8. member_coin 테이블 업데이트(공통) **/
        coinDao.updateMemberCoin(coinDto); // 계산하여 반환 받은 coin, coinFree, mileage 로 회원 잔여 코인 업데이트(member_coin 테이블)
        // 기존 member_coin 의 개수가 used 테이블과 동기화 되어있지 않았어도 이 쿼리문으로 동기화됨

        // 관리자 action log
        adminActionLogService.regist(coinDto, Thread.currentThread().getStackTrace());
    }

    /**
     * 코인 or 보너스 코인 차감(coin_used 테이블 update)
     *
     * @param coinDto : coin, coinFree (차감할 금액 ) 정보 담김
     * @param type    : '1'이면 coin , '2'이면 coin_free 차감
     */
    private void subtractCoinOrCoinFree(CoinDto coinDto, int type) {

        long beforeIdx = 0;              // 무한 루프 방지용
        Boolean init = true;             // 초기값. 차감할 금액 최초 한번만 실행 되도록 판별할 때 사용
        int subCoin = 1;                 // 차감할 코인 (while 문 진입 위해 1로 초기화)
        int subResult = 0;               // 조회한 rest_coin 에서 차감된 결과 값 누적 저장
        coinDto.setType(type);           // 코인유형 : '1'(코인) , '2'(무료코인)
        CoinDto restCoinDto;             // 남은 코인 조회 용 dto


        // 차감할 금액이 0이 될 때 까지 반복
        while (subCoin > 0) {

            // 첫 진입시만 실행(차감할 금액 set)
            // while 문 마지막에서 init 변수 false 로 변경
            if (init == Boolean.TRUE && type == 1) { // 코인 차감
                // 코인
                subCoin = coinDto.getCoin();
            } else if (init == Boolean.TRUE && type == 2) { // 무료 코인 차감
                // 무료 코인
                subCoin = coinDto.getCoinFree();
            }

            // 음수일 경우 이전 값 들어있으므로 초기화 후 진행
            coinDto.setSubResultCoin(null);  // db에 차감할 코인 초기화

            // 1. 남은 코인(rest_coin) & coin_used.idx 조회
            restCoinDto = coinDao.getRestCoinAndIdxFromCoinUsed(coinDto);

            // 무한 루프 방지
            if (beforeIdx == restCoinDto.getIdx()) { // 음수 였을 경우 이전에 조회한 used.idx 값과 같은 값이 조회 되었을 때
                throw new CustomException(CustomError.COIN_SUBTRACT_BY_ADMIN_ERROR); // 차감에 실패하였습니다.
            }
            // 조회한 used.idx 값 저장(무한 루프 방지용)
            beforeIdx = restCoinDto.getIdx();

            // 2. restCoin 이랑 차감할 금액 coin 차감하여 음수인지 검사(idx, restCoin 같이 들고 온다)
            int restCoin = restCoinDto.getRestCoin();
            coinDto.setIdx(restCoinDto.getIdx()); // 해당 row idx set

            // 3. 남은 코인 - 차감할 코인 = subResult
            subResult = restCoin - subCoin;

            // 양수면
            if (subResult > 0) {
                coinDto.setSubResultCoin(subResult);   // 차감된 결과값 set

                // subResult(차감 결과값)를 rest_coin 으로 update
                coinDao.updateCoinUsed(coinDto);
                subCoin = 0; // while 문 탈출 조건
            } else if (subResult < 0) { // 음수면

                // 조회된 rest_coin 만큼 차감할 코인에서 빼고 결과값 subCoin에 초기화
                subCoin = subCoin - restCoin; // 차감할 코인 초기화
                coinDto.setSubResultCoin(0);  // 남은 코인 0으로 set
                // 코인 or 무료 코인 update
                coinDao.updateCoinUsed(coinDto);
            } else if (subResult == 0) { // 0이면

                coinDto.setSubResultCoin(0);   // 차감된 결과값 set

                // subResult(차감 결과값)를 rest_coin 으로 update
                coinDao.updateCoinUsed(coinDto);
                subCoin = 0; // while 문 탈출 조건
            } // end of else if

            /** coin_used_log 테이블 등록(코인 & 보너스 코인) **/
            coinDao.insertCoinUsedLog(coinDto);

            init = false;

        } // end of while
    }

    /**
     * 마일리지 차감(mileage_used 테이블 update)
     *
     * @param coinDto : mileage(차감할 마일리지)
     */
    private void subtractMileage(CoinDto coinDto) {

        long beforeIdx = 0;
        int subResult = 0;                  // 조회한 rest_coin 에서 차감된 결과 값 누적 저장
        int subCoin = coinDto.getMileage(); // 차감할 마일리지
        CoinDto restCoinDto;                // 남은 코인 조회 용 dto

        // 차감할 금액이 0이 될 때 까지 반복
        while (subCoin > 0) {
            // 음수일 경우 이전 값 들어있으므로 초기화 후 진행
            coinDto.setSubResultCoin(null);

            // 1. 남은 마일리지 조회 & mileage_used.idx 조회
            restCoinDto = coinDao.getRestMileageFromMileageUsed(coinDto);

            // 무한 루프 방지
            if (beforeIdx == restCoinDto.getIdx()) { // 음수 였을 경우 이전에 조회한 used.idx 값과 같은 값이 조회 되었을 때
                throw new CustomException(CustomError.COIN_SUBTRACT_BY_ADMIN_ERROR);
            }
            // 조회한 used.idx 값 저장(무한 루프 방지용)
            beforeIdx = restCoinDto.getIdx();

            // 2. restCoin 이랑 차감할 금액 coin 차감하여 음수인지 검사(idx, restCoin 같이 들고 온다)
            int restMileage = restCoinDto.getRestMileage();
            coinDto.setIdx(restCoinDto.getIdx()); // 해당 row idx set

            subResult = restMileage - subCoin; // 남은 마일리지 - 차감할 마일리지 = subResult

            // 양수면
            if (subResult > 0) {
                coinDto.setSubResultCoin(subResult);   // 차감된 결과값 set

                // subResult(차감 결과값)를 rest_mileage 으로 update
                coinDao.updateMileageUsed(coinDto);
                subCoin = 0; // while 문 탈출 조건
            } else if (subResult < 0) { // 음수면

                // 조회된 rest_mileage 만큼 차감할 코인에서 빼고 결과값 subCoin에 초기화
                subCoin = subCoin - restMileage; // 차감할 마일리지 초기화
                coinDto.setSubResultCoin(0);     // 남은 마일리지 0으로 update

                coinDao.updateMileageUsed(coinDto);
            } else if (subResult == 0) { // 0이면

                coinDto.setSubResultCoin(0);   // 차감된 결과값 set

                // subResult(차감 결과값)를 rest_mileage 로 update
                coinDao.updateMileageUsed(coinDto);
                subCoin = 0; // while 문 탈출 조건
            } // end of else if

            /** mileage_used_log 테이블 등록 **/
            coinDao.insertMileageUsedLog(coinDto);

        } // end of while
    }

    /**
     * admin_coin 테이블 등록
     *
     * @param coinDto
     */
    private void insertAdminCoin(CoinDto coinDto) {
        // group_idx 최대값 조회
        long groupIdx = coinDao.getAdminCoinMaxGroupIdx();

        coinDto.setGroupIdx(groupIdx + 1); // 그룹 번호 set

        /** 각각 한번 씩 등록 **/
        // 1. 마일리지
        if (coinDto.getMileage() > 0) {
            coinDto.setCoinType(MILEAGE);
            coinDao.insertAdminCoin(coinDto);
        }
        // 2. 코인
        if (coinDto.getCoin() > 0) {
            coinDto.setCoinType(COIN);
            coinDao.insertAdminCoin(coinDto);
        }
        // 3. 보너스 코인
        if (coinDto.getCoinFree() > 0) {
            coinDto.setCoinType(COIN_FREE);
            coinDao.insertAdminCoin(coinDto);
        }
    }

    /**
     * 관리자 코인 지급 & 차감 로그(admin_coin_log)
     * 지급 개수가 0보다 클 경우만 각 항목에 대해 로그 남김
     *
     * @param coinDto : useGenerateKey -> paymentIdx 사용. 단 차감 시 paymentIdx 없으므로 paymentIdx 0 으로 setting
     */
    public void insertAdminCoinLog(CoinDto coinDto) {

        // 차감 시 paymentIdx 0
        if (coinDto.getPaymentIdx() == null) {
            coinDto.setPaymentIdx(0L);
        }

        // 1. 마일리지
        if (coinDto.getMileage() > 0) {
            coinDto.setCoinType(MILEAGE);
            coinDao.insertAdminCoinLog(coinDto);
        }
        // 2. 코인
        if (coinDto.getCoin() > 0) {
            coinDto.setCoinType(COIN);
            coinDao.insertAdminCoinLog(coinDto);
        }
        // 3. 보너스 코인
        if (coinDto.getCoinFree() > 0) {
            coinDto.setCoinType(COIN_FREE);
            coinDao.insertAdminCoinLog(coinDto);
        }
    }

    /*********************************************************************
     * Sub
     *********************************************************************/

    /**
     * 코인 & 무료 코인 & 마일리지 만료일 구하는 메서드
     *
     * @param coinType : COIN : 코인 만료일, COIN_FREE : 무료 코인 만료일, MILEAGE : 마일리지 만료일
     * @return String 타입 : 만료일
     */
    private String getExpireDate(String coinType) {

        Calendar cal = Calendar.getInstance();
        Date expireDate = null;
        // 구매코인
        if (coinType.equals(COIN)) {
            // 5년 후
            cal.add(Calendar.YEAR, +5);
            cal.add(Calendar.HOUR, +1);
            expireDate = cal.getTime();
        }
        // 마일리지 & 보너스 코인
        else if (coinType.equals(MILEAGE) || coinType.equals(COIN_FREE)) {
            // 5년 후
            cal.add(Calendar.MONTH, +1);
            cal.add(Calendar.HOUR, +1);
            expireDate = cal.getTime();
        }
        SimpleDateFormat formatDatetime = new SimpleDateFormat("yyyy-MM-dd HH:00:00");

        // 타임존 UTC 기준
        TimeZone utcZone = TimeZone.getTimeZone("UTC");
        formatDatetime.setTimeZone(utcZone);

        // 현재 날짜 구하기 (시스템 시계, 시스템 타임존)
        return formatDatetime.format(expireDate);
    }

    /**
     * 코인 리스트 문자 변환
     *
     * @param coinDtoList
     */
    private void coinStateText(List<CoinDto> coinDtoList) {

        for (CoinDto coinDto : coinDtoList) {
            stateText(coinDto);
        }
    }

    /**
     * 코인 리스트 문자 변환
     *
     * @param dto : CoinDto -> 상태값, 코인 유형
     */
    private void stateText(CoinDto dto) {
        // 상태값
        if (dto.getState() != null) {
            if (dto.getState() == 1) {//정상
                dto.setStateText(super.langMessage("lang.coin.state.normal")); // 정상
                dto.setStateBg("bg-success");
            } else if (dto.getState() == 0) {// 탈퇴
                dto.setStateText(super.langMessage("lang.coin.state.expire")); // 만료
                dto.setStateBg("bg-danger");
            }
        }
        // 유형
        if (dto.getType() != null) {
            if (dto.getType() == 1) {
                dto.setTypeText(super.langMessage("lang.coin.payCoin")); // 구매 코인
            } else if (dto.getType() == 2) {
                dto.setTypeText(super.langMessage("lang.coin.bonusCoin")); // 보너스 코인
            }
        }
    }

    /**
     * 마일리지 적립 내역 문자 변환
     *
     * @param mileageSaveDtoList
     */
    private void saveMileageStateText(List<MileageSaveDto> mileageSaveDtoList) {

        for (MileageSaveDto dto : mileageSaveDtoList) {
            saveMileageStateText(dto);
        }
    }

    /**
     * 마일리지 적립 내역 문자 변환
     *
     * @param dto
     */
    private void saveMileageStateText(MileageSaveDto dto) {
        // 상태값
        if (dto.getState() != null) {
            if (dto.getState() == 1) { // 정상
                dto.setStateText(super.langMessage("lang.coin.state.normal"));
                dto.setStateBg("bg-success");
            } else if (dto.getState() == 0) { // 만료
                dto.setStateText(super.langMessage("lang.coin.state.expire"));
                dto.setStateBg("bg-danger");
            }
        }
    }

    /**
     * 마일리지 사용 내역 문자 변환
     *
     * @param mileageUseDtoList
     */
    private void usedMileageStateText(List<MileageUseDto> mileageUseDtoList) {

        for (MileageUseDto dto : mileageUseDtoList) {
            usedMileageStateText(dto);
        }
    }

    /**
     * 마일리지 사용 내역 문자 변환
     *
     * @param dto
     */
    private void usedMileageStateText(MileageUseDto dto) {
        // 상태값
        if (dto.getState() != null) {
            if (dto.getState() == 1) { // 정상
                dto.setStateText(super.langMessage("lang.coin.state.normal"));
                dto.setStateBg("bg-success");
            } else if (dto.getState() == 0) { // 만료
                dto.setStateText(super.langMessage("lang.coin.state.expire"));
                dto.setStateBg("bg-danger");
            }
        }
    }

    /**
     * 관리자 지급 & 차감 문자 변환
     *
     * @param adminCoinList
     */
    private void adminStateText(List<CoinDto> adminCoinList) {
        for (CoinDto coinDto : adminCoinList) {
            adminStateText(coinDto);
        }
    }

    /**
     * 관리자 지급 & 차감 문자 변환
     *
     * @param dto
     */
    private void adminStateText(CoinDto dto) {
        // 상태값
        if (dto != null) {
            if (dto.getIsAdd().equals(PAY)) {// 지급
                dto.setStateText(super.langMessage("lang.coin.pay")); // 지급
                dto.setStateBg("bg-success");
            } else if (dto.getIsAdd().equals(SUBTRACT)) {// 차감
                dto.setStateText(super.langMessage("lang.coin.subtract")); // 차감
                dto.setStateBg("bg-danger");
            }
        }
    }

    /**
     * 코인 지급 유효성 검사
     *
     * @param coinDto : coin, mileage, coinFree, coinPrice, paymentType 유효성 검사
     */
    private void paymentCoinValidate(CoinDto coinDto) {

        if (coinDto.getPaymentType() == null || coinDto.getPaymentType().trim().isEmpty()) {
            throw new CustomException(CustomError.COIN_PAYMENT_TYPE_EMPTY);  // 결제 수단을 선택해주세요.
        }

        if (coinDto.getReason() == null || coinDto.getReason().isEmpty()) {
            throw new CustomException(CustomError.COIN_REASON_EMPTY);        // 사유를 입력하세요.
        }

        // 결제 지급
        if (!coinDto.getPaymentType().equals("normal")) {
            if (coinDto.getPaymentPrice() == null || coinDto.getPaymentPrice() < 1) {
                throw new CustomException(CustomError.COIN_PAYMENT_PRICE_EMPTY); // 결제 금액을 입력해주세요.
            }
        }

        // 코인&마일리지 null 일경우 0으로 치환
        if (coinDto.getCoin() == null) {
            coinDto.setCoin(0);
        }
        if (coinDto.getCoinFree() == null) {
            coinDto.setCoinFree(0);
        }
        if (coinDto.getMileage() == null) {
            coinDto.setMileage(0);
        }

        if (coinDto.getCoin() < 1 && coinDto.getCoinFree() < 1 && coinDto.getMileage() < 1) {
            throw new CustomException(CustomError.COIN_PRICE_EMPTY);         // 금액을 입력해주세요.
        }
    }

    /*********************************************************************
     * Validate - 유효성 검사
     *********************************************************************/

    /**
     * 코인 차감 유효성 검사
     *
     * @param coinDto : coin, mileage, coinFree, coinPrice, paymentType 유효성 검사
     */
    private void subtractCoinValidate(CoinDto coinDto) {

        // 코인&마일리지 null 일경우 0으로 치환
        if (coinDto.getCoin() == null) {
            coinDto.setCoin(0);
        }
        if (coinDto.getCoinFree() == null) {
            coinDto.setCoinFree(0);
        }
        if (coinDto.getMileage() == null) {
            coinDto.setMileage(0);
        }

        if (coinDto.getCoin() < 1 && coinDto.getCoinFree() < 1 && coinDto.getMileage() < 1) {
            throw new CustomException(CustomError.COIN_PRICE_EMPTY); // 금액을 입력해주세요.
        }
    }

    /**
     * 관리자 코인 지급 & 차감 리스트
     *
     * @param searchDto
     * @return
     */
    public List<CoinDto> getAdminCoinList(SearchDto searchDto) {

        List<CoinDto> coinAdminList = coinDaoSub.getAdminCoinList(searchDto);

        adminStateText(coinAdminList);

        return coinAdminList;
    }

}
