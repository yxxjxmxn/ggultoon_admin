package com.architecture.admin.models.dao.notification;

import com.architecture.admin.models.dto.notification.NotificationDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface NotificationDao {

    /**
     * 결제 취소 알림 전송
     * @param notificationDto
     * @return
     */
    void insertPaymentCancelAlarm(NotificationDto notificationDto);

    /**
     * 공지사항 알림 전송
     * @param sendNoticeList
     * @return
     */
    void insertNoticeAlarm(List<NotificationDto> sendNoticeList);
}
