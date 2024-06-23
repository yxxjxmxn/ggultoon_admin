package com.architecture.admin.services.payment;

import com.architecture.admin.config.SessionConfig;
import com.architecture.admin.libraries.CurlLibrary;
import com.architecture.admin.libraries.PaginationLibray;
import com.architecture.admin.libraries.excel.ExcelData;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dao.notification.NotificationDao;
import com.architecture.admin.models.dao.payment.PaymentDao;
import com.architecture.admin.models.daosub.payment.PaymentDaoSub;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.member.MemberPurchaseDto;
import com.architecture.admin.models.dto.notification.NotificationDto;
import com.architecture.admin.models.dto.payment.PaymentDto;
import com.architecture.admin.models.dto.payment.PaymentMethodDto;
import com.architecture.admin.models.dto.payment.PaymentProductDto;
import com.architecture.admin.services.BaseService;
import com.architecture.admin.services.log.AdminActionLogService;
import com.architecture.admin.services.member.GradeService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.text.SimpleDateFormat;
import java.util.*;
import static com.architecture.admin.libraries.utils.NotificationUtils.*;

@RequiredArgsConstructor
@Service
@Transactional
public class PaymentService extends BaseService {

    private final PaymentDao paymentDao;
    private final PaymentDaoSub paymentDaoSub;
    private final NotificationDao notificationDao;
    private final ExcelData excelData;
    private final GradeService gradeService;
    private final AdminActionLogService adminActionLogService;// 관리자 action log 처리용
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**************************************************************************************
     * 결제 내역
     **************************************************************************************/

    /**
     * 결제 내역 전체 카운트
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public int getPaymentTotalCnt(SearchDto searchDto) {

        // 목록 전체 count
        int totalCnt = paymentDaoSub.getPaymentTotalCnt(searchDto);

        // 보여줄 데이터 개수 set
        Integer searchCount = searchDto.getSearchCount();

        if (searchCount != null) {
            searchDto.setRecordSize(searchCount);
        } else {
            searchDto.setRecordSize(searchDto.getLimit());
        }

        // paging
        PaginationLibray pagination = new PaginationLibray(totalCnt, searchDto);
        searchDto.setPagination(pagination);

        return totalCnt;
    }

    /**
     * 결제 내역 전체 조회
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public List<PaymentDto> getPaymentList(SearchDto searchDto) {

        // 검색어 공백제거
        if (searchDto.getSearchWord() != null && !searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // 내보낼 데이터 set
        List<PaymentDto> paymentList = null;

        // 결제 내역 개수 조회
        int totalCount = paymentDaoSub.getPaymentTotalCnt(searchDto);

        // 결제 내역이 있는 경우
        if (totalCount > 0) {

            // recordSize set
            Integer recordSize = searchDto.getSearchCount();
            if (recordSize != null && recordSize > 0) {
                searchDto.setRecordSize(recordSize);
            }

            // paging
            PaginationLibray pagination = new PaginationLibray(totalCount, searchDto);
            searchDto.setPagination(pagination);

            // 결제 내역 리스트 조회
            paymentList = paymentDaoSub.getPaymentList(searchDto);

            // 사용한 코인 & 보너스 & 마일리지 세팅
            setUsed(paymentList);

            // 문자 변환
            paymentStateText(paymentList);

            // 결제 취소 버튼 활성화 여부 세팅
            setCancelButton(paymentList);
        }

        return paymentList;
    }

    /**
     * 결제 내역 엑셀 다운로드
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public Map<String, Object> excelPayment(SearchDto searchDto) {

        // 검색어 공백제거
        if (!searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // 내보낼 데이터 set
        List<PaymentDto> paymentList = null;

        // 결제 내역 개수 조회
        int totalCount = paymentDaoSub.getPaymentTotalCnt(searchDto);

        // 결제 내역이 있는 경우
        if (totalCount > 0) {

            // 결제 내역 리스트 조회
            paymentList = paymentDaoSub.getPaymentList(searchDto);

            // 사용한 코인 & 보너스 & 마일리지 세팅
            setUsed(paymentList);

            // 문자 변환
            paymentStateText(paymentList);
        }
        // 엑셀 데이터 생성
        return excelData.createExcelData(paymentList, PaymentDto.class);
    }

    /**
     * 결제 상세
     * @param paymentIdx (payment.idx)
     * @return
     */
    @Transactional(readOnly = true)
    public PaymentDto getViewPayment(Long paymentIdx) {

        // payment.idx
        if (paymentIdx == null || paymentIdx < 1L) {
            throw new CustomException(CustomError.PAYMENT_IDX_ERROR);
        }

        // 결제 상세
        PaymentDto viewPayment = paymentDaoSub.getViewPayment(paymentIdx);

        if (viewPayment != null) {

            // 사용한 코인+보너스+마일리지 세팅
            if (viewPayment.getCoin() != null && viewPayment.getRestCoin() != null) {
                viewPayment.setUsedCoin(viewPayment.getCoin() - viewPayment.getRestCoin());
            }else {
                viewPayment.setUsedCoin(0);
            }
            if (viewPayment.getBonus() != null && viewPayment.getRestBonus() != null) {
                viewPayment.setUsedBonus(viewPayment.getBonus() - viewPayment.getRestBonus());
            }else {
                viewPayment.setUsedBonus(0);
            }
            if (viewPayment.getMileage() != null && viewPayment.getRestMileage() != null) {
                viewPayment.setUsedMileage(viewPayment.getMileage() - viewPayment.getRestMileage());
            }else {
                viewPayment.setUsedMileage(0);
            }

            // 문자 변환
            paymentStateText(viewPayment);
            payTypeText(viewPayment);
        }

        return viewPayment;
    }

