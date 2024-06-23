package com.architecture.admin.models.daosub.report;

import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.report.ReportContentCommentDto;
import com.architecture.admin.models.dto.report.ReportContentDto;
import com.architecture.admin.models.dto.report.ReportEpisodeCommentDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ReportDaoSub {

    /**************************************************************************************
     * 작품 신고 내역
     **************************************************************************************/

    /**
     * 작품 신고 내역 전체 카운트
     *
     * @param searchDto
     * @return
     */
    int getReportContentTotalCnt(SearchDto searchDto);

    /**
     * 작품 신고 내역 전체 조회
     *
     * @param searchDto
     * @return
     */
    List<ReportContentDto> getReportContentList(SearchDto searchDto);

    /**
     * 유효한 작품 신고 내역 idx인지 조회
     *
     * @param idx
     * @return
     */
    int getReportContentIdxCnt(Integer idx);

    /**************************************************************************************
     * 작품 신고 내역
     **************************************************************************************/

    /**
     * 작품 댓글 신고 내역 전체 카운트
     *
     * @param searchDto
     * @return
     */
    int getReportContentCommentTotalCnt(SearchDto searchDto);

    /**
     * 작품 댓글 신고 내역 전체 조회
     *
     * @param searchDto
     * @return
     */
    List<ReportContentCommentDto> getReportContentCommentList(SearchDto searchDto);

    /**
     * 유효한 작품 댓글 신고 내역 idx인지 조회
     *
     * @param idx
     * @return
     */
    int getReportContentCommentIdxCnt(Integer idx);

    /**************************************************************************************
     * 회차 댓글 신고 내역
     **************************************************************************************/

    /**
     * 회차 댓글 신고 내역 전체 카운트
     *
     * @param searchDto
     * @return
     */
    int getReportEpisodeCommentTotalCnt(SearchDto searchDto);

    /**
     * 회차 댓글 신고 내역 전체 조회
     *
     * @param searchDto
     * @return
     */
    List<ReportEpisodeCommentDto> getReportEpisodeCommentList(SearchDto searchDto);

    /**
     * 유효한 회차 댓글 신고 내역 idx인지 조회
     *
     * @param idx
     * @return
     */
    int getReportEpisodeCommentIdxCnt(Integer idx);
}
