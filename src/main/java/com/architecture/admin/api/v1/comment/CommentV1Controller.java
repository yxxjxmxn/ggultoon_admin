package com.architecture.admin.api.v1.comment;

import com.architecture.admin.controllers.BaseController;
import com.architecture.admin.libraries.excel.ExcelXlsxView;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.services.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/comments")
public class CommentV1Controller extends BaseController {

    private final CommentService commentService;

    /**************************************************************************************
     * 작품 목록
     **************************************************************************************/

    /**
     * 작품 목록 조회
     * @param searchDto
     * @return
     */
    @GetMapping("/contents")
    public String viewContents(@ModelAttribute SearchDto searchDto){

        // 관리자 접근 권한
        super.adminAccessFail(34);

        // 컨텐츠 목록
        JSONObject joData = commentService.getContentList(searchDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.comment.success.search"); // 검색을 완료하였습니다.

        // 조회 결과가 없는 경우
        if (joData.getJSONArray("list").isEmpty()) {
            sMessage = super.langMessage("lang.comment.exception.search_fail"); // 검색 결과가 없습니다.
        }
        return displayJson(true, "1000", sMessage, joData);
    }

    /**************************************************************************************
     * 회차 목록
     **************************************************************************************/

    /**
     * 회차 목록 조회
     * @param searchDto
     * @param idx
     * @return
     */
    @GetMapping("/contents/{idx}/episodes")
    public String viewEpisodes(@ModelAttribute SearchDto searchDto,
                               @PathVariable(required = false) Integer idx){

        // 관리자 접근 권한
        super.adminAccessFail(34);

        // 작품 idx set
        searchDto.setContentsIdx(idx);

        // 회차 목록
        JSONObject joData = commentService.getEpisodeList(searchDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.comment.success.search"); // 검색을 완료하였습니다.

        // 조회 결과가 없는 경우
        if (joData.getJSONArray("list").isEmpty()) {
            sMessage = super.langMessage("lang.comment.exception.search_fail"); // 검색 결과가 없습니다.
        }
        return displayJson(true, "1000", sMessage, joData);
    }

    /**************************************************************************************
     * 작품 댓글 목록
     **************************************************************************************/

    /**
     * 작품 댓글 목록 조회
     * @param idx
     * @param searchDto
     * @return
     */
    @GetMapping("/contents/{idx}")
    public String viewContentComments(@PathVariable(name = "idx") Integer idx,
                                      @ModelAttribute SearchDto searchDto){

        // 관리자 접근 권한
        super.adminAccessFail(34);

        // 작품 idx set
        searchDto.setContentsIdx(idx);

        // 작품 댓글 목록
        JSONObject joData = commentService.getContentComments(searchDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.comment.success.search"); // 검색을 완료하였습니다.

        // 조회 결과가 없는 경우
        if (joData.getJSONArray("list").isEmpty()) {
            sMessage = super.langMessage("lang.comment.exception.search_fail"); // 검색 결과가 없습니다.
        }
        return displayJson(true, "1000", sMessage, joData);
    }

    /**
     * 작품 댓글 목록 엑셀 다운로드
     * @param idx
     * @param searchDto
     * @return
     */
    @GetMapping("/contents/excel/{idx}")
    public ModelAndView excelContentComments(@PathVariable(name = "idx") Integer idx,
                                       @ModelAttribute SearchDto searchDto){

        // 관리자 접근 권한
        super.adminAccessFail(10);

        // 작품 idx set
        searchDto.setContentsIdx(idx);

        // 검색어 공백제거
        searchDto.setSearchWord(searchDto.getSearchWord().trim());

        // 뷰에 담을 모델객체를 생성(엑셀 파일에 들어갈 데이터)
        Map<String, Object> mExcelData = commentService.excelContentComments(searchDto);

        // AbstractXlsxView 가 동작
        return new ModelAndView(new ExcelXlsxView(), mExcelData);
    }

    /**
     * 작품 댓글 숨김
     * @param idx
     * @param searchDto
     * @return
     */
    @PutMapping("/contents/hide/{idx}")
    public String hideContentComments(@PathVariable(name = "idx") Long idx,
                                        @ModelAttribute SearchDto searchDto){

        // 관리자 접근 권한
        super.adminAccessFail(10);

        // 작품 댓글 숨김 OR 숨김 취소
        int view = commentService.hideContentComments(idx);

        // 결과 메세지 처리
        String sMessage;

        if (view == 1) { // 숨김 취소 완료
            sMessage = super.langMessage("lang.comment.success.show"); // 댓글을 노출하였습니다.
        } else { // 숨김 완료
            sMessage = super.langMessage("lang.comment.success.hide"); // 댓글을 숨겼습니다.
        }
        return displayJson(true, "1000", sMessage);
    }

    /**
     * 작품 댓글 삭제
     * @param idx
     * @param searchDto
     * @return
     */
    @PutMapping("/contents/delete/{idx}")
    public String deleteContentComments(@PathVariable(name = "idx") Long idx,
                                        @ModelAttribute SearchDto searchDto){

        // 관리자 접근 권한
        super.adminAccessFail(10);

        // 작품 댓글 삭제 OR 삭제 취소
        int state = commentService.deleteContentComments(idx);

        // 결과 메세지 처리
        String sMessage;

        if (state == 1) { // 삭제 취소 완료
            sMessage = super.langMessage("lang.comment.success.normal"); // 댓글을 복구하였습니다.
        } else { // 삭제 완료
            sMessage = super.langMessage("lang.comment.success.delete"); // 댓글을 삭제하였습니다.
        }
        return displayJson(true, "1000", sMessage);
    }

    /**************************************************************************************
     * 작품 대댓글 목록
     **************************************************************************************/

    /**
     * 작품 댓글의 대댓글 목록 조회
     * @param idx
     * @param searchDto
     * @return
     */
    @GetMapping("/contents/replies/{idx}")
    public String viewContentReplies(@PathVariable(name = "idx") Long idx,
                                     @ModelAttribute SearchDto searchDto){

        // 관리자 접근 권한
        super.adminAccessFail(10);

        // 댓글 idx set
        searchDto.setIdx(idx);

        // 대댓글 목록
        JSONObject joData = commentService.getContentReplies(searchDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.comment.success.search"); // 검색을 완료하였습니다.

        // 조회 결과가 없는 경우
        if (joData.getJSONArray("list").isEmpty()) {
            sMessage = super.langMessage("lang.comment.exception.search_fail"); // 검색 결과가 없습니다.
        }
        return displayJson(true, "1000", sMessage, joData);
    }

    /**
     * 작품 대댓글 목록 엑셀 다운로드
     * @param idx
     * @param searchDto
     * @return
     */
    @GetMapping("/contents/replies/excel/{idx}")
    public ModelAndView excelContentReplies(@PathVariable(name = "idx") Long idx,
                                            @ModelAttribute SearchDto searchDto){

        // 관리자 접근 권한
        super.adminAccessFail(10);

        // 댓글 idx set
        searchDto.setIdx(idx);

        // 검색어 공백제거
        searchDto.setSearchWord(searchDto.getSearchWord().trim());

        // 뷰에 담을 모델객체를 생성(엑셀 파일에 들어갈 데이터)
        Map<String, Object> mExcelData = commentService.excelContentReplies(searchDto);

        // AbstractXlsxView 가 동작
        return new ModelAndView(new ExcelXlsxView(), mExcelData);
    }

    /**
     * 작품 대댓글 숨김
     * @param idx
     * @param searchDto
     * @return
     */
    @PutMapping("/contents/replies/hide/{idx}")
    public String hideContentReplies(@PathVariable(name = "idx") Long idx,
                                     @ModelAttribute SearchDto searchDto){

        // 관리자 접근 권한
        super.adminAccessFail(10);

        // 작품 대댓글 숨김 OR 숨김 취소
        int view = commentService.hideContentReplies(idx);

        // 결과 메세지 처리
        String sMessage;

        if (view == 1) { // 숨김 취소 완료
            sMessage = super.langMessage("lang.comment.success.show"); // 댓글을 노출하였습니다.
        } else { // 숨김 완료
            sMessage = super.langMessage("lang.comment.success.hide"); // 댓글을 숨겼습니다.
        }
        return displayJson(true, "1000", sMessage);
    }

    /**
     * 작품 대댓글 삭제
     * @param idx
     * @param searchDto
     * @return
     */
    @PutMapping("/contents/replies/delete/{idx}")
    public String deleteContentReplies(@PathVariable(name = "idx") Long idx,
                                       @ModelAttribute SearchDto searchDto){

        // 관리자 접근 권한
        super.adminAccessFail(10);

        // 작품 대댓글 삭제 OR 삭제 취소
        int state = commentService.deleteContentReplies(idx);

        // 결과 메세지 처리
        String sMessage;

        if (state == 1) { // 삭제 취소 완료
            sMessage = super.langMessage("lang.comment.success.normal"); // 댓글을 복구하였습니다.
        } else { // 삭제 완료
            sMessage = super.langMessage("lang.comment.success.delete"); // 댓글을 삭제하였습니다.
        }
        return displayJson(true, "1000", sMessage);
    }

    /**************************************************************************************
     * 회차 댓글 목록
     **************************************************************************************/

    /**
     * 회차 댓글 목록 조회
     * @param contentsIdx
     * @param episodeIdx
     * @param searchDto
     * @return
     */
    @GetMapping("/contents/{contentsIdx}/episodes/{episodeIdx}")
    public String viewEpisodeComments(@PathVariable(name = "contentsIdx") Integer contentsIdx,
                                      @PathVariable(name = "episodeIdx") Long episodeIdx,
                                      @ModelAttribute SearchDto searchDto){

        // 관리자 접근 권한
        super.adminAccessFail(10);

        // searchDto set
        searchDto.setContentsIdx(contentsIdx); // 작품 idx set
        searchDto.setIdx(episodeIdx); // 회차 idx set

        // 회차 댓글 목록
        JSONObject joData = commentService.getEpisodeComments(searchDto);

        // 결과 메세지 처리
        String sMessage = super.langMessage("lang.comment.success.search"); // 검색을 완료하였습니다.

        // 조회 결과가 없는 경우
        if (joData.getJSONArray("list").isEmpty()) {
            sMessage = super.langMessage("lang.comment.exception.search_fail"); // 검색 결과가 없습니다.
        }
        return displayJson(true, "1000", sMessage, joData);
    }

    /**
     * 회차 댓글 목록 엑셀 다운로드
     * @param idx
     * @param searchDto
     * @return
     */
    @GetMapping("/episodes/excel/{idx}")
    public ModelAndView excelEpisodeComments(@PathVariable(name = "idx") Long idx,
                                             @ModelAttribute SearchDto searchDto){

        // 관리자 접근 권한
        super.adminAccessFail(10);

        // 회차 idx set
        searchDto.setIdx(idx);

        // 검색어 공백제거
        searchDto.setSearchWord(searchDto.getSearchWord().trim());

        // 뷰에 담을 모델객체를 생성(엑셀 파일에 들어갈 데이터)
        Map<String, Object> mExcelData = commentService.excelEpisodeComments(searchDto);

        // AbstractXlsxView 가 동작
        return new ModelAndView(new ExcelXlsxView(), mExcelData);
    }

    /**
     * 회차 댓글 숨김
     * @param idx
     * @param searchDto
     * @return
     */
    @PutMapping("/episodes/hide/{idx}")
    public String hideEpisodeComments(@PathVariable(name = "idx") Long idx,
                                      @ModelAttribute SearchDto searchDto){

        // 관리자 접근 권한
        super.adminAccessFail(10);

        // 회차 댓글 숨김 OR 숨김 취소
        int view = commentService.hideEpisodeComments(idx);

        // 결과 메세지 처리
        String sMessage;

        if (view == 1) { // 숨김 취소 완료
            sMessage = super.langMessage("lang.comment.success.show"); // 댓글을 노출하였습니다.
        } else { // 숨김 완료
            sMessage = super.langMessage("lang.comment.success.hide"); // 댓글을 숨겼습니다.
        }
        return displayJson(true, "1000", sMessage);
    }

    /**
     * 회차 댓글 삭제
     * @param idx
     * @param searchDto
     * @return
     */
    @PutMapping("/episodes/delete/{idx}")
    public String deleteEpisodeComments(@PathVariable(name = "idx") Long idx,
                                        @ModelAttribute SearchDto searchDto){

        // 관리자 접근 권한
        super.adminAccessFail(10);

        // 회차 댓글 삭제 OR 삭제 취소
        int state = commentService.deleteEpisodeComments(idx);

        // 결과 메세지 처리
        String sMessage;

        if (state == 1) { // 삭제 취소 완료
            sMessage = super.langMessage("lang.comment.success.normal"); // 댓글을 복구하였습니다.
        } else { // 삭제 완료
            sMessage = super.langMessage("lang.comment.success.delete"); // 댓글을 삭제하였습니다.
        }
        return displayJson(true, "1000", sMessage);
    }
}
