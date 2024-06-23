package com.architecture.admin.services.notification;

import com.architecture.admin.libraries.PaginationLibray;
import com.architecture.admin.libraries.excel.ExcelData;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.daosub.coin.CoinDaoSub;
import com.architecture.admin.models.daosub.contents.ContentsDaoSub;
import com.architecture.admin.models.daosub.notification.NotificationDaoSub;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.notification.NotificationDto;
import com.architecture.admin.services.BaseService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.architecture.admin.libraries.utils.NotificationUtils.*;
import static com.architecture.admin.libraries.utils.NotificationUtils.Notification.*;

@RequiredArgsConstructor
@Service
@Transactional
public class NotificationService extends BaseService {

    private final NotificationDaoSub notificationDaoSub;
    private final CoinDaoSub coinDaoSub;
    private final ContentsDaoSub contentsDaoSub;
    private final ExcelData excelData;

    /**
     * 알림 리스트 엑셀 다운로드
     * @return
     */
    @Transactional(readOnly = true)
    public Map<String, Object> excelNotification(SearchDto searchDto) {

        // 검색어 공백제거
        if (searchDto.getSearchWord() != null && !searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // 내보낼 데이터 set
        List<NotificationDto> notificationList = null;

        // 알림 개수 전체 카운트
        int totalCnt = notificationDaoSub.getNotificationTotalCnt(searchDto);

        // 알림이 있는 경우
        if (totalCnt > 0) {

            // 알림 리스트 조회
            notificationList = notificationDaoSub.getNotificationList(searchDto);

            // 알림 사용 상태값 문자변환
            notificationStateText(notificationList);

        }

        // 엑셀 데이터 생성
        return excelData.createExcelData(notificationList, NotificationDto.class);
    }

    /**
     * 알림 리스트 조회
     * @return
     */
    @Transactional(readOnly = true)
    public JSONObject getNotificationList(SearchDto searchDto) {

        // 검색어 공백제거
        if (searchDto.getSearchWord() != null && !searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // 내보낼 데이터 set
        JSONObject joData = new JSONObject();
        List<NotificationDto> notificationList = null;

        // 알림 개수 전체 카운트
        int totalCnt = notificationDaoSub.getNotificationTotalCnt(searchDto);
        
        // 알림이 있는 경우
        if (totalCnt > 0) {

            // recordSize set
            Integer recordSize = searchDto.getSearchCount();
            if (recordSize != null && recordSize > 0) {
                searchDto.setRecordSize(recordSize);
            }

            // paging
            PaginationLibray pagination = new PaginationLibray(totalCnt, searchDto);
            searchDto.setPagination(pagination);

            // 알림 리스트 조회
            notificationList = notificationDaoSub.getNotificationList(searchDto);

            // 알림 사용 상태값 문자변환
            notificationStateText(notificationList);

            // paging 담기
            joData.put("params", new JSONObject(searchDto));

        }

        // list 담기
        joData.put("list", notificationList);

        return joData;
    }

    /**
     * 알림 상세
     * @return
     */
    @Transactional(readOnly = true)
    public JSONObject getViewNotification(Long idx) {

        // 알림 idx 유효성 검사
        isNotificationIdxValidate(idx);

        // 알림 상세 조회
        NotificationDto viewNotification = notificationDaoSub.getViewNotification(idx);

        // 알림 사용 상태값 문자변환
        notificationStateText(viewNotification);

        // 내보낼 데이터 set
        JSONObject joData = new JSONObject(viewNotification);

        return joData;

    }

    /*************************************************************
     * Validation
     *************************************************************/

    /**
     * 선택한 알림 idx 유효성 검사
     *
     * @param idx
     */
    private void isNotificationIdxValidate(Long idx) {

        // 알림 idx가 없는 경우
        if (idx == null || idx < 1) {
            throw new CustomException(CustomError.NOTIFICATION_IDX_EMPTY); // 요청하신 알림 정보를 찾을 수 없습니다.
        }
    }

    /*************************************************************
     * SUB
     *************************************************************/

    /**
     * 회원 알림 리스트
     * 문자변환 list
     */
    private void notificationStateText(List<NotificationDto> list) {
        for (NotificationDto dto : list) {
            notificationStateText(dto);
        }
    }

    /**
     * 회원 알림 리스트
     * 문자변환 dto
     */
    private void notificationStateText(NotificationDto dto)  {
        
        // 알림 사용 상태 set
        if (dto.getState() != null) {

            if (dto.getState() == 0) { // 미사용
                dto.setStateText(super.langMessage("lang.notification.state.unuse"));
                dto.setStateBg("badge-danger");

            } else if (dto.getState() == 1) { // 사용
                dto.setStateText(super.langMessage("lang.notification.state.use"));
                dto.setStateBg("badge-success");
            }
        }

        // 알림 카테고리 set
        if (dto.getCategory() != null && !dto.getCategory().isEmpty()) {

            if (dto.getCategory().equals(CHARGE)) { // 충전

                // 카테고리 set
                dto.setCategoryText(super.langMessage("lang.notification.category.charge"));

                // 알림 내용 및 url set
                dto.setUrl(CHARGE_COMPLETE.getUrl()); // 이용내역 충전 페이지
                dto.setTitle(super.langMessage(CHARGE_COMPLETE.getText())); // 충전이 정상적으로 완료됐어요.

            } else if (dto.getCategory().equals(EXPIRE)) { // 소멸

                // 카테고리 set
                dto.setCategoryText(super.langMessage("lang.notification.category.expire"));

                if (dto.getType() != null && !dto.getType().isEmpty()) {

                    // 소멸될 코인 또는 마일리지의 지급일 조회 -> 00년 00월 00일로 변환
                    String regdate = dateLibrary.convertDate(coinDaoSub.getCoinOrMileageRegdate(dto));

                    String type = "";
                    if (dto.getType().equals("member_coin_used")) { // 코인 소멸
                        type = COIN_TEXT; // 코인

                    } else if (dto.getType().equals("member_mileage_used")) { // 마일리지 소멸
                        type = MILEAGE_TEXT; // 마일리지
                    }

                    // 알림 내용 및 url set
                    String title = super.langMessage(EXPIRE_WEEK_LATER.getText()); // 7일 후 regdate에 지급된 00개가 소멸 예정이에요.
                    title = title.replace("regdate", regdate).replace("00", type + " " + dto.getTitle());
                    dto.setTitle(title);
                    dto.setUrl(EXPIRE_WEEK_LATER.getUrl()); // 이용내역 소멸 페이지
                }
            } else if (dto.getCategory().equals(CONTENT)) { // 작품

                // 카테고리 set
                dto.setCategoryText(super.langMessage("lang.notification.category.content"));

                if (dto.getType().equals("episode")) { // 신규 회차 업로드

                    // 신규 회차가 업로드된 작품 및 회차 번호 조회
                    HashMap<String, Object> contentInfo = contentsDaoSub.getContentInfo(dto);
                    String content = contentInfo.get("content").toString(); // 작품 이름
                    String episodeNumber = contentInfo.get("episodeNumber").toString(); // 회차 번호


                    // 알림 내용 set
                    String type = "";
                    if (!contentInfo.get("categoryIdx").equals(2)) { // 웹툰 OR 소설
                        type = "화";

                    } else { // 만화
                        type = "권";
                    }

                    String title = super.langMessage(NEW_EPISODE_UPDATE.getText()); // content의 episode이(가) 새롭게 업데이트됐어요.
                    title = title.replace("content", content).replace("episodeNumber", episodeNumber + type);
                    dto.setTitle(title);

                    // 알림 url set
                    String contentIdx = contentInfo.get("idx").toString();
                    String url = NEW_EPISODE_UPDATE.getUrl();
                    url = url.replace("idx", contentIdx);
                    dto.setUrl(url); // 해당되는 작품의 회차 리스트 페이지
                }

            } else if (dto.getCategory().equals(NOTICE)) { // 공지

                // 카테고리 set
                dto.setCategoryText(super.langMessage("lang.notification.category.notice"));

                if (dto.getType() != null && !dto.getType().isEmpty()) {

                    if (dto.getType().equals("payment_method")) { // 신규 결제 수단 오픈

                        // 알림 내용 및 url set
                        dto.setTitle(super.langMessage(NEW_PAY_METHOD_OPEN.getText())); // 새로운 결제 수단이 오픈했어요.
                        dto.setUrl(NEW_PAY_METHOD_OPEN.getUrl()); // 충전소 페이지

                    } else { // 그 외 일반 공지사항

                        // 알림 내용 및 url set
                        dto.setTitle(super.langMessage(NEW_NOTICE_UPDATE.getText())); // 새로운 공지사항이 등록됐어요.
                        dto.setUrl(NEW_NOTICE_UPDATE.getUrl()); // 공지사항 페이지

                    }
                }
            } else if (dto.getCategory().equals(EVENT)) { // 이벤트
                // 카테고리 set
                dto.setCategoryText(super.langMessage("lang.notification.category.event"));

                // 알림 내용 및 url set
                dto.setUrl(EVENT_URL); // 이벤트 페이지

            } else if (dto.getCategory().equals(CANCEL)) { // 취소

                // 카테고리 set
                dto.setCategoryText(super.langMessage("lang.notification.category.cancel"));

                // 알림 내용 및 url set
                dto.setTitle(super.langMessage(PAYMENT_CANCEL.getText())); // 결제 취소가 완료됐어요.
                dto.setUrl(PAYMENT_CANCEL.getUrl()); // 이용내역 충전 페이지
            }
        }
    }
}
