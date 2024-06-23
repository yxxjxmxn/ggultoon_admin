package com.architecture.admin.services.ticket;

import com.architecture.admin.libraries.PaginationLibray;
import com.architecture.admin.libraries.excel.ExcelData;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dao.ticket.TicketDao;
import com.architecture.admin.models.daosub.contents.EpisodeDaoSub;
import com.architecture.admin.models.daosub.ticket.TicketDaoSub;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.ticket.GroupDto;
import com.architecture.admin.models.dto.ticket.TicketDto;
import com.architecture.admin.services.BaseService;
import com.architecture.admin.services.log.AdminActionLogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Transactional
public class TicketService extends BaseService {

    private final TicketDaoSub ticketDaoSub;
    private final TicketDao ticketDao;
    private final EpisodeDaoSub episodeDaoSub;
    private final ExcelData excelData;
    private final AdminActionLogService adminActionLogService;// 관리자 action log 처리용

    /**************************************************************************************
     * 이용권 지급 대상 그룹 리스트
     **************************************************************************************/

    /**
     * 이용권 지급 대상 그룹 리스트 조회
     *
     * @param searchDto
     */
    @Transactional(readOnly = true)
    public JSONObject getTicketGroupList(SearchDto searchDto) {

        // 검색어 공백제거
        if (searchDto.getSearchWord() != null && !searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // 내보낼 데이터 set
        JSONObject joData = new JSONObject();
        List<GroupDto> ticketGroupList = null;

        // 그룹 개수 카운트
        int totalCnt = ticketDaoSub.getTicketGroupTotalCnt(searchDto);

        // 그룹이 있는 경우
        if (totalCnt > 0) {

            // paging
            PaginationLibray pagination = new PaginationLibray(totalCnt, searchDto);
            searchDto.setPagination(pagination);

            // 그룹 리스트 조회
            ticketGroupList = ticketDaoSub.getTicketGroupList(searchDto);

            // 상태값 문자변환
            convertGroupText(ticketGroupList);

            // paging 담기
            joData.put("params", new JSONObject(searchDto));
        }

        // list 담기
        joData.put("list", ticketGroupList);

        return joData;
    }

    /**
     * 이용권 지급 대상 그룹 상세
     *
     * @param idx
     */
    @Transactional(readOnly = true)
    public JSONObject getViewTicketGroup(Long idx) {


        // 그룹 idx 유효성 검사
        isTicketGroupIdxValidate(idx);

        // 그룹 상세 조회
        GroupDto viewTicketGroup = ticketDaoSub.getViewTicketGroup(idx);

        if (viewTicketGroup != null) {
            // 그룹 상태값 문자변환
            convertGroupText(viewTicketGroup);
        }

        // 내보낼 데이터 set
        JSONObject joData = new JSONObject(viewTicketGroup);

        return joData;
    }

    /**
     * 이용권 지급 대상 그룹 수정
     *
     * @param groupDto
     */
    @Transactional
    public void updateTicketGroup(GroupDto groupDto) {

        // 수정할 그룹 유효성 검사
        isTicketGroupDataValidate(groupDto);

        // 그룹 수정
        groupDto.setRegdate(dateLibrary.getDatetime()); // 수정일 set
        int result = ticketDao.updateTicketGroup(groupDto);

        // 수정 실패 시
        if (result < 1) {
            throw new CustomException(CustomError.TICKET_GROUP_MODIFY_ERROR); // 그룹 수정을 실패하였습니다.
        }

        // 관리자 action log
        adminActionLogService.regist(groupDto, Thread.currentThread().getStackTrace());
    }

    /**************************************************************************************
     * 이용권 지급 예정 및 진행 리스트
     **************************************************************************************/

    /**
     * 이용권 지급 예정 및 진행 리스트 조회
     *
     * @param searchDto
     */
    @Transactional(readOnly = true)
    public JSONObject getTicketReadyList(SearchDto searchDto) {

        // 검색어 공백제거
        if (searchDto.getSearchWord() != null && !searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // 검색 기간 시분초 세팅
        setSearchDateTime(searchDto);

        // nowDate set
        searchDto.setNowDate(dateLibrary.getDatetime());

        // 내보낼 데이터 set
        JSONObject joData = new JSONObject();
        List<TicketDto> ticketReadyList = null;

        // 지급 예정 및 진행 즁인 이용권 개수 카운트
        int totalCnt = ticketDaoSub.getTicketReadyTotalCnt(searchDto);

        // 이용권이 있는 경우
        if (totalCnt > 0) {

            // recordSize set
            Integer recordSize = searchDto.getSearchCount();
            if (recordSize != null && recordSize > 0) {
                searchDto.setRecordSize(recordSize);
            }

            // paging
            PaginationLibray pagination = new PaginationLibray(totalCnt, searchDto);
            searchDto.setPagination(pagination);

            // 지급 예정 및 진행 리스트 조회
            ticketReadyList = ticketDaoSub.getTicketReadyList(searchDto);

            // 상태값 문자변환
            convertTicketText(ticketReadyList);

            // paging 담기
            joData.put("params", new JSONObject(searchDto));
        }

        // list 담기
        joData.put("list", ticketReadyList);

        return joData;
    }

    /**
     * 지급 예정 및 진행 이용권 상세
     *
     * @param idx
     */
    @Transactional(readOnly = true)
    public JSONObject getViewTicketReady(Long idx) {

        // 이용권 idx 유효성 검사
        isTicketIdxValidate("view", idx);

        // 이용권 상세 조회
        TicketDto viewTicketReady = ticketDaoSub.getViewTicketReady(idx);

        if (viewTicketReady != null) {
            // 이용권 상태값 문자변환
            convertTicketText(viewTicketReady);
        }

        // 내보낼 데이터 set
        JSONObject joData = new JSONObject(viewTicketReady);

        return joData;
    }

    /**
     * 이용권 등록
     *
     * @param ticketDto
     */
    @Transactional
    public void insertTicket(TicketDto ticketDto) {

        // 등록할 이용권 유효성 검사
        isTicketDataValidate(ticketDto);

        // 상태값 및 등록일 set
        ticketDto.setState(1);
        ticketDto.setRegdate(dateLibrary.getDatetime());

        // 이용권 상세 정보 등록 - contents_ticket
        ticketDao.insertTicket(ticketDto);

        // 이용권 지급 대상 그룹 매핑 정보 set
        List<TicketDto> list = new ArrayList<>();
        String[] strCode = ticketDto.getCode().split(",");
        for (String code : strCode) {
            // dto set
            TicketDto dto = TicketDto.builder()
                    .idx(ticketDto.getInsertedIdx())
                    .groupIdx(Long.parseLong(code)) // String to Long
                    .state(1)
                    .regdate(ticketDto.getRegdate())
                    .build();

            // list set
            list.add(dto);
        }
        // 이용권 지급 대상 그룹 매핑 정보 등록 - contents_ticket_group_mapping
        int result = ticketDao.insertTicketGroupList(list);

        // 등록 실패 시
        if (result < 1) {
            throw new CustomException(CustomError.TICKET_REGISTER_ERROR); // 이용권 등록을 실패하였습니다.
        }

        // 관리자 action log
        adminActionLogService.regist(ticketDto, Thread.currentThread().getStackTrace());
    }

    /**
     * 이용권 삭제
     *
     * (1) 단일 건 삭제
     * (2) 복수 건 삭제
     * @param searchDto
     */
    @Transactional
    public void deleteTicket(SearchDto searchDto) throws JsonProcessingException {

        // Json String -> List 변환
        List<String> list = new ObjectMapper().readValue(searchDto.getList(), new TypeReference<>(){});
        List<Long> idxList = new ArrayList<>();
        for (String idx : list) {
            // String to Long
            idxList.add(Long.parseLong(idx));
        }

        // 이용권 idx 유효성 검사
        isTicketIdxValidate("delete", idxList);

        // 이용권 지급 대상 그룹 매핑 정보 삭제
        ticketDao.deleteTicketGroupList(idxList);

        // 이용권 삭제
        int result = ticketDao.deleteTicket(idxList);

        // 삭제 실패 시
        if (result < 1) {
            throw new CustomException(CustomError.TICKET_DELETE_ERROR); // 이용권 삭제를 실패하였습니다.
        }

        // 관리자 action log
        adminActionLogService.regist(idxList, Thread.currentThread().getStackTrace());
    }

    /**
     * 이용권 수정
     *
     * @param ticketDto
     */
    @Transactional
    public void updateTicket(TicketDto ticketDto) {

        // 수정할 데이터 유효성 검사
        isTicketDataValidate(ticketDto);

        // 이용권 상세 정보 수정
        ticketDao.updateTicket(ticketDto);

        // 기존에 매핑된 지급 대상 그룹 매핑 정보 삭제
        List<Long> idxList = new ArrayList<>();
        idxList.add(ticketDto.getIdx());
        ticketDao.deleteTicketGroupList(idxList);

        // 수정된 지급 대상 그룹으로 다시 매핑
        List<TicketDto> list = new ArrayList<>();
        String[] strArea = ticketDto.getCode().split(",");
        for (String code : strArea) {
            // dto set
            TicketDto dto = TicketDto.builder()
                    .idx(ticketDto.getIdx())
                    .groupIdx(Long.parseLong(code)) // String to Long
                    .state(1)
                    .regdate(dateLibrary.getDatetime())
                    .build();

            // list set
            list.add(dto);
        }
        int result = ticketDao.insertTicketGroupList(list);

        // 수정 실패 시
        if (result < 1) {
            throw new CustomException(CustomError.TICKET_MODIFY_ERROR); // 이용권 수정을 실패하였습니다.
        }

        // 관리자 action log
        adminActionLogService.regist(ticketDto, Thread.currentThread().getStackTrace());
    }

    /**************************************************************************************
     * 이용권 지급 완료 리스트
     **************************************************************************************/

    /**
     * 이용권 지급 완료 리스트 조회
     *
     * @param searchDto
     */
    @Transactional(readOnly = true)
    public JSONObject getTicketCompleteList(SearchDto searchDto) {

        // 검색어 공백제거
        if (searchDto.getSearchWord() != null && !searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // 검색 기간 시분초 세팅
        setSearchDateTime(searchDto);

        // nowDate set
        searchDto.setNowDate(dateLibrary.getDatetime());

        // 내보낼 데이터 set
        JSONObject joData = new JSONObject();
        List<TicketDto> ticketCompleteList = null;

        // 지급 완료 이용권 개수 카운트
        int totalCnt = ticketDaoSub.getTicketCompleteTotalCnt(searchDto);

        // 지급 완료 이용권이 있는 경우
        if (totalCnt > 0) {

            // recordSize set
            Integer recordSize = searchDto.getSearchCount();
            if (recordSize != null && recordSize > 0) {
                searchDto.setRecordSize(recordSize);
            }

            // paging
            PaginationLibray pagination = new PaginationLibray(totalCnt, searchDto);
            searchDto.setPagination(pagination);

            // 지급 완료 리스트 조회
            ticketCompleteList = ticketDaoSub.getTicketCompleteList(searchDto);

            // 상태값 문자변환
            convertTicketText(ticketCompleteList);

            // paging 담기
            joData.put("params", new JSONObject(searchDto));
        }

        // list 담기
        joData.put("list", ticketCompleteList);

        return joData;
    }

    /**
     * 지급 완료 이용권 상세
     *
     * @param idx
     */
    @Transactional(readOnly = true)
    public JSONObject getViewTicketComplete(Long idx) {

        // 이용권 idx 유효성 검사
        isTicketIdxValidate("view", idx);

        // 이용권 상세 조회
        TicketDto viewTicketComplete = ticketDaoSub.getViewTicketComplete(idx);

        if (viewTicketComplete != null) {
            // 이용권 상태값 문자변환
            convertTicketText(viewTicketComplete);
        }

        // 내보낼 데이터 set
        JSONObject joData = new JSONObject(viewTicketComplete);

        return joData;
    }

    /**
     * 지급 완료 이용권 리스트 엑셀 다운로드
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public Map<String, Object> excelTicketComplete(SearchDto searchDto) {

        // 검색어 공백제거
        if (!searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // nowDate set
        searchDto.setNowDate(dateLibrary.getDatetime());

        // send data set
        List<TicketDto> ticketCompleteList = null;

        // 지급 완료 이용권 개수 카운트
        int totalCnt = ticketDaoSub.getTicketCompleteTotalCnt(searchDto);

        // 지급 완료 이용권이 있는 경우
        if (totalCnt > 0) {

            // 지급 완료 리스트 조회
            ticketCompleteList = ticketDaoSub.getTicketCompleteList(searchDto);

            // 상태값 문자변환
            convertTicketText(ticketCompleteList);
        }
        // 엑셀 데이터 생성
        return excelData.createExcelData(ticketCompleteList, TicketDto.class);
    }

    /*************************************************************
     * SUB
     *************************************************************/
    /**
     * 기간 검색 시 데이터 누락 방지용 - 검색 기간 시작일 및 종료일 시분초 세팅
     * 시작일 : 00시 00분 00초
     * 종료일 : 23시 59분 59초
     */
    private void setSearchDateTime(SearchDto searchDto) {

        // 검색 기간 시작일
        String startDate = searchDto.getSearchStartDate();
        if (startDate != null && !startDate.isEmpty()) {
            // 검색 기간 시작일 시분초 추가 -> 해당 일자의 00시 00분 00초부터 검색하도록 설정
            searchDto.setSearchStartDate(startDate + " 00:00:00");
        }

        // 검색 기간 종료일
        String endDate = searchDto.getSearchEndDate();
        if (endDate != null && !endDate.isEmpty()) {
            // 검색 기간 종료일 시분초 추가 -> 해당 일자의 23시 59분 59초까지 검색하도록 설정
            searchDto.setSearchEndDate(endDate + " 23:59:59");
        }
    }

    /*************************************************************
     * 문자변환
     *************************************************************/

    /**
     * 이용권 지급 대상 그룹
     * 문자변환 list
     */
    private void convertGroupText(List<GroupDto> list) {
        for (GroupDto dto : list) {
            if (dto != null) {
                convertGroupText(dto);
            }
        }
    }

    /**
     * 이용권 지급 대상 그룹
     * 문자변환 dto
     */
    private void convertGroupText(GroupDto dto) {

        // 그룹 사용 상태 set
        if (dto.getState() != null) {

            // 미사용
            if (dto.getState() == 2) {
                dto.setStateText(super.langMessage("lang.ticket.use.state.unuse"));
                dto.setStateBg("badge-danger");

                // 사용
            } else if (dto.getState() == 1) {
                dto.setStateText(super.langMessage("lang.ticket.use.state.use"));
                dto.setStateBg("badge-success");
            }
        }
    }

    /**
     * 이용권 리스트
     * 문자변환 list
     */
    private void convertTicketText(List<TicketDto> list) {
        for (TicketDto dto : list) {
            if (dto != null) {
                convertTicketText(dto);
            }
        }
    }

    /**
     * 이용권 리스트
     * 문자변환 dto
     */
    private void convertTicketText(TicketDto dto) {

        // 지급 진행 상태 set
        if (dto.getStartDate() != null && !dto.getStartDate().isEmpty() && dto.getEndDate() != null && !dto.getEndDate().isEmpty()) {

            // 시작일과 종료일을 받아 현재 지급이 진행 중인지 체크
            Integer result = dateLibrary.checkProgressState(dto.getStartDate(), dto.getEndDate());

            // 진행중
            if (result == 0) {
                dto.setStateText(super.langMessage("lang.ticket.give.state.progress"));
                dto.setStateBg("badge-success");

                // 예약중
            } else if (result == -1) {
                dto.setStateText(super.langMessage("lang.ticket.give.state.reserve"));
                dto.setStateBg("badge-warning");
            }
        }

        // 지급 연령 set
        if (dto.getAdult() != null) {

            // 전체
            if (dto.getAdult() == 0) {
                dto.setAdultText(super.langMessage("lang.ticket.group.age.all"));
                dto.setAdultBg("badge-primary");

                // 성인
            } else if (dto.getAdult() == 1) {
                dto.setAdultText(super.langMessage("lang.ticket.group.age.adult"));
                dto.setAdultBg("badge-danger");
            }
        }

        // 사용량 set
        if (dto.getGiveCnt() > 0 && dto.getUseCnt() > 0) {
            // 반올림 - 소수점 둘째자리까지만 노출
            double usePercent = Math.round((dto.getUseCnt() / dto.getGiveCnt()) * 100) / 100.0;
            dto.setUsePercent(usePercent);
        }
    }

    /*************************************************************
     * Validation
     *************************************************************/

    /**
     * 선택한 그룹 idx 유효성 검사
     * @param idx
     */
    private void isTicketGroupIdxValidate(Long idx) {

        // 선택한 그룹 idx가 없는 경우
        if (idx == null || idx < 1) {
            throw new CustomException(CustomError.TICKET_GROUP_IDX_EMPTY); // 요청하신 그룹 상세 정보를 찾을 수 없습니다.
        }
    }

    /**
     * 이용권 지급 대상 그룹
     * 입력 데이터 유효성 검사
     *
     * @param groupDto
     */
    private void isTicketGroupDataValidate(GroupDto groupDto) {

        // 사용 상태
        if (groupDto.getState() == null || groupDto.getState() < 0 || groupDto.getState() > 2) {
            throw new CustomException(CustomError.TICKET_GROUP_STATE_EMPTY); // 그룹 사용 상태를 선택해주세요.
        }

        // 코드
        if (groupDto.getCode() == null || groupDto.getCode().isEmpty()) {
            throw new CustomException(CustomError.TICKET_GROUP_CODE_EMPTY); // 그룹 코드를 선택해주세요.
        }

        // 이름
        if (groupDto.getName() == null || groupDto.getName().isEmpty()) {
            throw new CustomException(CustomError.TICKET_GROUP_NAME_EMPTY); // 그룹 이름을 입력해주세요.
        }

        // 설명
        if (groupDto.getDescription() == null || groupDto.getDescription().isEmpty()) {
            throw new CustomException(CustomError.TICKET_GROUP_DESCRIPTION_EMPTY); // 그룹 설명을 입력해주세요.
        }
    }

    /**
     * 선택한 이용권 idx 유효성 검사
     * @param idxList
     */
    private void isTicketIdxValidate(String type, List<Long> idxList) {

        for (Long idx : idxList) {
            isTicketIdxValidate(type, idx);
        }
    }

    /**
     * 선택한 이용권 idx 유효성 검사
     * @param idx
     */
    private void isTicketIdxValidate(String type, Long idx) {

        // 이용권 idx가 없는 경우
        if (idx == null || idx < 1) {
            throw new CustomException(CustomError.TICKET_IDX_EMPTY); // 요청하신 이용권 상세 정보를 찾을 수 없습니다.
        }

        // 현재 지급 진행 중인 이용권을 삭제하려는 경우
        if (type == "delete") {

            // 이용권 상세 정보 조회
            TicketDto ticket = ticketDaoSub.getViewTicketReady(idx);

            // 시작일과 종료일을 받아 현재 지급이 진행 중인지 체크
            Integer result = dateLibrary.checkProgressState(ticket.getStartDate(), ticket.getEndDate());

            // 지급 진행 중인 경우
            if (Boolean.TRUE.equals(result)) {
                throw new CustomException(CustomError.TICKET_DELETE_STATE_ERROR); // 지급이 진행 중인 이용권은 삭제할 수 없습니다.
            }
        }
    }

    /**
     * 이용권 등록 & 수정
     * 입력 데이터 유효성 검사
     *
     * @param ticketDto
     */
    private void isTicketDataValidate(TicketDto ticketDto) {

        // 지급 상태
        if (ticketDto.getState() != null && ticketDto.getState() == 1) {
            throw new CustomException(CustomError.TICKET_MODIFY_STATE_ERROR); // 지급이 진행 중인 이용권은 수정할 수 없습니다.
        }

        // 그룹 코드 리스트
        if (ticketDto.getCode() == null || ticketDto.getCode().isEmpty()) {
            throw new CustomException(CustomError.TICKET_GROUP_EMPTY); // 지급 대상 그룹을 선택해주세요.
        }
        String codeList = ticketDto.getCode().replaceAll("[\\[\\]]", "").replaceAll("\"", "");
        ticketDto.setCode(codeList);

        // 작품 idx
        if (ticketDto.getContentsIdx() == null || ticketDto.getContentsIdx() < 1) {
            throw new CustomException(CustomError.TICKET_CONTENTS_IDX_EMPTY); // 작품을 선택해주세요.
        }

        // 작품 sellType
        if (ticketDto.getSellType() == null || ticketDto.getSellType() == 2) {
            throw new CustomException(CustomError.TICKET_SELL_TYPE_ERROR); // 소장만 가능한 작품은 이용권에 등록할 수 없습니다.
        }

        // 지급 연령
        if (ticketDto.getAdult() == null || ticketDto.getAdult() < 0 || ticketDto.getAdult() > 2) {
            throw new CustomException(CustomError.TICKET_GROUP_AGE_EMPTY); // 지급 연령을 선택해주세요.
        }

        // 지급 이용권 수
        if (ticketDto.getCount() == null || ticketDto.getCount() < 1) {
            throw new CustomException(CustomError.TICKET_COUNT_EMPTY); // 지급할 이용권 개수를 입력해주세요.
        }

        // 이용권 지급 제외 회차 수
        if (ticketDto.getExcept() == null || ticketDto.getExcept() < 1L) {
            throw new CustomException(CustomError.TICKET_EXCEPT_EPISODE_EMPTY); // 이용권 지급 제외 회차 개수를 입력해주세요.

        } else {
            // 해당 작품의 전체 회차 idx 리스트
            SearchDto search = new SearchDto();
            search.setContentsIdx(ticketDto.getContentsIdx());
            search.setNowDate(dateLibrary.getDatetime());
            List<Long> epIdxList = episodeDaoSub.getEpIdxList(search);

            // 입력 받은 이용권 지급 제외 회차 수가 실제 회차 개수보다 초과한 경우
            if (ticketDto.getExcept() >= epIdxList.size()) {
                throw new CustomException(CustomError.TICKET_EXCEPT_COUNT_ERROR); // 이용권 지급 제외 회차 개수가 유효하지 않습니다.
            }
        }

        // 유효기간
        if (ticketDto.getPeriod() == null || ticketDto.getPeriod() < 1L) {
            throw new CustomException(CustomError.TICKET_PERIOD_EMPTY); // 이용권 사용 유효기간을 입력해주세요.
        }

        // 입력 받은 사용 기간이 없는 경우
        if (ticketDto.getStartDate() == null || ticketDto.getStartDate().isEmpty() || ticketDto.getEndDate() == null || ticketDto.getEndDate().isEmpty()) {
            throw new CustomException(CustomError.TICKET_AVAILABLE_DATE_EMPTY); // 이용권 사용 가능 기간을 입력해주세요.

            // 입력 받은 사용 기간이 있는 경우
        } else {
            // 입력 받은 사용 기간 UTC 변환
            ticketDto.setStartDate(dateLibrary.localTimeToUtc(ticketDto.getStartDate()));
            ticketDto.setEndDate(dateLibrary.localTimeToUtc(ticketDto.getEndDate()));

            // 입력 받은 사용 기간이 유효한 기간인지 체크
            if (Boolean.FALSE.equals(dateLibrary.checkIsValidPeriod(ticketDto.getStartDate(), ticketDto.getEndDate()))) {
                throw new CustomException(CustomError.TICKET_AVAILABLE_DATE_ERROR); // 이용권 사용 가능 기간이 유효하지 않습니다.
            }
        }
    }
}
