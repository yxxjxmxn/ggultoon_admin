package com.architecture.admin.models.daosub.event;

import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.event.NewYearStatDto;
import com.architecture.admin.models.dto.event.NewYearViewDto;
import com.architecture.admin.models.dto.event.PromotionDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface EventDaoSub {

    /**
     * 2024 설연휴 이벤트 참여 내역 개수 조회
     * @return
     */
    int getNewYearEventCnt(SearchDto searchDto);
    
    /**
     * 2024 설연휴 이벤트 참여 내역 조회
     * @return
     */
    List<NewYearViewDto> getNewYearEventList(SearchDto searchDto);

    /**
     * 2024 설연휴 이벤트 통계 개수 조회
     * @return
     */
    int getNewYearEventStatCnt(SearchDto searchDto);

    /**
     * 2024 설연휴 이벤트 통계 조회
     * @return
     */
    List<NewYearStatDto> getNewYearEventStat(SearchDto searchDto);

    /**
     * 프로모션 이벤트 조회
     * @return
     */
    List<PromotionDto> getPromotion();
}
