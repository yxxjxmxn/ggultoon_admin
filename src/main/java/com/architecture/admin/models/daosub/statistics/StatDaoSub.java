package com.architecture.admin.models.daosub.statistics;

import com.architecture.admin.models.dto.statistics.StatDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface StatDaoSub {

    /**
     * 정산 통계 수동
     * @param map
     * @return
     */
    List<StatDto> getStat(Map<String, Object> map);
}
