package com.architecture.admin.models.daosub.payment;

import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.member.MemberPurchaseDto;
import com.architecture.admin.models.dto.payment.PaymentDto;
import com.architecture.admin.models.dto.payment.PaymentMethodDto;
import com.architecture.admin.models.dto.payment.PaymentProductDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface PaymentDaoSub {

    /**************************************************************************************
     * 결제 내역
     **************************************************************************************/

    /**
     * 결제 내역 전체 카운트
     *
     * @param searchDto
     * @return
     */
    int getPaymentTotalCnt(SearchDto searchDto);

    /**
     * 결제 내역 전체 조회
     *
     * @param searchDto
     * @return
     */
    List<PaymentDto> getPaymentList(SearchDto searchDto);

    /**
     * 결제 내역 상세
     *
     * @param idx (payment.idx)
     * @return
     */
    PaymentDto getViewPayment(Long idx);

    /**
     * member_coin_save_log 로그 조회
     *
     * @param paymentDto
     * @return
     */
    PaymentDto getCoinSaveLog(PaymentDto paymentDto);

    /**
     * member_mileage_save_log 로그 조회
     *
     * @param paymentDto
     * @return
     */
    PaymentDto getMileageSaveLog(PaymentDto paymentDto);

    /**
     * member_coin_used_log 로그 조회
     *
     * @param paymentDto
     * @return
     */
    PaymentDto getCoinUsedLog(PaymentDto paymentDto);

    /**
     * member_mileage_used_log 로그 조회
     *
     * @param paymentDto
     * @return
     */
    PaymentDto getMileageUsedLog(PaymentDto paymentDto);

    /**
     * member_coin 상세 조회
     *
     * @param paymentDto
     * @return
     */
    PaymentDto getMemberCoin(PaymentDto paymentDto);

    /**************************************************************************************
     * 결제 수단
     **************************************************************************************/

    /**
     * 결제수단 전체 개수 카운트
     *
     * @param searchDto
     * @return
     */
    int getPaymentMethodTotalCnt(SearchDto searchDto);

    /**
     * 유효한 결제 수단 idx인지 조회
     *
     * @param idx
     * @return
     */
    int getPaymentMethodIdxCnt(Integer idx);

    /**
     * 결제 수단 리스트 조회
     *
     * @param searchDto
     * @return
     */
    List<PaymentMethodDto> getPaymentMethodList(SearchDto searchDto);

    /**
     * 결제 수단 상세 조회
     *
     * @param idx
     * @return
     */
    PaymentMethodDto getViewPaymentMethod(Integer idx);

    /**************************************************************************************
     * 결제 상품
     **************************************************************************************/

    /**
     * 결제 상품 전체 카운트
     *
     * @param searchDto
     * @return
     */
    int getPaymentProductTotalCnt(SearchDto searchDto);

    /**
     * 결제 상품 리스트 전체 조회
     *
     * @param searchDto
     * @return
     */
    List<PaymentProductDto> getPaymentProductList(SearchDto searchDto);

    /**
     * 유효한 결제 상품 idx인지 조회
     *
     * @param idx
     * @return
     */
    int getPaymentProductIdxCnt(Integer idx);

    /**
     * 결제 상품 상세 조회
     *
     * @param idx
     * @return
     */
    PaymentProductDto getViewPaymentProduct(Integer idx);

    /**************************************************************************************
     * 회원 회차 구매
     **************************************************************************************/

    /**
     * 전체 회원 회차구매 개수 조회(결제 사용내역)
     *
     * @param searchDto
     * @return
     */
    int getPurchaseTotalCnt(SearchDto searchDto);

    /**
     * 회원 회차 구매내역 리스트(결제 사용내역)
     *
     * @param searchDto
     * @return
     */
    List<MemberPurchaseDto> getPurchaseList(SearchDto searchDto);

    /**
     * 회원 회차 구매내역 개수
     *
     * @param memberIdx
     * @return
     */
    int getMemberPurchaseTotalCnt(Long memberIdx);

    /**
     * 회원 상품 구매내역 정보(회원 상세 보기 시)
     *
     * @param memberIdx
     */
    List<MemberPurchaseDto> getMemberPurchaseInfo(long memberIdx);

    /**
     * payment 개수 조회
     *
     * @param memberIdx
     * @return
     */
    int getPaymentCnt(Long memberIdx);

}
