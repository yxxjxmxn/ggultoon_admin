package com.architecture.admin.api.v1.board;

import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.libraries.excel.ExcelXlsxView;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.board.FileResponseDto;
import com.architecture.admin.models.dto.board.NoticeDto;
import com.architecture.admin.services.board.BoardService;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/boards")
public class BoardV1Controller extends BaseController {

    private final BoardService boardService;

    /*************************************************************
     * 공지사항 리스트
     *************************************************************/

    /**
     * 공지사항 리스트 엑셀 다운로드
     *
     * @param searchDto
     * @return
     */
    @GetMapping("/notices/excel")
    public ModelAndView noticeListExcelDownload(@ModelAttribute("param") SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(39);

        // 뷰에 담을 모델객체를 생성(엑셀 파일에 들어갈 데이터)
        Map<String, Object> mExcelData = boardService.excelNotice(searchDto);

        // AbstractXlsxView 가 동작
        return new ModelAndView(new ExcelXlsxView(), mExcelData);
    }

    /**
     * 공지사항 리스트 조회
     * @return
     */
    @GetMapping("/notices")
    public String noticeList(@ModelAttribute SearchDto searchDto) {

        // 관리자 접근 권한
        super.adminAccessFail(39);

        // 공지사항 리스트 조회
        JSONObject data = boardService.getNoticeList(searchDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.board.success.search"); // 검색을 완료하였습니다.

        // 공지사항 리스트가 없는 경우
        if (data.getJSONArray("list").isEmpty()) {
            sMessage = super.langMessage("lang.board.exception.search_fail"); // 검색 결과가 없습니다.
        }

        return displayJson(true, "1000", sMessage, data);
    }

    /**
     * 공지사항 상세
     * @param idx
     * @return
     */
    @GetMapping("/notices/{idx}")
    public String viewNotice(@PathVariable(name = "idx") Long idx){

        // 관리자 접근 권한
        super.adminAccessFail(39);

        // 공지사항 상세
        JSONObject joData = boardService.getViewNotice(idx);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.board.success.search"); // 검색을 완료하였습니다.

        // 조회 결과가 없는 경우
        if (joData.isEmpty()) {
            sMessage = super.langMessage("lang.board.exception.search_fail"); // 검색 결과가 없습니다.
        }

        return displayJson(true, "1000", sMessage, joData);
    }

    /**
     * 공지사항 등록
     * @param noticeDto
     * @return
     */
    @PostMapping("/notices")
    public String registerNotice(@ModelAttribute("NoticeDto") NoticeDto noticeDto) {

        // 관리자 접근 권한
        super.adminAccessFail(39);

        // 공지사항 등록
        boardService.registerNotice(noticeDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.board.success.register"); // 등록하였습니다.

        return displayJson(true, "1000", sMessage);
    }

    /**
     * 공지사항 수정
     * @param idx
     * @return
     */
    @PutMapping("/notices/{idx}")
    public String modifyNotice(@PathVariable(name = "idx") Long idx,
                               @ModelAttribute("NoticeDto") NoticeDto noticeDto) {

        // 관리자 접근 권한
        super.adminAccessFail(39);

        // idx set
        noticeDto.setIdx(idx);

        // 공지사항 수정
        boardService.modifyNotice(noticeDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.board.success.modify"); // 수정하였습니다.

        return displayJson(true, "1000", sMessage);
    }

    /**
     * 공지사항 삭제
     * @param idx
     * @return
     */
    @DeleteMapping("/notices/{idx}")
    public String deleteNotice(@PathVariable(name = "idx") Long idx) {

        // 관리자 접근 권한
        super.adminAccessFail(39);

        // 공지사항 삭제
        boardService.deleteNotice(idx);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.board.success.delete"); // 삭제하였습니다.

        return displayJson(true, "1000", sMessage);
    }

    /**
     * 공지사항 알림 전송
     * @param request
     * @return
     */
    @PostMapping("/notices/send")
    public String sendNoticeAlarm(HttpServletRequest request) {

        // 관리자 접근 권한
        super.adminAccessFail(39);

        String json = request.getParameter("json");
        JSONArray array = new JSONArray(json);
        List<Object> list = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonObject =  (JSONObject) array.get(i);
            String idx = jsonObject.get("idx").toString();
            list.add(Integer.parseInt(idx));
        }

        // 공지사항 알림 전송
        boardService.sendNoticeAlarm(list);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.board.success.send"); // 알림을 전송하였습니다.

        return displayJson(true, "1000", sMessage);
    }

    /**
     * 이미지 임시 저장
     *
     * @param image
     * @return
     */
    @PostMapping("/notices/tempImage")
    public ResponseEntity<FileResponseDto> fileUploadFromCKEditor(@RequestPart(value = "upload", required = false) List<MultipartFile> image) {

        // 이미지 s3 임시 저장
        return new ResponseEntity<>(FileResponseDto.builder().
                uploaded(true).
                url(boardService.tempImage(image)).
                build(), HttpStatus.OK);
    }
}
