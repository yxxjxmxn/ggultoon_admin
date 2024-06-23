package com.architecture.admin.models.daosub.contents;

import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.contents.EpisodeDto;

import java.util.List;
import java.util.Map;

public interface EpisodeDaoSub {

    /**
     * 회차 목록
     * @param contentsIdx
     * @return
     */
    List<EpisodeDto> getList(Integer contentsIdx);


    /**
     * 회차 상세
     * @param idx
     * @return
     */
    EpisodeDto getView(Integer idx);


    /**
     * 회차 순서 정보
     * @param contentsIdx
     * @return
     */
    Map<String, Object> getLastEpisodeOrder(Integer contentsIdx);

    /**
     * 특정 작품의 전체 회차 idx 리스트
     * @param searchDto : contentsIdx(작품 idx), nowDate(현재 시간)
     * @return
     */
    List<Long> getEpIdxList(SearchDto searchDto);
}
