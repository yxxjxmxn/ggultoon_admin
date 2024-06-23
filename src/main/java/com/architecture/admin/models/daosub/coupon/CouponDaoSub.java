package com.architecture.admin.models.daosub.coupon;

import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.coupon.CouponDto;
import com.architecture.admin.models.dto.coupon.CouponStatDto;
import com.architecture.admin.models.dto.coupon.CouponStoreDto;
import com.architecture.admin.models.dto.coupon.CouponUsedDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface CouponDaoSub {

    /**
     * 쿠폰 업체 개수 카운트
     *
     * @param searchDto
     * @return
     */
    int getCouponStoreTotalCnt(SearchDto searchDto);

    /**
     * 쿠폰 업체 리스트 조회
     *
     * @param searchDto
     * @return
     */
    List<CouponStoreDto> getCouponStoreList(SearchDto searchDto);

    /**
     * 쿠폰 업체 상세
     *
     * @param idx
     */
    CouponStoreDto getViewCouponStore(Long idx);

    /**
     * 쿠폰 개수 카운트
     *
     * @param searchDto
     * @return
     */
    int getCouponTotalCnt(SearchDto searchDto);

    /**
     * 쿠폰 리스트 조회
     *
     * @param searchDto
     * @return
     */
    List<CouponDto> getCouponList(SearchDto searchDto);

    /**
     * 쿠폰 상세
     *
     * @param idx
     */
    CouponDto getViewCoupon(Long idx);

    /**
     * 쿠폰 사용 내역 개수 카운트
     *
     * @param searchDto
     * @return
     */
    int getCouponUsedTotalCnt(SearchDto searchDto);

    /**
     * 쿠폰 사용 내역 조회
     *
     * @param searchDto
     * @return
     */
    List<CouponUsedDto> getCouponUsedList(SearchDto searchDto);

    /**
     * 쿠폰 통계 개수 카운트
     *
     * @param searchDto
     * @return
     */
    int getCouponStatTotalCnt(SearchDto searchDto);

    /**
     * 쿠폰 통계 조회
     *
     * @param searchDto
     * @return
     */
    List<CouponStatDto> getCouponStat(SearchDto searchDto);
}
