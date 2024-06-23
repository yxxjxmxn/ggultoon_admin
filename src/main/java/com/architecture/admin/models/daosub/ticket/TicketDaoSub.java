package com.architecture.admin.models.daosub.ticket;

import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.ticket.GroupDto;
import com.architecture.admin.models.dto.ticket.TicketDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface TicketDaoSub {

    /**
     * 이용권 지급 대상 그룹 개수 조회
     * @return
     */
    int getTicketGroupTotalCnt(SearchDto searchDto);

    /**
     * 이용권 지급 대상 그룹 리스트 조회
     * @return
     */
    List<GroupDto> getTicketGroupList(SearchDto searchDto);

    /**
     * 이용권 지급 대상 그룹 상세 조회
     * @return
     */
    GroupDto getViewTicketGroup(Long idx);

    /**
     * 지급 예정 및 진행 즁인 이용권 개수 카운트
     * @return
     */
    int getTicketReadyTotalCnt(SearchDto searchDto);

    /**
     * 이용권 지급 예정 및 진행 리스트 조회
     * @return
     */
    List<TicketDto> getTicketReadyList(SearchDto searchDto);

    /**
     * 지급 예정 및 진행 이용권 상세 조회
     * @return
     */
    TicketDto getViewTicketReady(Long idx);

    /**
     * 지급 예정 및 진행 즁인 이용권 개수 카운트
     * @return
     */
    int getTicketCompleteTotalCnt(SearchDto searchDto);

    /**
     * 이용권 지급 완료 리스트 조회
     * @return
     */
    List<TicketDto> getTicketCompleteList(SearchDto searchDto);

    /**
     * 지급 완료 이용권 상세 조회
     * @return
     */
    TicketDto getViewTicketComplete(Long idx);
}
