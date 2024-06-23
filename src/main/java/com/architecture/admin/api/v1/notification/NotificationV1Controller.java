package com.architecture.admin.api.v1.notification;

import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.libraries.excel.ExcelXlsxView;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.services.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/notifications")
public class NotificationV1Controller extends BaseController {

    private final NotificationService notificationService;

    /**
     * 회원 알림 리스트 엑셀 다운로드
     *
     * @param searchDto
     * @return
     */
    @GetMapping("/excel")
    public ModelAndView notificationListExcelDownload(@ModelAttribute("param") SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(37);

        // 뷰에 담을 모델객체를 생성(엑셀 파일에 들어갈 데이터)
        Map<String, Object> mExcelData = notificationService.excelNotification(searchDto);

        // AbstractXlsxView 가 동작
        return new ModelAndView(new ExcelXlsxView(), mExcelData);
    }

    /**
     * 회원 알림 리스트 조회
     * @return
     */
    @GetMapping()
    public String notificationList(@ModelAttribute SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(37);

        // 알림 리스트 조회
        JSONObject data = notificationService.getNotificationList(searchDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.notification.success.search"); // 검색을 완료하였습니다.

        // 알림 리스트가 없는 경우
        if (data.getJSONArray("list").isEmpty()) {
            sMessage = super.langMessage("lang.notification.exception.search_fail"); // 검색 결과가 없습니다.
        }

        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 회원 알림 상세
     * @param idx
     * @return
     */
    @GetMapping("/{idx}")
    public String viewNotification(@PathVariable(name = "idx") Long idx){

        // 관리자 접근 권한
        super.adminAccessFail(37);

        // 알림 상세
        JSONObject joData = notificationService.getViewNotification(idx);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.notification.success.search"); // 검색을 완료하였습니다.

        // 조회 결과가 없는 경우
        if (joData.isEmpty()) {
            sMessage = super.langMessage("lang.notification.exception.search_fail"); // 검색 결과가 없습니다.
        }

        return displayJson(true, "1000", sMessage, joData);
    }
}
