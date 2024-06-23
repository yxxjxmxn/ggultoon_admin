package com.architecture.admin.models.dao.coin;

import com.architecture.admin.models.dto.coin.CoinDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface CoinDao {

    /**************************************************************
     * SELECT
     **************************************************************/

    /**
     * 남은 코인 & coin_used.idx 값 구하기(차감 시 사용)
     *
     * @param coinDto
     * @return : 1.coin_used.idx / 2.rest_coin
     */

    CoinDto getRestCoinAndIdxFromCoinUsed(CoinDto coinDto);

    /**
     * 남은 마일리지 조회 & mileage_used.idx 값 구하기(차감 시 사용)
     *
     * @param coinDto : memberIdx
     * @return : 1. mileage_used.idx / 2. rest_mileage
     */
    CoinDto getRestMileageFromMileageUsed(CoinDto coinDto);

    /**************************************************************
     * INSERT
     **************************************************************/

    /**
     * 관리자 코인 지급(payment 테이블 insert)
     *
     * @param coinDto
     * @return
     */
    int insertMemberCoinSave(CoinDto coinDto);

    /**
     * 관리자 마일리지 지급(mileage 테이블 insert)
     *
     * @param coinDto
     * @return
     */
    int insertMemberMileageSave(CoinDto coinDto);

    /**
     * 관리자 코인 & 마일리지 지급(admin_coin_log 테이블 insert)
     *
     * @param coinDto
     * @return
     */
    int insertAdminCoinLog(CoinDto coinDto);

    /**
     * member_coin_save_log 테이블 insert(관리자 코인 지급)
     *
     * @param coinDto
     * @return
     */
    int insertCoinSaveLog(CoinDto coinDto);

    /**
     * member_mileage_save_log 테이블 insert(관리자 마일리지 지급)
     *
     * @param coinDto
     * @return
     */
    int insertMileageSaveLog(CoinDto coinDto);

    /**
     * member_coin_used 테이블 등록
     *
     * @param coinDto
     * @return
     */
    int insertMemberCoinUsed(CoinDto coinDto);

    /**
     * member_mileage_used 테이블 등록
     *
     * @param coinDto
     * @return
     */
    int insertMemberMileageUsed(CoinDto coinDto);

    /**
     * coin_used_log 테이블 등록
     *
     * @param coinDto
     */
    void insertCoinUsedLog(CoinDto coinDto);

    /**
     * member_mileage_used_log 테이블 등록
     *
     * @param coinDto
     */
    void insertMileageUsedLog(CoinDto coinDto);


    /**************************************************************
     * UPDATE
     **************************************************************/

    /**
     * 회원 코인 update(member_coin 테이블 update)
     *
     * @param coinDto
     * @return
     */
    int updateMemberCoin(CoinDto coinDto);

    /**
     * coin_used 테이블 update(차감된 결과값으로 남은 코인 update)
     *
     * @param coinDto : 1.memberIdx /  2.idx(coin_used.idx) /  3.subResultCnt 이용
     */
    void updateCoinUsed(CoinDto coinDto);

    /**
     * mileage_used 테이블 update(차감된 결과 값으로 남은 마일리지 update)
     *
     * @param coinDto
     */
    void updateMileageUsed(CoinDto coinDto);

    /**
     * admin_coin 테이블 insert
     *
     * @param coinDto
     * @return
     */
    int insertAdminCoin(CoinDto coinDto);

    /**
     * admin_coin 테이블 group_idx 최대값 조회
     *
     * @return
     */
    long getAdminCoinMaxGroupIdx();
}
