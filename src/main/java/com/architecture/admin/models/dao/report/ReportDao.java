package com.architecture.admin.models.dao.report;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface ReportDao {

    /**
     * 작품 신고 내역 삭제
     *
     * @param idx
     * @return
     */
    int deleteReportContent(Integer idx);

    /**
     * 작품 댓글 신고 내역 삭제
     *
     * @param idx
     * @return
     */
    int deleteReportContentComment(Integer idx);

    /**
     * 회차 댓글 신고 내역 삭제
     *
     * @param idx
     * @return
     */
    int deleteReportEpisodeComment(Integer idx);
}
