package com.architecture.admin.models.dao.statistics;

import com.architecture.admin.models.dto.statistics.StatDto;

import java.util.List;

public interface StatDao {

    /**
     * 정산 통계 수동
     * @param statList
     */
    Integer setStat(List<StatDto> statList);
}
