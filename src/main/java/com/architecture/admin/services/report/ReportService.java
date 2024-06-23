package com.architecture.admin.services.report;

import com.architecture.admin.libraries.PaginationLibray;
import com.architecture.admin.libraries.excel.ExcelData;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dao.report.ReportDao;
import com.architecture.admin.models.daosub.report.ReportDaoSub;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.report.ReportContentCommentDto;
import com.architecture.admin.models.dto.report.ReportContentDto;
import com.architecture.admin.models.dto.report.ReportEpisodeCommentDto;
import com.architecture.admin.services.BaseService;
import com.architecture.admin.services.log.AdminActionLogService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor
@Service
@Transactional
public class ReportService extends BaseService {

    private final ReportDao reportDao;
    private final ReportDaoSub reportDaoSub;
    private final ExcelData excelData;
    private final AdminActionLogService adminActionLogService;// 관리자 action log 처리용

    /**************************************************************************************
     * 작품 신고 내역
     **************************************************************************************/

    /**
     * 작품 신고 내역 엑셀 다운로드
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public Map<String, Object> excelReportContent(SearchDto searchDto) {

        // 검색어 공백제거
        if (!searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // 내보낼 데이터 set
        List<ReportContentDto> reportContentList = null;

        // 작품 신고 내역 개수 카운트
        int totalCnt = reportDaoSub.getReportContentTotalCnt(searchDto);

        // 작품 신고 내역이 있는 경우
        if (totalCnt > 0) {

            // 작품 신고 내역 조회
            reportContentList = reportDaoSub.getReportContentList(searchDto);

            // 작품 신고 내역 상태값 문자변환
            reportContentStateText(reportContentList);
        }
        // 엑셀 데이터 생성
        return excelData.createExcelData(reportContentList, ReportContentDto.class);
    }

    /**
     * 작품 신고 내역 조회
     *
     * @param searchDto
     */
    @Transactional(readOnly = true)
    public JSONObject getReportContentList(SearchDto searchDto) {

        // 검색어 공백제거
        if (!searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // 내보낼 데이터 set
        JSONObject joData = new JSONObject();
        List<ReportContentDto> reportContentList = null;

        // 작품 신고 내역 개수 카운트
        int totalCnt = reportDaoSub.getReportContentTotalCnt(searchDto);

        // 작품 신고 내역이 있는 경우
        if (totalCnt > 0) {

            // recordSize set
            Integer recordSize = searchDto.getSearchCount();
            if (recordSize != null && recordSize > 0) {
                searchDto.setRecordSize(recordSize);
            }

            // paging
            PaginationLibray pagination = new PaginationLibray(totalCnt, searchDto);
            searchDto.setPagination(pagination);

            // 작품 신고 내역 조회
            reportContentList = reportDaoSub.getReportContentList(searchDto);

            // 작품 신고 내역 상태값 문자변환
            reportContentStateText(reportContentList);

            // paging 담기
            joData.put("params", new JSONObject(searchDto));
        }

        // list 담기
        joData.put("list", reportContentList);

        return joData;
    }

    /**
     * 작품 신고 내역 삭제
     *
     * @param idx
     */
    public void deleteReportContent(Integer idx) {

        // 작품 신고 내역 idx 유효성 검사
        isReportContentIdxValidate(idx);

        // 작품 신고 내역 삭제
        int result = reportDao.deleteReportContent(idx);

        // 삭제 실패 시
        if (result < 1) {
            throw new CustomException(CustomError.REPORT_CONTENTS_DELETE_ERROR); // 작품 신고 내역 삭제를 실패하였습니다.
        }

        // 관리자 action log
        adminActionLogService.regist(idx, Thread.currentThread().getStackTrace());
    }

    /**************************************************************************************
     * 작품 댓글 신고 내역
     **************************************************************************************/

    /**
     * 작품 댓글 신고 내역 엑셀 다운로드
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public Map<String, Object> excelReportContentComment(SearchDto searchDto) {

        // 검색어 공백제거
        if (!searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // 내보낼 데이터 set
        List<ReportContentCommentDto> reportContentCommentList = null;

        // 작품 댓글 신고 내역 개수 카운트
        int totalCnt = reportDaoSub.getReportContentCommentTotalCnt(searchDto);

        // 작품 댓글 신고 내역이 있는 경우
        if (totalCnt > 0) {

            // 작품 댓글 신고 내역 조회
            reportContentCommentList = reportDaoSub.getReportContentCommentList(searchDto);

            // 작품 댓글 신고 내역 상태값 문자변환
            reportContentCommentStateText(reportContentCommentList);
        }
        // 엑셀 데이터 생성
        return excelData.createExcelData(reportContentCommentList, ReportContentCommentDto.class);
    }

    /**
     * 작품 댓글 신고 내역 조회
     *
     * @param searchDto
     */
    @Transactional(readOnly = true)
    public JSONObject getReportContentCommentList(SearchDto searchDto) {

        // 검색어 공백제거
        if (!searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // 내보낼 데이터 set
        JSONObject joData = new JSONObject();
        List<ReportContentCommentDto> reportContentCommentList = null;

        // 작품 댓글 신고 내역 개수 카운트
        int totalCnt = reportDaoSub.getReportContentCommentTotalCnt(searchDto);

        // 작품 댓글 신고 내역이 있는 경우
        if (totalCnt > 0) {

            // recordSize set
            Integer recordSize = searchDto.getSearchCount();
            if (recordSize != null && recordSize > 0) {
                searchDto.setRecordSize(recordSize);
            }

            // paging
            PaginationLibray pagination = new PaginationLibray(totalCnt, searchDto);
            searchDto.setPagination(pagination);

            // 작품 댓글 신고 내역 조회
            reportContentCommentList = reportDaoSub.getReportContentCommentList(searchDto);

            // 작품 댓글 신고 내역 상태값 문자변환
            reportContentCommentStateText(reportContentCommentList);

            // paging 담기
            joData.put("params", new JSONObject(searchDto));
        }

        // list 담기
        joData.put("list", reportContentCommentList);

        return joData;
    }

    /**
     * 작품 댓글 신고 내역 삭제
     *
     * @param idx
     */
    public void deleteReportContentComment(Integer idx) {

        // 작품 댓글 신고 내역 idx 유효성 검사
        isReportContentCommentIdxValidate(idx);

        // 작품 댓글 신고 내역 삭제
        int result = reportDao.deleteReportContentComment(idx);

        // 삭제 실패 시
        if (result < 1) {
            throw new CustomException(CustomError.REPORT_CONTENTS_COMMENT_DELETE_ERROR); // 작품 댓글 신고 내역 삭제를 실패하였습니다.
        }

        // 관리자 action log
        adminActionLogService.regist(idx, Thread.currentThread().getStackTrace());
    }

    /**************************************************************************************
     * 회차 댓글 신고 내역
     **************************************************************************************/

    /**
     * 회차 댓글 신고 내역 엑셀 다운로드
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public Map<String, Object> excelReportEpisodeComment(SearchDto searchDto) {

        // 검색어 공백제거
        if (!searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // 내보낼 데이터 set
        List<ReportEpisodeCommentDto> reportEpisodeCommentList = null;

        // 회차 댓글 신고 내역 개수 카운트
        int totalCnt = reportDaoSub.getReportEpisodeCommentTotalCnt(searchDto);

        // 회차 댓글 신고 내역이 있는 경우
        if (totalCnt > 0) {

            // 회차 댓글 신고 내역 조회
            reportEpisodeCommentList = reportDaoSub.getReportEpisodeCommentList(searchDto);

            // 회차 댓글 신고 내역 상태값 문자변환
            reportEpisodeCommentStateText(reportEpisodeCommentList);
        }
        // 엑셀 데이터 생성
        return excelData.createExcelData(reportEpisodeCommentList, ReportEpisodeCommentDto.class);
    }

    /**
     * 회차 댓글 신고 내역 조회
     *
     * @param searchDto
     */
    @Transactional(readOnly = true)
    public JSONObject getReportEpisodeCommentList(SearchDto searchDto) {

        // 검색어 공백제거
        if (!searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // 내보낼 데이터 set
        JSONObject joData = new JSONObject();
        List<ReportEpisodeCommentDto> reportEpisodeCommentList = null;

        // 회차 댓글 신고 내역 개수 카운트
        int totalCnt = reportDaoSub.getReportEpisodeCommentTotalCnt(searchDto);

        // 회차 댓글 신고 내역이 있는 경우
        if (totalCnt > 0) {

            // recordSize set
            Integer recordSize = searchDto.getSearchCount();
            if (recordSize != null && recordSize > 0) {
                searchDto.setRecordSize(recordSize);
            }

            // paging
            PaginationLibray pagination = new PaginationLibray(totalCnt, searchDto);
            searchDto.setPagination(pagination);

            // 회차 댓글 신고 내역 조회
            reportEpisodeCommentList = reportDaoSub.getReportEpisodeCommentList(searchDto);

            // 회차 댓글 신고 내역 상태값 문자변환
            reportEpisodeCommentStateText(reportEpisodeCommentList);

            // paging 담기
            joData.put("params", new JSONObject(searchDto));
        }

        // list 담기
        joData.put("list", reportEpisodeCommentList);

        return joData;
    }

    /**
     * 회차 댓글 신고 내역 삭제
     *
     * @param idx
     */
    public void deleteReportEpisodeComment(Integer idx) {

        // 회차 댓글 신고 내역 idx 유효성 검사
        isReportEpisodeCommentIdxValidate(idx);

        // 회차 댓글 신고 내역 삭제
        int result = reportDao.deleteReportEpisodeComment(idx);

        // 삭제 실패 시
        if (result < 1) {
            throw new CustomException(CustomError.REPORT_EPISODE_COMMENT_DELETE_ERROR); // 회차 댓글 신고 내역 삭제를 실패하였습니다.
        }

        // 관리자 action log
        adminActionLogService.regist(idx, Thread.currentThread().getStackTrace());
    }


    /*************************************************************
     * 문자변환
     *************************************************************/

    /**
     * 작품 신고 내역
     * 문자변환 list
     */
    private void reportContentStateText(List<ReportContentDto> list) {
        for (ReportContentDto dto : list) {
            reportContentStateText(dto);
        }
    }

    /**
     * 작품 신고 내역
     * 문자변환 dto
     */
    private void reportContentStateText(ReportContentDto dto)  {
        if (dto.getState() != null) {
            if (dto.getState() == 2) {
                // 취소
                dto.setStateText(super.langMessage("lang.report.content.state.cancel"));
                dto.setStateBg("badge-danger");
            } else if (dto.getState() == 1) {
                // 정상
                dto.setStateText(super.langMessage("lang.report.content.state.normal"));
                dto.setStateBg("badge-success");
            }
        }
        if (dto.getStatus() != null) {
            if (dto.getStatus() == 0) {
                // 중지
                dto.setStatusText(super.langMessage("lang.report.content.status.stop"));
                dto.setStatusBg("badge-warning");
            } else if (dto.getStatus() == 1) {
                // 정상
                dto.setStatusText(super.langMessage("lang.report.content.status.normal"));
                dto.setStatusBg("badge-success");
            }  else if (dto.getStatus() == 2) {
                // 승인대기
                dto.setStatusText(super.langMessage("lang.report.content.status.wait"));
                dto.setStatusBg("badge-secondary");
            }  else if (dto.getStatus() == 3) {
                // 승인거절
                dto.setStatusText(super.langMessage("lang.report.content.status.refuse"));
                dto.setStatusBg("badge-danger");
            }
        }
    }

    /**
     * 작품 댓글 신고 내역
     * 문자변환 list
     */
    private void reportContentCommentStateText(List<ReportContentCommentDto> list) {
        for (ReportContentCommentDto dto : list) {
            reportContentCommentStateText(dto);
        }
    }

    /**
     * 작품 댓글 신고 내역
     * 문자변환 dto
     */
    private void reportContentCommentStateText(ReportContentCommentDto dto)  {
        if (dto.getState() != null) {
            if (dto.getState() == 2) {
                // 취소
                dto.setStateText(super.langMessage("lang.report.content.comment.state.cancel"));
                dto.setStateBg("badge-danger");
            } else if (dto.getState() == 1) {
                // 정상
                dto.setStateText(super.langMessage("lang.report.content.comment.state.normal"));
                dto.setStateBg("badge-success");
            }
        }
        if (dto.getType() != null) {
            if (dto.getType() == 0) {
                // 댓글
                dto.setTypeText(super.langMessage("lang.report.content.comment.comment"));
                dto.setTypeBg("badge-warning");
            } else {
                // 대댓글
                dto.setTypeText(super.langMessage("lang.report.content.comment.reply"));
                dto.setTypeBg("badge-secondary");
            }
        }
        if (dto.getStatus() != null) {
            if (dto.getStatus() == 0) {
                // 삭제
                dto.setStatusText(super.langMessage("lang.report.content.comment.status.delete"));
                dto.setStatusBg("badge-danger");
            } else if (dto.getStatus() == 1) {
                // 정상
                dto.setStatusText(super.langMessage("lang.report.content.comment.status.normal"));
                dto.setStatusBg("badge-success");
            }
        }
        if (dto.getView() != null) {
            if (dto.getView() == 0) {
                // 비노출
                dto.setViewText(super.langMessage("lang.report.content.comment.view.false"));
                dto.setViewBg("badge-danger");
            } else if (dto.getView() == 1) {
                // 노출
                dto.setViewText(super.langMessage("lang.report.content.comment.view.true"));
                dto.setViewBg("badge-success");
            }
        }
    }

    /**
     * 회차 댓글 신고 내역
     * 문자변환 list
     */
    private void reportEpisodeCommentStateText(List<ReportEpisodeCommentDto> list) {
        for (ReportEpisodeCommentDto dto : list) {
            reportEpisodeCommentStateText(dto);
        }
    }

    /**
     * 회차 댓글 신고 내역
     * 문자변환 dto
     */
    private void reportEpisodeCommentStateText(ReportEpisodeCommentDto dto)  {
        if (dto.getState() != null) {
            if (dto.getState() == 2) {
                // 취소
                dto.setStateText(super.langMessage("lang.report.episode.comment.state.cancel"));
                dto.setStateBg("badge-danger");
            } else if (dto.getState() == 1) {
                // 정상
                dto.setStateText(super.langMessage("lang.report.episode.comment.state.normal"));
                dto.setStateBg("badge-success");
            }
        }
        if (dto.getType() != null) {
            if (dto.getType() == 0) {
                // 댓글
                dto.setTypeText(super.langMessage("lang.report.episode.comment.comment"));
                dto.setTypeBg("badge-warning");
            } else {
                // 대댓글
                dto.setTypeText(super.langMessage("lang.report.episode.comment.reply"));
                dto.setTypeBg("badge-secondary");
            }
        }
        if (dto.getStatus() != null) {
            if (dto.getStatus() == 0) {
                // 삭제
                dto.setStatusText(super.langMessage("lang.report.episode.comment.status.delete"));
                dto.setStatusBg("badge-danger");
            } else if (dto.getStatus() == 1) {
                // 정상
                dto.setStatusText(super.langMessage("lang.report.episode.comment.status.normal"));
                dto.setStatusBg("badge-success");
            }
        }
        if (dto.getView() != null) {
            if (dto.getView() == 0) {
                // 비노출
                dto.setViewText(super.langMessage("lang.report.episode.comment.view.false"));
                dto.setViewBg("badge-danger");
            } else if (dto.getView() == 1) {
                // 노출
                dto.setViewText(super.langMessage("lang.report.episode.comment.view.true"));
                dto.setViewBg("badge-success");
            }
        }
    }

    /*************************************************************
     * Validation
     *************************************************************/
    
    /**
     * 선택한 작품 신고 내역 idx 유효성 검사
     *
     * @param idx
     */
    private void isReportContentIdxValidate(Integer idx) {

        // 작품 신고 내역 idx가 없는 경우
        if (idx == null || idx < 1) {
            throw new CustomException(CustomError.REPORT_CONTENTS_IDX_EMPTY); // 요청하신 작품 신고 내역을 찾을 수 없습니다.
        }

        // 작품 신고 내역 idx가 유효하지 않은 경우
        int totalCnt = reportDaoSub.getReportContentIdxCnt(idx);
        if (totalCnt < 1) {
            throw new CustomException(CustomError.REPORT_CONTENTS_IDX_ERROR); // 요청하신 작품 신고 내역을 찾을 수 없습니다.
        }
    }

    /**
     * 선택한 작품 댓글 신고 내역 idx 유효성 검사
     *
     * @param idx
     */
    private void isReportContentCommentIdxValidate(Integer idx) {

        // 작품 댓글 신고 내역 idx가 없는 경우
        if (idx == null || idx < 1) {
            throw new CustomException(CustomError.REPORT_CONTENTS_COMMENT_IDX_EMPTY); // 요청하신 작품 댓글 신고 내역을 찾을 수 없습니다.
        }

        // 작품 댓글 신고 내역 idx가 유효하지 않은 경우
        int totalCnt = reportDaoSub.getReportContentCommentIdxCnt(idx);
        if (totalCnt < 1) {
            throw new CustomException(CustomError.REPORT_CONTENTS_COMMENT_IDX_ERROR); // 요청하신 작품 댓글 신고 내역을 찾을 수 없습니다.
        }
    }

    /**
     * 선택한 회차 댓글 신고 내역 idx 유효성 검사
     *
     * @param idx
     */
    private void isReportEpisodeCommentIdxValidate(Integer idx) {

        // 회차 댓글 신고 내역 idx가 없는 경우
        if (idx == null || idx < 1) {
            throw new CustomException(CustomError.REPORT_EPISODE_COMMENT_IDX_EMPTY); // 요청하신 회차 댓글 신고 내역을 찾을 수 없습니다.
        }

        // 회차 댓글 신고 내역 idx가 유효하지 않은 경우
        int totalCnt = reportDaoSub.getReportEpisodeCommentIdxCnt(idx);
        if (totalCnt < 1) {
            throw new CustomException(CustomError.REPORT_EPISODE_COMMENT_IDX_ERROR); // 요청하신 회차 댓글 신고 내역을 찾을 수 없습니다.
        }
    }
}
