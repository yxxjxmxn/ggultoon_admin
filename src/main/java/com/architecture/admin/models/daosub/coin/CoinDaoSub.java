package com.architecture.admin.models.daosub.coin;

import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.coin.CoinDto;
import com.architecture.admin.models.dto.coin.MileageSaveDto;
import com.architecture.admin.models.dto.coin.MileageUseDto;
import com.architecture.admin.models.dto.notification.NotificationDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface CoinDaoSub {

    /**
     * 코인 적립 내역 개수
     *
     * @param searchDto
     * @return
     */
    int getSavedCoinTotalCnt(SearchDto searchDto);

    /**
     * 코인 사용 내역 개수
     *
     * @param searchDto
     * @return
     */
    int getUsedCoinTotalCnt(SearchDto searchDto);

    /**
     * 마일리지 적립 내역 개수
     *
     * @param searchDto
     * @return
     */
    int getSavedMileageTotalCnt(SearchDto searchDto);

    /**
     * 마일리지 사용 내역 개수
     *
     * @param searchDto
     * @return
     */
    int getUsedMileageTotalCnt(SearchDto searchDto);

    /**
     * 관리자 지급 or 차감 리스트 총 개수
     *
     * @param searchDto
     * @return
     */
    int getAdminCoinTotalCnt(SearchDto searchDto);

    /**
     * 코인 적립 내역 리스트
     *
     * @param searchDto
     * @return
     */
    List<CoinDto> getSavedCoinTotalList(SearchDto searchDto);

    /**
     * 코인 사용 내역 리스트
     *
     * @param searchDto
     * @return
     */
    List<CoinDto> getUsedCoinTotalList(SearchDto searchDto);

    /**
     * 마일리지 적립 내역 리스트
     *
     * @param searchDto
     * @return
     */
    List<MileageSaveDto> getSavedMileageTotalList(SearchDto searchDto);

    /**
     * 마일리지 사용 내역 리스트
     *
     * @param searchDto
     * @return
     */
    List<MileageUseDto> getUsedMileageTotalList(SearchDto searchDto);

    /**
     * 누적 결제 코인 & 누적 사용 코인 & 사용 가능 코인 조회
     *
     * @param memberIdx
     * @return
     */
    CoinDto getMemberCoinInfo(long memberIdx);

    /**
     * 누적 마일리지 & 누적 사용 마일리지 & 사용 가능 마일리지 조회
     *
     * @param memberIdx
     * @return
     */
    CoinDto getMemberMileageInfo(long memberIdx);

    /**
     * 잔여 코인 & 잔여 무료 코인 조회
     *
     * @param memberIdx
     * @return
     */
    CoinDto getRestCoinAndCoinFree(long memberIdx);

    /**
     * 잔여 마일리지 조회
     *
     * @param memberIdx
     * @return
     */
    CoinDto getRestMileage(long memberIdx);

    /**
     * 관리자 코인 지급 & 차감 리스트
     * @param searchDto
     * @return
     */
    List<CoinDto> getAdminCoinList(SearchDto searchDto);

    /**
     * 코인 OR 마일리지 지급일 조회
     * @param dto
     * @return
     */
    String getCoinOrMileageRegdate(NotificationDto dto);
}
