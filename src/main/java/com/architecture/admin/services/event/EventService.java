package com.architecture.admin.services.event;

import com.architecture.admin.libraries.PaginationLibray;
import com.architecture.admin.libraries.excel.ExcelData;
import com.architecture.admin.models.dao.event.EventDao;
import com.architecture.admin.models.daosub.event.EventDaoSub;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.contents.EpisodeDto;
import com.architecture.admin.models.dto.event.NewYearStatDto;
import com.architecture.admin.models.dto.event.NewYearViewDto;
import com.architecture.admin.models.dto.event.PromotionDto;
import com.architecture.admin.services.BaseService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class EventService extends BaseService {

    private final EventDaoSub eventDaoSub;
    private final EventDao eventDao;
    private final ExcelData excelData;


    /**************************************************************************************
     * 2024 설연휴 이벤트 참여 내역 조회
     **************************************************************************************/

    /**
     * 2024 설연휴 이벤트 참여 내역 조회
     *
     * @param searchDto
     */
    @Transactional(readOnly = true)
    public JSONObject getNewYearEventList(SearchDto searchDto) {

        // 검색어 공백제거
        if (searchDto.getSearchWord() != null && !searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // nowDate set
        searchDto.setNowDate(dateLibrary.getDatetime());

        /** 데이터 누락 방지용 시분초 추가 **/
        // 검색 시작일 시분초 추가 -> 해당 일자의 00시 00분 00초부터 검색하도록 설정
        if (searchDto.getSearchStartDate() != null && !searchDto.getSearchStartDate().isEmpty()) {
            searchDto.setSearchStartDate(searchDto.getSearchStartDate() + " 00:00:00");
        }
        // 검색 종료일 시분초 추가 -> 해당 일자의 23시 59분 59초까지 검색하도록 설정
        if (searchDto.getSearchEndDate() != null && !searchDto.getSearchEndDate().isEmpty()) {
            searchDto.setSearchEndDate(searchDto.getSearchEndDate() + " 23:59:59");
        }

        // 내보낼 데이터 set
        JSONObject joData = new JSONObject();
        List<NewYearViewDto> newYearEventList = null;

        // 이벤트 참여 내역 개수 카운트
        int totalCnt = eventDaoSub.getNewYearEventCnt(searchDto);

        // 이벤트 참여 내역이 있는 경우
        if (totalCnt > 0) {

            // recordSize set
            Integer recordSize = searchDto.getSearchCount();
            if (recordSize != null && recordSize > 0) {
                searchDto.setRecordSize(recordSize);
            }

            // paging
            PaginationLibray pagination = new PaginationLibray(totalCnt, searchDto);
            searchDto.setPagination(pagination);

            // 리스트 조회
            newYearEventList = eventDaoSub.getNewYearEventList(searchDto);

            // 상태값 문자변환
            convertEventText(newYearEventList);

            // paging 담기
            joData.put("params", new JSONObject(searchDto));
        }
        // list 담기
        joData.put("list", newYearEventList);
        return joData;
    }

    /**
     * 2024 설연휴 이벤트 참여 내역 엑셀 다운로드
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public Map<String, Object> excelNewYearEvent(SearchDto searchDto) {

        // 검색어 공백제거
        if (searchDto.getSearchWord() != null && !searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // nowDate set
        searchDto.setNowDate(dateLibrary.getDatetime());

        /** 데이터 누락 방지용 시분초 추가 **/
        // 검색 시작일 시분초 추가 -> 해당 일자의 00시 00분 00초부터 검색하도록 설정
        if (searchDto.getSearchStartDate() != null && !searchDto.getSearchStartDate().isEmpty()) {
            searchDto.setSearchStartDate(searchDto.getSearchStartDate() + " 00:00:00");
        }
        // 검색 종료일 시분초 추가 -> 해당 일자의 23시 59분 59초까지 검색하도록 설정
        if (searchDto.getSearchEndDate() != null && !searchDto.getSearchEndDate().isEmpty()) {
            searchDto.setSearchEndDate(searchDto.getSearchEndDate() + " 23:59:59");
        }

        // send data set
        List<NewYearViewDto> newYearEventList = null;

        // 이벤트 참여 내역 개수 카운트
        int totalCnt = eventDaoSub.getNewYearEventCnt(searchDto);

        // 이벤트 참여 내역이 있는 경우
        if (totalCnt > 0) {

            // recordSize set
            Integer recordSize = searchDto.getSearchCount();
            if (recordSize != null && recordSize > 0) {
                searchDto.setRecordSize(recordSize);
            }

            // 리스트 조회
            newYearEventList = eventDaoSub.getNewYearEventList(searchDto);

            // 상태값 문자변환
            convertEventText(newYearEventList);
        }
        // 엑셀 데이터 생성
        return excelData.createExcelData(newYearEventList, NewYearViewDto.class);
    }

    /**************************************************************************************
     * 2024 설연휴 이벤트 통계
     **************************************************************************************/

    /**
     * 2024 설연휴 이벤트 통계
     *
     * @param searchDto
     */
    @Transactional(readOnly = true)
    public JSONObject getNewYearEventStat(SearchDto searchDto) {

        // 검색어 공백제거
        if (searchDto.getSearchWord() != null && !searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // nowDate set
        searchDto.setNowDate(dateLibrary.getDatetime());

        // 내보낼 데이터 set
        JSONObject joData = new JSONObject();
        List<NewYearStatDto> newYearEventStat = null;

        // 이벤트 통계 개수 카운트
        int totalCnt = eventDaoSub.getNewYearEventStatCnt(searchDto);

        // 이벤트 참여 내역이 있는 경우
        if (totalCnt > 0) {

            // paging
            PaginationLibray pagination = new PaginationLibray(totalCnt, searchDto);
            searchDto.setPagination(pagination);

            // 리스트 조회
            newYearEventStat = eventDaoSub.getNewYearEventStat(searchDto);

            // 상태값 문자변환
            convertStatText(newYearEventStat);

            // paging 담기
            joData.put("params", new JSONObject(searchDto));
        }
        // list 담기
        joData.put("list", newYearEventStat);
        return joData;
    }

    /**
     * 2024 설연휴 이벤트 통계 엑셀 다운로드
     * @param searchDto
     * @return
     */
    @Transactional(readOnly = true)
    public Map<String, Object> excelNewYearEventStat(SearchDto searchDto) {

        // 검색어 공백제거
        if (searchDto.getSearchWord() != null && !searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // nowDate set
        searchDto.setNowDate(dateLibrary.getDatetime());

        // send data set
        List<NewYearStatDto> newYearEventStat = null;

        // 이벤트 참여 내역 개수 카운트
        int totalCnt = eventDaoSub.getNewYearEventStatCnt(searchDto);

        // 이벤트 참여 내역이 있는 경우
        if (totalCnt > 0) {

            // 리스트 조회
            newYearEventStat = eventDaoSub.getNewYearEventStat(searchDto);

            // 상태값 문자변환
            convertStatText(newYearEventStat);
        }
        // 엑셀 데이터 생성
        return excelData.createExcelData(newYearEventStat, NewYearStatDto.class);
    }

    /*************************************************************
     * 문자변환
     *************************************************************/

    /**
     * 2024 설연휴 이벤트 참여 내역
     * 문자변환 list
     */
    private void convertEventText(List<NewYearViewDto> list) {
        for (NewYearViewDto dto : list) {
            if (dto != null) {
                convertEventText(dto);
            }
        }
    }

    /**
     * 2024 설연휴 이벤트 참여 내역
     * 문자변환 dto
     */
    private void convertEventText(NewYearViewDto dto) {

        // 참여 기기 set
        if (dto.getRoute() != null) {

            // 웹
            if (dto.getRoute() == 1) {
                dto.setRouteText(super.langMessage("lang.event.route.web"));
                dto.setRouteBg("badge-warning");

                // 어플
            } else if (dto.getRoute() == 2) {
                dto.setRouteText(super.langMessage("lang.event.route.app"));
                dto.setRouteBg("badge-success");
            }
        }

        // 구매유형 set
        if (dto.getType() != null) {

            // 대여
            if (dto.getType() == 1) {
                dto.setTypeText(super.langMessage("lang.event.type.rent"));
                dto.setTypeBg("badge-warning");

                // 소장
            } else if (dto.getType() == 2) {
                dto.setTypeText(super.langMessage("lang.event.type.have"));
                dto.setTypeBg("badge-primary");
            }
        }

        // 회원구분 set
        if (dto.getUserType() != null) {

            // OTT 배너 또는 레이어 팝업을 통해 유입(신규)
            if (dto.getUserType() == 1) {
                dto.setUserTypeText(super.langMessage("lang.event.userType.new"));
                dto.setUserTypeBg("badge-success");

                // 가입 경로가 OTT 사이트인 꿀툰 회원(기존)
            } else if (dto.getUserType() == 2) {
                dto.setUserTypeText(super.langMessage("lang.event.userType.old"));
                dto.setUserTypeBg("badge-light");
            }
        }

        // 이용관 정보 set
        if (dto.getPavilion() != null) {

            // 일반관
            if (dto.getPavilion() == 0) {
                dto.setPavilionText(super.langMessage("lang.contents.pavilion.all"));
                dto.setPavilionBg("badge-primary");

                // 성인관
            } else if (dto.getPavilion() == 1) {
                dto.setPavilionText(super.langMessage("lang.contents.pavilion.adult"));
                dto.setPavilionBg("badge-danger");
            }
        }

        // 성인작 정보 set
        if (dto.getAdult() != null) {

            // 전체
            if (dto.getAdult() == 0) {
                dto.setAdultText(super.langMessage("lang.contents.age.non_adult"));
                dto.setAdultBg("badge-primary");

                // 성인
            } else if (dto.getAdult() == 1) {
                dto.setAdultText(super.langMessage("lang.contents.age.adult"));
                dto.setAdultBg("badge-danger");
            }
        }
    }

    /**
     * 2024 설연휴 이벤트 통계
     * 문자변환 list
     */
    private void convertStatText(List<NewYearStatDto> list) {
        for (NewYearStatDto dto : list) {
            if (dto != null) {
                convertStatText(dto);
            }
        }
    }

    /**
     * 2024 설연휴 이벤트 통계
     * 문자변환 dto
     */
    private void convertStatText(NewYearStatDto dto) {

        // 이용관 정보 set
        if (dto.getPavilion() != null) {

            // 일반관
            if (dto.getPavilion() == 0) {
                dto.setPavilionText(super.langMessage("lang.contents.pavilion.all"));
                dto.setPavilionBg("badge-primary");

                // 성인관
            } else if (dto.getPavilion() == 1) {
                dto.setPavilionText(super.langMessage("lang.contents.pavilion.adult"));
                dto.setPavilionBg("badge-danger");
            }
        }

        // 성인작 정보 set
        if (dto.getAdult() != null) {

            // 전체
            if (dto.getAdult() == 0) {
                dto.setAdultText(super.langMessage("lang.contents.age.non_adult"));
                dto.setAdultBg("badge-primary");

                // 성인
            } else if (dto.getAdult() == 1) {
                dto.setAdultText(super.langMessage("lang.contents.age.adult"));
                dto.setAdultBg("badge-danger");
            }
        }
    }

    /**
     * 프로모션 이벤트
     * @return
     */
    @Transactional
    public Integer promotionEvent() {
        List<EpisodeDto> promotionEpisodeList = new ArrayList<>();

        // promotion 조회
        List<PromotionDto> list = eventDaoSub.getPromotion();
        for (PromotionDto promotionDto : list) {
            EpisodeDto episodeDto = new EpisodeDto();
            episodeDto.setIdx(promotionDto.getEpisodeIdx());
            episodeDto.setEventCoin(promotionDto.getCoin());
            episodeDto.setEventCoinRent(promotionDto.getCoinRent());
            episodeDto.setEventStartDate(promotionDto.getPromotionStartDate());
            episodeDto.setEventEndDate(promotionDto.getPromotionEndDate());
            episodeDto.setRegdate(dateLibrary.getDatetime());

            promotionEpisodeList.add(episodeDto);
        }

        Integer result = 1;
        if (promotionEpisodeList.size() > 0) {
            // 프로모션 적용
            result = eventDao.setPromotionEvent(promotionEpisodeList);

            // 프로모션 적용여부
            eventDao.setPromotionEventUsed();
        }

        return result;
    }
}
