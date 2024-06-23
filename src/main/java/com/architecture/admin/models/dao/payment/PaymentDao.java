package com.architecture.admin.models.dao.payment;

import com.architecture.admin.models.dto.coin.CoinDto;
import com.architecture.admin.models.dto.payment.PaymentDto;
import com.architecture.admin.models.dto.payment.PaymentMethodDto;
import com.architecture.admin.models.dto.payment.PaymentProductDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface PaymentDao {

    /*****************************************************
     * 결제 내역
     ****************************************************/

    /**
     * payment_log 등록
     *
     * @param paymentDto (취소할 결제 정보의 payment.idx)
     * @return
     */
    int insertPaymentLog(PaymentDto paymentDto);

    /**
     * member_coin_save_log 등록
     *
     * @param paymentDto (취소할 결제 정보의 payment.idx)
     * @return
     */
    int insertCoinSaveLog(PaymentDto paymentDto);

    /**
     * member_mileage_save_log 등록
     *
     * @param paymentDto (취소할 결제 정보의 payment.idx)
     * @return
     */
    int insertMileageSaveLog(PaymentDto paymentDto);

    /**
     * member_coin_used_log (coin) 등록
     *
     * @param paymentDto (취소할 결제 정보의 payment.idx)
     * @return
     */
    int insertCoinUsedLog(PaymentDto paymentDto);

    /**
     * member_mileage_used_log 등록
     *
     * @param paymentDto (취소할 결제 정보의 payment.idx)
     * @return
     */
    int insertMileageUsedLog(PaymentDto paymentDto);

    /**
     * payment 테이블 insert(관리자 코인 & 마일리지 지급)
     *
     * @param coinDto
     * @return
     */
    int insertPaymentByAdmin(CoinDto coinDto);

    /**
     * payment_info 테이블 insert
     *
     * @param paymentDto
     * @return
     */
    int insertPaymentInfo(PaymentDto paymentDto);

    /**
     * payment - state 변경 & 취소일 등록
     *
     * @param paymentDto (취소할 결제 정보)
     * @return
     */
    int updatePayment(PaymentDto paymentDto);

    /**
     * payment_info - state 변경
     *
     * @param paymentDto (취소할 결제 정보)
     * @return
     */
    int updatePaymentInfo(PaymentDto paymentDto);

    /**
     * member_coin_save - state 변경
     *
     * @param paymentDto (취소할 결제 정보)
     * @return
     */
    int updateCoinSave(PaymentDto paymentDto);

    /**
     * member_mileage_save - state 변경
     *
     * @param paymentDto (취소할 결제 정보)
     * @return
     */
    int updateMileageSave(PaymentDto paymentDto);

    /**
     * member_coin_used - state 변경
     *
     * @param paymentDto (취소할 결제 정보)
     * @return
     */
    int updateCoinUsed(PaymentDto paymentDto);

    /**
     * member_mileage_used - state 변경
     *
     * @param paymentDto (취소할 결제 정보)
     * @return
     */
    int updateMileageUsed(PaymentDto paymentDto);

    /**
     * member_coin - coin / coin_free / mileage 변경
     *
     * @param memberCoin (변경된 회원의 코인, 보너스, 마일리지 정보)
     * @return
     */
    int updateMemberCoin(PaymentDto memberCoin);

    /*****************************************************
     * 결제 수단
     ****************************************************/

    /**
     * 결제 수단 등록
     *
     * @param paymentMethodDto
     * @return
     */
    int registerPaymentMethod(PaymentMethodDto paymentMethodDto);

    /**
     * 결제 수단 삭제
     *
     * @param idx
     * @return
     */
    int deletePaymentMethod(Integer idx);

    /**
     * 결제 수단 수정
     *
     * @param paymentMethodDto
     * @return
     */
    int modifyPaymentMethod(PaymentMethodDto paymentMethodDto);

    /*****************************************************
     * 결제 상품
     ****************************************************/

    /**
     * 결제 상품 등록
     *
     * @param paymentProductDto
     * @return
     */
    int registerPaymentProduct(PaymentProductDto paymentProductDto);

    /**
     * 결제 상품 삭제
     *
     * @param idx
     * @return
     */
    int deletePaymentProduct(Integer idx);

    /**
     * 결제 상품 수정
     *
     * @param paymentProductDto
     * @return
     */
    int modifyPaymentProduct(PaymentProductDto paymentProductDto);
}