    /**
     * 결제 취소
     * @param paymentIdx (취소할 결제 정보의 payment.idx)
     * @return
     */
    @Transactional
    public void deleteDatePayment(Long paymentIdx) {

        // payment.idx
        if (paymentIdx == null || paymentIdx < 1L) {
            throw new CustomException(CustomError.PAYMENT_IDX_ERROR); // 결제 번호가 없습니다.
        }

        // 취소할 결제 상세
        PaymentDto deletePayment = paymentDaoSub.getViewPayment(paymentIdx);
        // pushAlarm(deletePayment.toString(),"LJH");

        // 결제 취소 가능 여부 검사
        IsCancelValidation(deletePayment);

        // 로그인한 관리자 아이디 정보
        String adminID = String.valueOf(session.getAttribute(SessionConfig.LOGIN_ID));

        /** payment **/
        // 결제 취소일 세팅
        deletePayment.setModdate(dateLibrary.getDatetime());
        paymentDao.updatePayment(deletePayment);

        /** payment_info **/
        paymentDao.updatePaymentInfo(deletePayment);

        /** payment_log **/
        deletePayment.setPaymentType(0);
        paymentDao.insertPaymentLog(deletePayment);

        /** member_coin_save **/
        paymentDao.updateCoinSave(deletePayment);

        /** member_coin_used **/
        if (deletePayment.getCoin() > 0) {
            deletePayment.setType(1);
        }
        if (deletePayment.getBonus() > 0) {
            deletePayment.setType(2);
        }
        paymentDao.updateCoinUsed(deletePayment);

        /** member_coin_used_log **/
        if (deletePayment.getCoin() > 0) {
            deletePayment.setType(1);

            // dto set
            PaymentDto coinUsedLog = deletePayment;

            // 결제 취소 표시 세팅
            coinUsedLog.setTitle(adminID + " 결제 취소 차감");

            // 등록일 세팅
            coinUsedLog.setRegdate(dateLibrary.getDatetime());

            // 로그 등록
            paymentDao.insertCoinUsedLog(coinUsedLog);
        }
        if (deletePayment.getBonus() > 0) {
            deletePayment.setType(2);

            // dto set
            PaymentDto coinUsedLog = deletePayment;

            // 결제 취소 표시 세팅
            coinUsedLog.setTitle(adminID + " 결제 취소 차감");

            // 등록일 세팅
            coinUsedLog.setRegdate(dateLibrary.getDatetime());

            // 로그 등록
            paymentDao.insertCoinUsedLog(coinUsedLog);
        }

        /** member_mileage_save **/
        paymentDao.updateMileageSave(deletePayment);

        /** member_mileage_used **/
        paymentDao.updateMileageUsed(deletePayment);

        /** member_mileage_used_log **/

        // dto set
        PaymentDto mileageUsedLog = deletePayment;

        // 결제 취소 표시 세팅
        mileageUsedLog.setTitle(adminID + " 결제 취소 차감");

        // 등록일 세팅
        mileageUsedLog.setRegdate(dateLibrary.getDatetime());

        // 로그 등록
        paymentDao.insertMileageUsedLog(mileageUsedLog);

        /** member_coin **/
        // 회원이 가진 총 코인, 보너스, 마일리지 개수 조회
        PaymentDto memberCoin = paymentDaoSub.getMemberCoin(deletePayment);

        // 결제 시 지급받은 코인이 있는 경우
        if (deletePayment.getCoin() > 0) {
            // 회원이 현재 갖고 있는 코인에서 결제 시 지급받았던 코인 차감
            memberCoin.setCoin(memberCoin.getCoin() - deletePayment.getCoin());
        }

        // 결제 시 지급받은 보너스가 있는 경우
        if (deletePayment.getBonus() > 0) {

            // 회원이 현재 갖고 있는 보너스에서 결제 시 지급받았던 보너스 차감
            memberCoin.setBonus(memberCoin.getBonus() - deletePayment.getBonus());
        }

        // 결제 시 지급받은 마일리지가 있는 경우
        if (deletePayment.getMileage() > 0) {

            // 회원이 현재 갖고 있는 마일리지에서 결제 시 지급받았던 마일리지 차감
            memberCoin.setMileage(memberCoin.getMileage() - deletePayment.getMileage());
        }
        // 차감한 개수로 업데이트
        paymentDao.updateMemberCoin(memberCoin);

        /** member_notification **/
        // dto set
        NotificationDto dto = NotificationDto.builder()
                            .memberIdx(deletePayment.getMemberIdx())
                            .category(CANCEL) // 알림 카테고리
                            .type("payment") // 알림 보낼 테이블명
                            .typeIdx(deletePayment.getIdx()) // 알림 보낼 테이블 idx
                            .state(1)
                            .regdate(dateLibrary.getDatetime())
                            .build();

        // 결제 취소 완료 알림 전송
        notificationDao.insertPaymentCancelAlarm(dto);

        // 회원 등급 수정
        gradeService.modifyGrade(deletePayment.getMemberIdx());

        // 관리자 action log
        adminActionLogService.regist(deletePayment, Thread.currentThread().getStackTrace());

        // 취소내역 원스토어 전송
        onestoreCancel(deletePayment);
    }

    /**************************************************************************************
     * 결제 수단
     **************************************************************************************/

