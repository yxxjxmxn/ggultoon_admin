package com.architecture.admin.models.daosub.board;

import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.board.NoticeDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface BoardDaoSub {

    /**
     * 공지사항 개수 전체 카운트
     */
    int getNoticeTotalCnt(SearchDto searchDto);

    /**
     * 공지사항 리스트 조회
     */
    List<NoticeDto> getNoticeList(SearchDto searchDto);

    /**
     * 공지사항 상세
     */
    NoticeDto getViewNotice(Long idx);

    /**
     * 알림 전송할 공지사항 등록일 리스트 조회
     */
    List<NoticeDto> getNoticeRegdateList(List<Long> idxList);
}
