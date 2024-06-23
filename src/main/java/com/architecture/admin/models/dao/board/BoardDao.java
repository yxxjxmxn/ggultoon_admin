package com.architecture.admin.models.dao.board;

import com.architecture.admin.models.dto.board.NoticeDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface BoardDao {

    /**
     * 공지사항 상세 정보 등록
     * @param noticeDto
     * @return
     */
    void registerNoticeInfo(NoticeDto noticeDto);

    /**
     * 공지사항 내용 등록
     * @param noticeDto
     * @return
     */
    int registerNoticeContent(NoticeDto noticeDto);

    /**
     * 공지사항 수정
     * @param noticeDto
     * @return
     */
    int modifyNotice(NoticeDto noticeDto);

    /**
     * 공지사항 삭제
     * @param idx
     * @return
     */
    int deleteNotice(Long idx);
}
