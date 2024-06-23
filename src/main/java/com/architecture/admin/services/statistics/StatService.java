package com.architecture.admin.services.statistics;

import com.architecture.admin.models.dao.statistics.StatDao;
import com.architecture.admin.models.daosub.statistics.StatDaoSub;
import com.architecture.admin.models.dto.statistics.StatDto;
import com.architecture.admin.services.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@RequiredArgsConstructor
@Service
@Transactional
public class StatService extends BaseService {

    private final StatDaoSub statDaoSub;

    private final StatDao statDao;

    /**
     * 정산 통계 수동
     * @param date
     * @return
     */
    @Transactional
    public Integer stat(Date date) {

        // cron 활성화 상태 체크
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        sdf.setLenient(false);
        Calendar day = Calendar.getInstance();
        // 날짜 세팅
        day.setTime(date);

        // 해당 날짜의 정산 시작일 세팅
        String startDate = sdf.format(day.getTime());

        // +1 일 종료일
        day.add(Calendar.DATE, 1);
        String endDate = sdf.format(day.getTime());

        Map<String, Object> map = new HashMap<>();
        map.put("startDate", startDate);
        map.put("endDate", endDate);

        // 일별 통계 목록
        List<StatDto> statList = statDaoSub.getStat(map);
        Integer result = 0;
        if (!statList.isEmpty()) {
            result = statDao.setStat(statList);
        }

        return result;
    }
}
