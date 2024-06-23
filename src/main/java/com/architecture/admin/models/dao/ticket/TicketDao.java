package com.architecture.admin.models.dao.ticket;

import com.architecture.admin.models.dto.ticket.GroupDto;
import com.architecture.admin.models.dto.ticket.TicketDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface TicketDao {

    /**
     * 이용권 지급 대상 그룹 수정
     * @param groupDto
     * @return
     */
    int updateTicketGroup(GroupDto groupDto);

    /**
     * 이용권 상세 정보 등록
     * @param ticketDto
     * @return
     */
    void insertTicket(TicketDto ticketDto);

    /**
     * 이용권 지급 대상 그룹 매핑 정보 등록
     * @param list
     * @return
     */
    int insertTicketGroupList(List<TicketDto> list);

    /**
     * 이용권 지급 대상 그룹 매핑 정보 삭제
     * @param idxList
     * @return
     */
    void deleteTicketGroupList(List<Long> idxList);

    /**
     * 이용권 상세 정보 삭제
     * (1) 단일 건 삭제
     * (2) 복수 건 삭제
     *
     * @param idxList
     * @return
     */
    int deleteTicket(List<Long> idxList);

    /**
     * 이용권 상세 정보 수정
     * @param ticketDto
     * @return
     */
    void updateTicket(TicketDto ticketDto);
}
