package com.architecture.admin.models.dao.event;

import com.architecture.admin.models.dto.contents.EpisodeDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface EventDao {

    /**
     * 프로모션 이벤트
     * @param promotionEpisodeList
     * @return
     */
    Integer setPromotionEvent(List<EpisodeDto> promotionEpisodeList);

    /**
     * 프로모션 적용여부
     */
    void setPromotionEventUsed();
}