    /**
     * 결제 수단 리스트 엑셀 다운로드
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public Map<String, Object> excelPaymentMethod(SearchDto searchDto) {

        // 검색어 공백제거
        if (!searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // send data set
        List<PaymentMethodDto> paymentMethodList = null;

        // 결제 수단 개수 카운트
        int totalCnt = paymentDaoSub.getPaymentMethodTotalCnt(searchDto);

        // 결제 수단이 있는 경우
        if (totalCnt > 0) {

            // 결제 수단 리스트 조회
            paymentMethodList = paymentDaoSub.getPaymentMethodList(searchDto);

            // 결제 수단 사용 상태값 & 결제방식 문자변환
            paymentMethodStateText(paymentMethodList);
        }
        // 엑셀 데이터 생성
        return excelData.createExcelData(paymentMethodList, PaymentMethodDto.class);
    }

    /**
     * 결제 수단 리스트 조회
     *
     * @param searchDto
     */
    @Transactional(readOnly = true)
    public JSONObject getPaymentMethodList(SearchDto searchDto) {

        // 검색어 공백제거
        if (!searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // 내보낼 데이터 set
        JSONObject joData = new JSONObject();
        List<PaymentMethodDto> paymentMethodList = null;

        // 결제 수단 개수 카운트
        int totalCnt = paymentDaoSub.getPaymentMethodTotalCnt(searchDto);

        // 결제 수단이 있는 경우
        if (totalCnt > 0) {

            // recordSize set
            Integer recordSize = searchDto.getSearchCount();
            if (recordSize != null && recordSize > 0) {
                searchDto.setRecordSize(recordSize);
            }

            // paging
            PaginationLibray pagination = new PaginationLibray(totalCnt, searchDto);
            searchDto.setPagination(pagination);

            // 결제 수단 리스트 조회
            paymentMethodList = paymentDaoSub.getPaymentMethodList(searchDto);

            // 결제 수단 사용 상태값 & 결제방식 문자변환
            paymentMethodStateText(paymentMethodList);

            // paging 담기
            joData.put("params", new JSONObject(searchDto));
        }

        // list 담기
        joData.put("list", paymentMethodList);

        return joData;
    }

    /**
     * 결제 수단 상세
     *
     * @param idx
     */
    @Transactional(readOnly = true)
    public JSONObject getViewPaymentMethod(Integer idx) {

        // 결제 수단 idx 유효성 검사
        isPaymentMethodIdxValidate(idx);

        // 결제 수단 상세 조회
        PaymentMethodDto viewPaymentMethod = paymentDaoSub.getViewPaymentMethod(idx);

        // 결제 수단 상세 상태값 & 결제방식 문자변환
        paymentMethodStateText(viewPaymentMethod);

        // 내보낼 데이터 set
        JSONObject joData = new JSONObject(viewPaymentMethod);

        return joData;
    }

    /**
     * 결제 수단 등록
     *
     * @param paymentMethodDto
     */
    @Transactional
    public void registerPaymentMethod(PaymentMethodDto paymentMethodDto) {

        // 등록할 데이터 유효성 검사
        isInputMethodDataValidate(paymentMethodDto);

        // 결제 수단 등록
        paymentMethodDto.setRegdate(dateLibrary.getDatetime()); // 등록일 set
        int result = paymentDao.registerPaymentMethod(paymentMethodDto);

        // 등록 실패 시
        if (result < 1) {
            throw new CustomException(CustomError.PAYMENT_METHOD_REGISTER_ERROR); // 결제 수단 등록을 실패하였습니다.
        }

        // 관리자 action log
        adminActionLogService.regist(paymentMethodDto, Thread.currentThread().getStackTrace());
    }

    /**
     * 결제 수단 삭제
     *
     * @param idx
     */
    @Transactional
    public void deletePaymentMethod(Integer idx) {

        // 결제 수단 idx 유효성 검사
        isPaymentMethodIdxValidate(idx);

        // 결제 수단 삭제
        int result = paymentDao.deletePaymentMethod(idx);

        // 삭제 실패 시
        if (result < 1) {
            throw new CustomException(CustomError.PAYMENT_METHOD_DELETE_ERROR); // 결제 수단 삭제를 실패하였습니다.
        }

        // 관리자 action log
        adminActionLogService.regist(idx, Thread.currentThread().getStackTrace());
    }

    /**
     * 결제 수단 수정
     *
     * @param paymentMethodDto
     */
    @Transactional
    public void modifyPaymentMethod(PaymentMethodDto paymentMethodDto) {

        // 수정할 데이터 유효성 검사
        isInputMethodDataValidate(paymentMethodDto);

        // 결제 수단 수정
        paymentMethodDto.setRegdate(dateLibrary.getDatetime()); // 수정일 set
        int result = paymentDao.modifyPaymentMethod(paymentMethodDto);

        // 수정 실패 시
        if (result < 1) {
            throw new CustomException(CustomError.PAYMENT_METHOD_MODIFY_ERROR); // 결제 수단 수정을 실패하였습니다.
        }

        // 관리자 action log
        adminActionLogService.regist(paymentMethodDto, Thread.currentThread().getStackTrace());
    }

    /**************************************************************************************
     * 결제 상품
     **************************************************************************************/

    /**
     * 결제 상품 리스트 엑셀 다운로드
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public Map<String, Object> excelPaymentProduct(SearchDto searchDto) {

        // 검색어 공백제거
        if (!searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // send data set
        List<PaymentProductDto> paymentProductList = null;

        // 결제 상품 개수 카운트
        int totalCnt = paymentDaoSub.getPaymentProductTotalCnt(searchDto);

        // 결제 상품이 있는 경우
        if (totalCnt > 0) {

            // 결제 상품 리스트 조회
            paymentProductList = paymentDaoSub.getPaymentProductList(searchDto);

            // 결제 상품 상태값 문자변환
            paymentProductStateText(paymentProductList);
        }
        // 엑셀 데이터 생성
        return excelData.createExcelData(paymentProductList, PaymentProductDto.class);
    }

    /**
     * 결제 상품 리스트 조회
     *
     * @param searchDto
     */
    @Transactional(readOnly = true)
    public JSONObject getPaymentProductList(SearchDto searchDto) {

        // 검색어 공백제거
        if (!searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // 내보낼 데이터 set
        JSONObject joData = new JSONObject();
        List<PaymentProductDto> paymentProductList = null;

        // 결제 상품 개수 카운트
        int totalCnt = paymentDaoSub.getPaymentProductTotalCnt(searchDto);

        // 결제 상품이 있는 경우
        if (totalCnt > 0) {

            // recordSize set
            Integer recordSize = searchDto.getSearchCount();
            if (recordSize != null && recordSize > 0) {
                searchDto.setRecordSize(recordSize);
            }

            // paging
            PaginationLibray pagination = new PaginationLibray(totalCnt, searchDto);
            searchDto.setPagination(pagination);

            // 결제 상품 리스트 조회
            paymentProductList = paymentDaoSub.getPaymentProductList(searchDto);

            // 결제 상품 상태값 문자변환
            paymentProductStateText(paymentProductList);

            // 결제 상품 코인 합계 set
            paymentProductSetTotalCoin(paymentProductList);

            // paging 담기
            joData.put("params", new JSONObject(searchDto));
        }

        // list 담기
        joData.put("list", paymentProductList);

        return joData;
    }

    /**
     * 결제 상품 상세
     *
     * @param idx
     */
    @Transactional(readOnly = true)
    public JSONObject getViewPaymentProduct(Integer idx) {

        // 결제 상품 idx 유효성 검사
        isPaymentProductIdxValidate(idx);

        // 결제 상품 상세 조회
        PaymentProductDto viewPaymentProduct = paymentDaoSub.getViewPaymentProduct(idx);

        // 결제 상품 상세 상태값 문자변환
        paymentProductStateText(viewPaymentProduct);

        // 결제 상품 코인 합계 set
        paymentProductSetTotalCoin(viewPaymentProduct);

        // 내보낼 데이터 set
        JSONObject joData = new JSONObject(viewPaymentProduct);

        return joData;
    }

    /**
     * 결제 상품 등록
     *
     * @param paymentProductDto
     */
    @Transactional
    public void registerPaymentProduct(PaymentProductDto paymentProductDto) {

        // 등록할 데이터 유효성 검사
        isInputProductDataValidate(paymentProductDto);

        // 결제 상품 등록
        paymentProductDto.setRegdate(dateLibrary.getDatetime()); // 등록일 set
        int result = paymentDao.registerPaymentProduct(paymentProductDto);

        // 등록 실패 시
        if (result < 1) {
            throw new CustomException(CustomError.PAYMENT_PRODUCT_REGISTER_ERROR); // 결제 상품 등록을 실패하였습니다.
        }

        // 관리자 action log
        adminActionLogService.regist(paymentProductDto, Thread.currentThread().getStackTrace());
    }


    /**
     * 결제 상품 삭제
     *
     * @param idx
     */
    @Transactional
    public void deletePaymentProduct(Integer idx) {

        // 결제 상품 idx 유효성 검사
        isPaymentProductIdxValidate(idx);

        // 결제 상품 삭제
        int result = paymentDao.deletePaymentProduct(idx);

        // 삭제 실패 시
        if (result < 1) {
            throw new CustomException(CustomError.PAYMENT_PRODUCT_DELETE_ERROR); // 결제 상품 삭제를 실패하였습니다.
        }

        // 관리자 action log
        adminActionLogService.regist(idx, Thread.currentThread().getStackTrace());
    }

    /**
     * 결제 상품 수정
     *
     * @param paymentProductDto
     */
    @Transactional
    public void modifyPaymentProduct(PaymentProductDto paymentProductDto) {

        // 수정할 데이터 유효성 검사
        isInputProductDataValidate(paymentProductDto);

        // 결제 상품 수정
        paymentProductDto.setRegdate(dateLibrary.getDatetime()); // 수정일 set
        int result = paymentDao.modifyPaymentProduct(paymentProductDto);

        // 수정 실패 시
        if (result < 1) {
            throw new CustomException(CustomError.PAYMENT_PRODUCT_MODIFY_ERROR); // 결제 상품 수정을 실패하였습니다.
        }

        // 관리자 action log
        adminActionLogService.regist(paymentProductDto, Thread.currentThread().getStackTrace());
    }

    /**************************************************************************************
     * 회원 구매 내역
     **************************************************************************************/

    /**
     * 전체 회원 회차 구매내역 개수 조회(결제 사용내역)
     *
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public int getPurchaseTotalCnt(SearchDto searchDto) {

        // 결제 사용내역 개수 조회
        int totalCnt = paymentDaoSub.getPurchaseTotalCnt(searchDto);

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
     * 전체 회원 회차구매 리스트(결제 사용내역 리스트)
     *
     * @param searchDto : id, nick , 날짜 검색
     * @return
     */
    @Transactional(readOnly = true)
    public List<MemberPurchaseDto> getPurchaseList(SearchDto searchDto) {

        List<MemberPurchaseDto> purchaseList = paymentDaoSub.getPurchaseList(searchDto);

        // 문자 변환
        purchaseStateText(purchaseList);

        return purchaseList;
    }

    /**
     * 회원 회차 구매내역 개수
     *
     * @param memberIdx
     * @return
     */
    @Transactional(readOnly = true)
    public int getMemberPurchaseTotalCnt(Long memberIdx) {

        return paymentDaoSub.getMemberPurchaseTotalCnt(memberIdx);
    }

    /**
     * 회원 회차 구매내역 리스트(회원 상세보기시 사용)
     *
     * @param memberIdx : 회원 idx
     * @return
     */
    @Transactional(readOnly = true)
    public List<MemberPurchaseDto> getMemberPurchaseInfo(Long memberIdx) {
        // 회원 회차 구매 정보
        List<MemberPurchaseDto> purchaseList = paymentDaoSub.getMemberPurchaseInfo(memberIdx);

        // 문자 변환
        purchaseStateText(purchaseList);

        return purchaseList;
    }

    /**
     * 구매 회차 엑셀 다운로드
     *
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public Map<String, Object> excelPurchase(SearchDto searchDto) {

        // 구매 회차 리스트 조회
        List<MemberPurchaseDto> purchaseList = getPurchaseList(searchDto);

        // 엑셀 데이터 변환
        return excelData.createExcelData(purchaseList, MemberPurchaseDto.class);
    }

    /*************************************************************
     * SUB
     *************************************************************/

    /**
     * 결제 취소 버튼 활성화 여부 세팅
     * @param listPayment : 결제 취소 버튼을 세팅할 결제 내역
     */
    @SneakyThrows
    public void setCancelButton(List<PaymentDto> listPayment) {

        // 현재 날짜 구하기
        Date now = new Date();



        for (PaymentDto dto : listPayment) {

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

    /**
     * 코인, 보너스, 마일리지 사용 개수 계산
     * 지급 개수 - 잔여 개수 = 사용 개수
     * @param list (결제 내역 리스트)
     */
    private void setUsed(List<PaymentDto> list) {

        for (PaymentDto l : list) {

            // 코인 사용 개수 세팅
            if (l.getCoin() != null && l.getRestCoin() != null) {
                l.setUsedCoin(l.getCoin() - l.getRestCoin());
            }else {
                l.setUsedCoin(0);
            }

            // 보너스 코인 사용 개수 세팅
            if (l.getBonus() != null && l.getRestBonus() != null) {
                l.setUsedBonus(l.getBonus() - l.getRestBonus());
            }else {
                l.setUsedBonus(0);
            }

            // 마일리지 사용 개수 세팅
            if (l.getMileage() != null && l.getRestMileage() != null) {
                l.setUsedMileage(l.getMileage() - l.getRestMileage());
            }else {
                l.setUsedMileage(0);
            }
        }
    }

    /**
     * 결제 취소 가능 여부 검사
     * (1) 결제 상태 판단
     * (2) 코인, 보너스, 마일리지 지급 개수와 잔여 개수 비교
     * (3) 결제 취소 가능 기간 판단
     * (4) 코인, 보너스, 마일리지 만료일 판단
     * @param deletePayment (취소할 결제 정보)
     */
    @SneakyThrows
    private void IsCancelValidation(PaymentDto deletePayment) {

        if (deletePayment != null) {

            /** 결제 상태 판단 **/
            if(deletePayment.getState() == 0) { // 결제 상태값이 0일 경우
                throw new CustomException(CustomError.PAYMENT_STATE_ERROR); // 이미 취소된 결제건입니다.
            }

            /** 결제 취소 가능 기간 판단 **/
            // 현재 날짜 구하기
            Date now = new Date();

            // 결제일 String -> Date 변환
            Date registerDate = sdf.parse(deletePayment.getRegdate());

            // 결제 취소 가능 기간 만료일 구하기
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(registerDate);
            calendar.add(Calendar.DATE, 7); // 결제일로부터 7일 뒤
            Date expireDate = calendar.getTime();  // 결제 취소 가능 기간 만료일

            if(expireDate.compareTo(now) < 0) { // 결제 취소 시점이 결제일에서 7일이 지난 이후일 경우
                throw new CustomException(CustomError.PAYMENT_DATE_ERROR); // 결제 취소 가능 기간이 아닙니다.
            }

            /** 코인의 결제 취소 조건 충족 여부 체크 **/
            // 결제 시 지급받은 코인이 있는 경우
            if (deletePayment.getCoin() > 0) {

                // 지급받은 코인이 사용 불가 상태일 경우
                if (deletePayment.getCoinState() != 1) {
                    throw new CustomException(CustomError.PAYMENT_COIN_STATE_ERROR); // 지급받은 코인이 현재 사용 불가 상태입니다.
                }
                // 코인의 지급 개수와 잔여 개수가 일치하지 않을 경우
                if(!deletePayment.getCoin().equals(deletePayment.getRestCoin())) {
                    throw new CustomException(CustomError.PAYMENT_COIN_COUNT_ERROR); // 코인의 지급 개수와 잔여 개수가 일치하지 않습니다.
                }
                // 코인 만료일이 지났을 경우
                Date coinExp = sdf.parse(deletePayment.getCoinExp());
                if (coinExp.compareTo(now) < 0) {
                    throw new CustomException(CustomError.PAYMENT_COIN_EXP_ERROR); // 지급받은 코인의 만료일이 지났습니다.
                }
            }

            /** 보너스 코인의 결제 취소 조건 충족 여부 체크 **/
            // 결제 시 지급받은 보너스 코인이 있는 경우
            if (deletePayment.getBonus() > 0) {

                // 지급받은 보너스가 사용 불가 상태일 경우
                if (deletePayment.getBonusState() != 1) {
                    throw new CustomException(CustomError.PAYMENT_BONUS_STATE_ERROR); // 지급받은 보너스 코인이 현재 사용 불가 상태입니다.
                }
                // 보너스 코인의 지급 개수와 잔여 개수가 일치하지 않을 경우
                if(!deletePayment.getBonus().equals(deletePayment.getRestBonus())) {
                    throw new CustomException(CustomError.PAYMENT_BONUS_COUNT_ERROR); // 보너스 코인의 지급 개수와 잔여 개수가 일치하지 않습니다.
                }
                // 보너스 코인 만료일이 지났을 경우
                Date bonusExp = sdf.parse(deletePayment.getBonusExp());
                if (bonusExp.compareTo(now) < 0) {
                    throw new CustomException(CustomError.PAYMENT_BONUS_EXP_ERROR); // 지급받은 보너스 코인의 만료일이 지났습니다.
                }

            }

            /** 마일리지의 결제 취소 조건 충족 여부 체크 **/
            // 결제 시 지급받은 마일리지가 있는 경우
            if (deletePayment.getMileage() > 0) {

                // 지급받은 마일리지가 사용 불가 상태일 경우
                if (deletePayment.getMileageState() != 1) {
                    throw new CustomException(CustomError.PAYMENT_MILEAGE_STATE_ERROR); // 지급받은 마일리지가 현재 사용 불가 상태입니다.
                }
                // 마일리지의 지급 개수와 잔여 개수가 일치하지 않을 경우
                if(!deletePayment.getMileage().equals(deletePayment.getRestMileage())) {
                    throw new CustomException(CustomError.PAYMENT_MILEAGE_COUNT_ERROR); // 마일리지의 지급 개수와 잔여 개수가 일치하지 않습니다.
                }
                // 마일리지 만료일이 지났을 경우
                Date mileageExp = sdf.parse(deletePayment.getMileageExp());
                if (mileageExp.compareTo(now) < 0) {
                    throw new CustomException(CustomError.PAYMENT_MILEAGE_EXP_ERROR); // 지급받은 마일리지의 만료일이 지났습니다.
                }
            }
        }
    }

    /**
     * 결제 상품 리스트 & 결제 상품 상세
     * 코인 합계 계산 및 세팅 list
     *
     * @param paymentProductList
     */
    private void paymentProductSetTotalCoin(List<PaymentProductDto> paymentProductList) {

        for (PaymentProductDto paymentProductDto : paymentProductList) {
            paymentProductSetTotalCoin(paymentProductDto);
        }
    }

    /**
     * 결제 상품 리스트 & 결제 상품 상세
     * 코인 합계 계산 및 세팅 dto
     *
     * @param paymentProductDto
     */
    private void paymentProductSetTotalCoin(PaymentProductDto paymentProductDto) {

        Integer coinTotalCnt = paymentProductDto.getCoin() + paymentProductDto.getCoinFree() + paymentProductDto.getCoinFree2();
        paymentProductDto.setCoinTotalCnt(coinTotalCnt);
    }

    /*************************************************************
     * 문자변환
     *************************************************************/

    /**
     * 결제 내역
     * 문자변환 list
     */
    private void paymentStateText(List<PaymentDto> list) {
        for (PaymentDto dto : list) {
            paymentStateText(dto);
            payTypeText(dto);
        }
    }

    /**
     * 결제 내역
     * 문자변환 dto
     */
    private void paymentStateText(PaymentDto dto) {

        if (dto.getState() != null) {
            if (dto.getState() == 0) {
                // 취소
                dto.setStateText(super.langMessage("lang.payment.status.cancel"));
                dto.setStateBg("badge-danger");
            } else if (dto.getState() == 1) {
                // 정상
                dto.setStateText(super.langMessage("lang.payment.status.normal"));
                dto.setStateBg("badge-success");
            }
        }
        if(dto.getFirst() != null) {
            if (dto.getFirst() == 0) {
                // 일반 결제
                dto.setFirstText(super.langMessage("lang.payment.payType.normal"));
                dto.setFirstBg("badge-light");
            } else if (dto.getFirst() == 1) {
                // 첫 결제
                dto.setFirstText(super.langMessage("lang.payment.payType.first"));
                dto.setFirstBg("badge-info");
            }
        }
    }

    /**
     * 결제 수단 리스트
     * 문자변환 list
     */
    private void paymentMethodStateText(List<PaymentMethodDto> list) {
        for (PaymentMethodDto dto : list) {
            paymentMethodStateText(dto);
        }
    }

    /**
     * 결제 수단 리스트
     * 상태값 & 결제방식 문자변환 dto
     */
    private void paymentMethodStateText(PaymentMethodDto dto)  {
        if (dto.getState() != null) {
            if (dto.getState() == 2) {
                // 미사용
                dto.setStateText(super.langMessage("lang.payment.method.status.unuse"));
                dto.setStateBg("badge-danger");
            } else if (dto.getState() == 1) {
                // 사용
                dto.setStateText(super.langMessage("lang.payment.method.status.use"));
                dto.setStateBg("badge-success");
            }
        }
        if (dto.getAutoPay() != null) {
            if (dto.getAutoPay() == 0) {
                // 일반결제
                dto.setAutoPayText(super.langMessage("lang.payment.method.normal"));
                dto.setAutoPayBg("badge-warning");
            } else if (dto.getAutoPay() == 1) {
                // 자동결제
                dto.setAutoPayText(super.langMessage("lang.payment.method.auto"));
                dto.setAutoPayBg("badge-secondary");
            }
        }
    }

    /**
     * 결제 상품 리스트
     * 문자변환 list
     */
    private void paymentProductStateText(List<PaymentProductDto> list) {
        for (PaymentProductDto dto : list) {
            paymentProductStateText(dto);
        }
    }

    /**
     * 결제 상품 리스트
     * 상태값 문자변환 dto
     * @param dto
     */
    private void paymentProductStateText(PaymentProductDto dto)  {

        // 사용 상태
        if (dto.getState() != null) {
            if (dto.getState() == 2) {
                // 미사용
                dto.setStateText(super.langMessage("lang.payment.product.state.unuse"));
                dto.setStateBg("badge-danger");
            } else if (dto.getState() == 1) {
                // 사용
                dto.setStateText(super.langMessage("lang.payment.product.state.use"));
                dto.setStateBg("badge-success");
            }
        }

        // 결제 구분(첫결제, 재결제, 장기미결제)
        if (dto.getType() != null) {
            if (dto.getType() == 1) {
                // 첫결제
                dto.setTypeText(super.langMessage("lang.payment.product.type.first"));
            } else if (dto.getType() == 2) {
                // 재결제
                dto.setTypeText(super.langMessage("lang.payment.product.type.continue"));
            } else if (dto.getType() == 3) {
                // 장기미결제
                dto.setTypeText(super.langMessage("lang.payment.product.type.long"));
            }
        }
    }

    /**
     * 회원 시청내역 문자 변환
     *
     * @param memberPurchaseDtoList
     */
    private void purchaseStateText(List<MemberPurchaseDto> memberPurchaseDtoList) {

        for (MemberPurchaseDto purchaseDto : memberPurchaseDtoList) {
            purchaseStateText(purchaseDto);
        }
    }

    /**
     * 회원 시청내역 문자 변환
     *
     * @param dto : episodeNumber : 0이면 전체 회차 구매, 0 이상이면 n 회차 구매
     */
    private void purchaseStateText(MemberPurchaseDto dto) {
        // 회차
        if (dto.getEpisodeNumber() != null) {
            Integer episodeNumber = dto.getEpisodeNumber();
            // 전체 회차 번호
            if (episodeNumber > 0) {
                dto.setEpisodeNumberText(super.langMessage("lang.contents.episode.number", new Integer[]{episodeNumber})); // n 회차
            }
        }
        // 전체 회차 구매 여부
        if (dto.getBuyAllIdx() > 0) {
            dto.setTitle(dto.getTitle() + "(" +super.langMessage("lang.contents.episode.all") + ")"); // 전체 회차
        }
        // 시청 방식
        if (dto.getType() != null) {
            // 전체 회차 구매
            if (dto.getType() == 1) {
                dto.setTypeText(super.langMessage("lang.contents.rent"));        // 대여
                dto.setTypeBg("bg-warning");
            } else if (dto.getType() == 2) {
                dto.setTypeText(super.langMessage("lang.contents.possession"));  // 소장
                dto.setTypeBg("bg-primary");
            }
        }
    }


    /*************************************************************
     * Validation
     *************************************************************/
    
    /**
     * 결제 수단 상세
     * 선택한 결제 수단 idx 유효성 검사
     *
     * @param idx
     */
    private void isPaymentMethodIdxValidate(Integer idx) {

        // 결제 수단 idx가 없는 경우
        if (idx == null || idx < 1) {
            throw new CustomException(CustomError.PAYMENT_METHOD_IDX_EMPTY); // 요청하신 결제 수단 상세 정보를 찾을 수 없습니다.
        }
        
        // 결제 수단 idx가 유효하지 않은 경우
        int totalCnt = paymentDaoSub.getPaymentMethodIdxCnt(idx);
        if (totalCnt < 1) {
            throw new CustomException(CustomError.PAYMENT_METHOD_IDX_ERROR); // 요청하신 결제 수단 상세 정보를 찾을 수 없습니다.
        }
    }

    /**
     * 결제 수단 등록 & 수정
     * 입력 데이터 유효성 검사
     *
     * @param paymentMethodDto
     */
    private void isInputMethodDataValidate(PaymentMethodDto paymentMethodDto) {

        // 결제 수단 사용 상태
        if (paymentMethodDto.getState() == null || paymentMethodDto.getState() < 0 || paymentMethodDto.getState() > 2) {
            throw new CustomException(CustomError.PAYMENT_METHOD_STATE_EMPTY); // 결제 수단 사용 상태를 선택해주세요.
        }

        // 결제 방식
        if (paymentMethodDto.getAutoPay() == null || paymentMethodDto.getAutoPay() < 0 || paymentMethodDto.getAutoPay() > 2) {
            throw new CustomException(CustomError.PAYMENT_METHOD_AUTOPAY_EMPTY); // 결제 방식을 선택해주세요.
        }

        // PG 업체명
        if (paymentMethodDto.getPgName() == null || paymentMethodDto.getPgName().isEmpty()) {
            throw new CustomException(CustomError.PAYMENT_METHOD_PGNAME_EMPTY); // PG업체명을 입력해주세요.
        }

        // 상점 아이디
        if (paymentMethodDto.getMchtId() == null || paymentMethodDto.getMchtId().isEmpty()) {
            throw new CustomException(CustomError.PAYMENT_METHOD_MCHTID_EMPTY); // 상점아이디를 입력해주세요.
        }

        // 해시 체크키
        if (paymentMethodDto.getLicenseKey() == null || paymentMethodDto.getLicenseKey().isEmpty()) {
            throw new CustomException(CustomError.PAYMENT_METHOD_LICENSEKEY_EMPTY); // 해시체크키를 입력해주세요.
        }

        // 암호키
        if (paymentMethodDto.getAes256Key() == null || paymentMethodDto.getAes256Key().isEmpty()) {
            throw new CustomException(CustomError.PAYMENT_METHOD_AES256KEY_EMPTY); // 암호키를 입력해주세요.
        }

        // 결제 수단 종류
        if (paymentMethodDto.getMethodType() == null || paymentMethodDto.getMethodType().isEmpty()) {
            throw new CustomException(CustomError.PAYMENT_METHOD_METHODTYPE_EMPTY); // 결제 수단 종류를 입력해주세요.
        }

        // 결제 수단 코드
        if (paymentMethodDto.getMethod() == null || paymentMethodDto.getMethod().isEmpty()) {
            throw new CustomException(CustomError.PAYMENT_METHOD_METHOD_EMPTY); // 결제 수단 코드를 입력해주세요.
        }

        // 간편 결제 코드
//        if (paymentMethodDto.getCorpPayCode() == null || paymentMethodDto.getCorpPayCode().isEmpty()) {
//            throw new CustomException(CustomError.PAYMENT_METHOD_CORPPAYCODE_EMPTY); // 간편 결제 코드를 입력해주세요.
//        }

        // 결제 결과 메소드
        if (paymentMethodDto.getMethodNoti() == null || paymentMethodDto.getMethodNoti().isEmpty()) {
            throw new CustomException(CustomError.PAYMENT_METHOD_METHODNOTI_EMPTY); // 결제 결과 메소드를 입력해주세요.
        }

        // 서비스명(한글)
        if (paymentMethodDto.getMchtName() == null || paymentMethodDto.getMchtName().isEmpty()) {
            throw new CustomException(CustomError.PAYMENT_METHOD_MCHTNAME_EMPTY); // 서비스명(한글)을 입력해주세요.
        }

        // 서비스명(영어)
        if (paymentMethodDto.getMchtEName() == null || paymentMethodDto.getMchtEName().isEmpty()) {
            throw new CustomException(CustomError.PAYMENT_METHOD_MCHTENAME_EMPTY); // 서비스명(영어)을 입력해주세요.
        }

        // 결제 처리 URL
        if (paymentMethodDto.getNotiUrl() == null || paymentMethodDto.getNotiUrl().isEmpty()) {
            throw new CustomException(CustomError.PAYMENT_METHOD_NOTIURL_EMPTY); // 결제처리 URL을 입력해주세요.
        }

        // 결제 서버 URL
        if (paymentMethodDto.getPaymentServer() == null || paymentMethodDto.getPaymentServer().isEmpty()) {
            throw new CustomException(CustomError.PAYMENT_METHOD_PAYMENT_SERVER_EMPTY); // 결제 서버 URL을 입력해주세요.
        }

        // 결제 취소 서버 URL
        if (paymentMethodDto.getCancelServer() == null || paymentMethodDto.getCancelServer().isEmpty()) {
            throw new CustomException(CustomError.PAYMENT_METHOD_CANCEL_SERVER_EMPTY); // 결제 취소 서버 URL을 입력해주세요.
        }

        // 설명
        if (paymentMethodDto.getInfo() == null || paymentMethodDto.getInfo().isEmpty()) {
            throw new CustomException(CustomError.PAYMENT_METHOD_INFO_EMPTY); // 설명을 입력해주세요.
        }
    }

    /**
     * 결제 상품 상세
     * 선택한 결제 상품 idx 유효성 검사
     *
     * @param idx
     */
    private void isPaymentProductIdxValidate(Integer idx) {

        // 결제 상품 idx가 없는 경우
        if (idx == null || idx < 1) {
            throw new CustomException(CustomError.PAYMENT_PRODUCT_IDX_EMPTY); // 요청하신 결제 상품 상세 정보를 찾을 수 없습니다.
        }

        // 결제 상품 idx가 유효하지 않은 경우
        int totalCnt = paymentDaoSub.getPaymentProductIdxCnt(idx);
        if (totalCnt < 1) {
            throw new CustomException(CustomError.PAYMENT_PRODUCT_IDX_ERROR); // 요청하신 결제 상품 상세 정보를 찾을 수 없습니다.
        }
    }

    /**
     * 결제 상품 등록 & 수정
     * 입력 데이터 유효성 검사
     *
     * @param paymentProductDto
     */
    private void isInputProductDataValidate(PaymentProductDto paymentProductDto) {

        // 결제 상품 사용 상태
        if (paymentProductDto.getState() == null || paymentProductDto.getState() < 0 || paymentProductDto.getState() > 2) {
            throw new CustomException(CustomError.PAYMENT_PRODUCT_STATE_EMPTY); // 결제 상품 사용 상태를 선택해주세요.
        }

        // 상품명
        if (paymentProductDto.getTitle() == null || paymentProductDto.getTitle().isEmpty()) {
            throw new CustomException(CustomError.PAYMENT_PRODUCT_TITLE_EMPTY); // 상품명을 입력해주세요.
        }

        // 상품 구분
        if (paymentProductDto.getType() == null || paymentProductDto.getType() < 1 || paymentProductDto.getType() > 3 ) {
            throw new CustomException(CustomError.PAYMENT_PRODUCT_TYPE_EMPTY); // 상품 구분값을 입력해주세요.
        }

        // 코인
        if (paymentProductDto.getCoin() == null || paymentProductDto.getCoin() < 0) {
            throw new CustomException(CustomError.PAYMENT_PRODUCT_COIN_EMPTY); // 코인 개수를 입력해주세요.
        }

        // 마일리지
        if (paymentProductDto.getMileage() == null || paymentProductDto.getMileage() < 0) {
            throw new CustomException(CustomError.PAYMENT_PRODUCT_MILEAGE_EMPTY); // 마일리지 개수를 입력해주세요.
        }

        // 보너스 코인
//        if (paymentProductDto.getCoinFree() == null || paymentProductDto.getCoinFree() < 0) {
//            throw new CustomException(CustomError.PAYMENT_PRODUCT_COINFREE_EMPTY); // 보너스 코인 개수를 입력해주세요.
//        }
//
//        // 보너스 코인2
//        if (paymentProductDto.getCoinFree2() == null || paymentProductDto.getCoinFree2() < 0) {
//            throw new CustomException(CustomError.PAYMENT_PRODUCT_COINFREE2_EMPTY); // 보너스코인2 개수를 입력해주세요.
//        }

        // 결제 금액
        if (paymentProductDto.getPrice() == null || paymentProductDto.getPrice() < 0) {
            throw new CustomException(CustomError.PAYMENT_PRODUCT_PRICE_EMPTY); // 결제 금액을 입력해주세요.
        }

        // 상품 순서
        if (paymentProductDto.getSort() == null || paymentProductDto.getSort() < 0) {
            throw new CustomException(CustomError.PAYMENT_PRODUCT_SORT_EMPTY); // 상품 순서를 입력해주세요.
        }
    }

    /**
     * 결제수단 문자변환 dto(결제)
     *
     * @param payment
     */
    private void payTypeText(PaymentDto payment) {
        Map<String, String> map = new HashMap<>() {{
            put("CA", "신용카드");
            put("RA", "계좌이체");
            put("VA", "가상계좌");
            put("MP", "휴대폰");
            put("TC", "틴캐시");
            put("HM", "해피머니");
            put("CG", "컬쳐랜드");
            put("SG", "스마트문상");
            put("BG", "도서상품권");
            put("TM", "티머니");
            put("CP", "포인트다모아");
            put("NVP", "네이버페이");
            put("KKP", "카카오페이");
        }};
        if(Objects.equals(payment.getPayType(), "PZ")) {
            payment.setPayTypeText(map.get(payment.getPayMethod()));
        }else{
            payment.setPayTypeText(map.get(payment.getPayType()));
        }
    }

    /**
     * 원스토어 결제 취소
     *
     * @param payment
     */
    private void onestoreCancel(PaymentDto payment) {

        if(payment.getOrderNo() == null){
            return;
        }
        // 원스토어 토큰 생성
        MultiValueMap<String, String> edata = new LinkedMultiValueMap<>();
        edata.add("client_secret", "NgLOb3GsVZsh9+7mtd9vjgVX8B+NDe7huBw8i0dKrpY=");
        edata.add("client_id", "com.uxplusstudio.ggultoon");
        edata.add("grant_type", "client_credentials");
        String token = CurlLibrary.post("https://apis.onestore.co.kr/v2/oauth/token", edata);

        JSONObject json = new JSONObject(token);


        // 결제 정보
        org.json.JSONObject sendData = new org.json.JSONObject();
        sendData.put("developerOrderId", payment.getOrderNo());
        sendData.put("cancelCd", "TRD_CANCEL_ETC");
        sendData.put("cancelTime", System.currentTimeMillis());
        super.pushAlarm("원스토어 전송 : " + sendData.toString(), "LJH");
        String res = CurlLibrary.post("https://apis.onestore.co.kr/v2/purchase/developer/com.uxplusstudio.ggultoon/cancel", sendData.toString(), json.getString("access_token"));
        super.pushAlarm("원스토어 결과 : " + res, "LJH");
    }


}
