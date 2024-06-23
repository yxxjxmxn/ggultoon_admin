package com.architecture.admin.models.dao.coupon;

import com.architecture.admin.models.dto.coupon.CouponDto;
import com.architecture.admin.models.dto.coupon.CouponStoreDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface CouponDao {

    /**
     * 쿠폰 업체 등록
     *
     * @param couponStoreDto
     * @return
     */
    int insertCouponStore(CouponStoreDto couponStoreDto);

    /**
     * 쿠폰 업체 삭제
     *
     * @param idx
     * @return
     */
    int deleteCouponStore(Long idx);

    /**
     * 쿠폰 업체 수정
     *
     * @param couponStoreDto
     * @return
     */
    int updateCouponStore(CouponStoreDto couponStoreDto);

    /**
     * 쿠폰 등록
     *
     * @param couponDto
     * @return
     */
    int insertCoupon(CouponDto couponDto);

    /**
     * 쿠폰 삭제
     *
     * @param idx
     * @return
     */
    int deleteCoupon(Long idx);

    /**
     * 쿠폰 수정
     *
     * @param couponDto
     * @return
     */
    int updateCoupon(CouponDto couponDto);
}
