package com.architecture.admin.models.daosub.notification;

import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.notification.NotificationDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface NotificationDaoSub {

    /**
     * 알림 개수 전체 카운트
     */
    int getNotificationTotalCnt(SearchDto searchDto);

    /**
     * 알림 리스트 조회
     */
    List<NotificationDto> getNotificationList(SearchDto searchDto);

    /**
     * 알림 상세
     */
    NotificationDto getViewNotification(Long idx);

    /**
     * 회원에게 전송된 공지사항 알림 idx 리스트 조회
     */
    List<Long> getNoticeAlarmIdxList();
}
