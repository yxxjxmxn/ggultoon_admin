package com.architecture.admin.services.coupon;

import com.architecture.admin.libraries.PaginationLibray;
import com.architecture.admin.libraries.excel.ExcelData;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.models.dao.coupon.CouponDao;
import com.architecture.admin.models.daosub.coupon.CouponDaoSub;
import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.coupon.CouponDto;
import com.architecture.admin.models.dto.coupon.CouponStatDto;
import com.architecture.admin.models.dto.coupon.CouponStoreDto;
import com.architecture.admin.models.dto.coupon.CouponUsedDto;
import com.architecture.admin.services.BaseService;
import com.architecture.admin.services.log.AdminActionLogService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
@Transactional
public class CouponService extends BaseService {

    private final CouponDaoSub couponDaoSub;
    private final CouponDao couponDao;
    private final ExcelData excelData;
    private final AdminActionLogService adminActionLogService;// 관리자 action log 처리용

    /**************************************************************************************
     * 쿠폰 업체 리스트
     **************************************************************************************/

    /**
     * 쿠폰 업체 리스트
     *
     * @param searchDto
     */
    @Transactional(readOnly = true)
    public JSONObject getCouponStoreList(SearchDto searchDto) {

        // 검색어 공백제거
        if (searchDto.getSearchWord() != null && !searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // 내보낼 데이터 set
        JSONObject joData = new JSONObject();
        List<CouponStoreDto> couponStoreList = null;

        // 쿠폰 업체 개수 카운트
        int totalCnt = couponDaoSub.getCouponStoreTotalCnt(searchDto);

        // 쿠폰 업체가 있는 경우
        if (totalCnt > 0) {

            // paging
            PaginationLibray pagination = new PaginationLibray(totalCnt, searchDto);
            searchDto.setPagination(pagination);

            // 쿠폰 업체 리스트 조회
            couponStoreList = couponDaoSub.getCouponStoreList(searchDto);

            // 문자변환
            convertStoreText(couponStoreList);

            // paging 담기
            joData.put("params", new JSONObject(searchDto));
        }
        // list 담기
        joData.put("list", couponStoreList);
        return joData;
    }

    /**
     * 쿠폰 업체 리스트 엑셀 다운로드
     *
     * @param searchDto
     */
    @Transactional(readOnly = true)
    public Map<String, Object> excelCouponStoreList(SearchDto searchDto) {

        // 검색어 공백제거
        if (searchDto.getSearchWord() != null && !searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // 내보낼 데이터 set
        List<CouponStoreDto> couponStoreList = null;

        // 쿠폰 업체 개수 카운트
        int totalCnt = couponDaoSub.getCouponStoreTotalCnt(searchDto);

        // 쿠폰 업체가 있는 경우
        if (totalCnt > 0) {

            // paging
            PaginationLibray pagination = new PaginationLibray(totalCnt, searchDto);
            searchDto.setPagination(pagination);

            // 쿠폰 업체 리스트 조회
            couponStoreList = couponDaoSub.getCouponStoreList(searchDto);

            // 문자변환
            convertStoreText(couponStoreList);
        }
        // 엑셀 데이터 생성
        return excelData.createExcelData(couponStoreList, CouponStoreDto.class);
    }

    /**
     * 쿠폰 업체 상세
     *
     * @param idx
     */
    @Transactional(readOnly = true)
    public JSONObject getViewCouponStore(Long idx) {

        // return value
        JSONObject joData = new JSONObject();

        // 쿠폰 업체 idx 유효성 검사
        isCouponStoreIdxValidate(idx);

        // 쿠폰 업체 상세 조회
        CouponStoreDto viewCouponStore = couponDaoSub.getViewCouponStore(idx);

        if (viewCouponStore != null) {
            // 문자변환
            convertStoreText(viewCouponStore);
            // dto 담기
            joData = new JSONObject(viewCouponStore);
        }
        return joData;
    }

    /**
     * 쿠폰 업체 등록
     *
     * @param couponStoreDto
     */
    @Transactional
    public void insertCouponStore(CouponStoreDto couponStoreDto) {

        // 쿠폰 업체 정보 유효성 검사
        isStoreDataValidate(couponStoreDto);

        // 등록일 set
        couponStoreDto.setRegdate(dateLibrary.getDatetime());

        // 쿠폰 업체 등록
        int result = couponDao.insertCouponStore(couponStoreDto);

        // 등록 실패 시
        if (result < 1) {
            throw new CustomException(CustomError.COUPON_STORE_REGISTER_ERROR); // 쿠폰 업체 등록을 실패하였습니다.
        }
        // 관리자 action log
        adminActionLogService.regist(couponStoreDto, Thread.currentThread().getStackTrace());
    }

    /**
     * 쿠폰 업체 삭제
     *
     * @param idx
     */
    @Transactional
    public void deleteCouponStore(Long idx) {

        // 쿠폰 업체 idx 유효성 검사
        isCouponStoreIdxValidate(idx);

        // 쿠폰 업체 삭제
        int result = couponDao.deleteCouponStore(idx);

        // 삭제 실패 시
        if (result < 1) {
            throw new CustomException(CustomError.COUPON_STORE_DELETE_ERROR); // 쿠폰 업체 삭제를 실패하였습니다.
        }
        // 관리자 action log
        adminActionLogService.regist(idx, Thread.currentThread().getStackTrace());
    }
    
    /**
     * 쿠폰 업체 수정
     *
     * @param couponStoreDto
     */
    @Transactional
    public void updateCouponStore(CouponStoreDto couponStoreDto) {

        // 쿠폰 업체 정보 유효성 검사
        isStoreDataValidate(couponStoreDto);

        // 쿠폰 업체 수정
        int result = couponDao.updateCouponStore(couponStoreDto);

        // 수정 실패 시
        if (result < 1) {
            throw new CustomException(CustomError.COUPON_STORE_MODIFY_ERROR); // 쿠폰 업체 수정을 실패하였습니다.
        }
        // 관리자 action log
        adminActionLogService.regist(couponStoreDto, Thread.currentThread().getStackTrace());
    }

    /**************************************************************************************
     * 쿠폰 리스트
     **************************************************************************************/

    /**
     * 쿠폰 리스트 조회
     *
     * @param searchDto
     */
    @Transactional(readOnly = true)
    public JSONObject getCouponList(SearchDto searchDto) {

        // 검색어 공백제거
        if (searchDto.getSearchWord() != null && !searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // 검색 기간 시분초 세팅
        setSearchDateTime(searchDto);

        // 내보낼 데이터 set
        JSONObject joData = new JSONObject();
        List<CouponDto> couponList = null;

        // 쿠폰 개수 카운트
        int totalCnt = couponDaoSub.getCouponTotalCnt(searchDto);

        // 쿠폰이 있는 경우
        if (totalCnt > 0) {

            // recordSize set
            Integer recordSize = searchDto.getSearchCount();
            if (recordSize != null && recordSize > 0) {
                searchDto.setRecordSize(recordSize);
            }

            // paging
            PaginationLibray pagination = new PaginationLibray(totalCnt, searchDto);
            searchDto.setPagination(pagination);

            // 쿠폰 리스트 조회
            couponList = couponDaoSub.getCouponList(searchDto);

            // 상태값 문자변환
            convertCouponText(couponList);

            // paging 담기
            joData.put("params", new JSONObject(searchDto));
        }
        // list 담기
        joData.put("list", couponList);
        return joData;
    }

    /**
     * 쿠폰 리스트 엑셀 다운로드
     *
     * @param searchDto
     */
    @Transactional(readOnly = true)
    public Map<String, Object> excelCouponList(SearchDto searchDto) {

        // 검색어 공백제거
        if (searchDto.getSearchWord() != null && !searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // 검색 기간 시분초 세팅
        setSearchDateTime(searchDto);

        // 내보낼 데이터 set
        List<CouponDto> couponList = null;

        // 쿠폰 개수 카운트
        int totalCnt = couponDaoSub.getCouponTotalCnt(searchDto);

        // 쿠폰이 있는 경우
        if (totalCnt > 0) {

            // 쿠폰 리스트 조회
            couponList = couponDaoSub.getCouponList(searchDto);

            // 상태값 문자변환
            convertCouponText(couponList);
        }
        // 엑셀 데이터 생성
        return excelData.createExcelData(couponList, CouponDto.class);
    }

    /**
     * 쿠폰 상세
     *
     * @param idx
     */
    @Transactional(readOnly = true)
    public JSONObject getViewCoupon(Long idx) {

        // 쿠폰 idx 유효성 검사
        isCouponIdxValidate(idx);

        // 쿠폰 상세 조회
        CouponDto viewCoupon = couponDaoSub.getViewCoupon(idx);

        if (viewCoupon != null) {
            // 쿠폰 상태값 문자변환
            convertCouponText(viewCoupon);
        }
        // return value
        JSONObject joData = new JSONObject(viewCoupon);
        return joData;
    }

    /**
     * 쿠폰 등록
     *
     * @param couponDto
     */
    @Transactional
    public void insertCoupon(CouponDto couponDto) {

        // 쿠폰 정보 유효성 검사
        isCouponDataValidate(couponDto);

        // 등록일 set
        couponDto.setRegdate(dateLibrary.getDatetime());

        // 쿠폰 등록
        int result = couponDao.insertCoupon(couponDto);

        // 등록 실패 시
        if (result < 1) {
            throw new CustomException(CustomError.COUPON_REGISTER_ERROR); // 쿠폰 등록을 실패하였습니다.
        }
        // 관리자 action log
        adminActionLogService.regist(couponDto, Thread.currentThread().getStackTrace());
    }

    /**
     * 쿠폰 삭제
     *
     * @param idx
     */
    @Transactional
    public void deleteCoupon(Long idx) {

        // 쿠폰 idx 유효성 검사
        isCouponIdxValidate(idx);

        // 삭제할 쿠폰 정보 조회
        CouponDto couponDto = couponDaoSub.getViewCoupon(idx);
        if (couponDto != null && couponDto.getState() == 1) { // 쿠폰이 사용 가능 상태일 경우

            // 쿠폰 지급 상태 체크
            if (couponDto.getStartDate() != null && !couponDto.getStartDate().isEmpty() && couponDto.getEndDate() != null && !couponDto.getEndDate().isEmpty()) {

                // 시작일과 종료일을 받아 현재 지급이 진행 중인지 체크
                Integer result = dateLibrary.checkProgressState(couponDto.getStartDate(), couponDto.getEndDate());
                if (result == 0 || result == 1) {
                    throw new CustomException(CustomError.COUPON_DELETE_STATE_ERROR); // 지급이 진행중이거나 완료된 쿠폰은 삭제할 수 없습니다.
                }
            }
        }

        // 쿠폰 삭제
        int result = couponDao.deleteCoupon(idx);

        // 삭제 실패 시
        if (result < 1) {
            throw new CustomException(CustomError.COUPON_DELETE_ERROR); // 쿠폰 삭제를 실패하였습니다.
        }
        // 관리자 action log
        adminActionLogService.regist(idx, Thread.currentThread().getStackTrace());
    }

    /**
     * 쿠폰 수정
     *
     * @param couponDto
     */
    @Transactional
    public void updateCoupon(CouponDto couponDto) {

        // 쿠폰 정보 유효성 검사
        isCouponDataValidate(couponDto);

        // 쿠폰 수정
        int result = couponDao.updateCoupon(couponDto);

        // 수정 실패 시
        if (result < 1) {
            throw new CustomException(CustomError.COUPON_MODIFY_ERROR); // 쿠폰 수정을 실패하였습니다.
        }
        // 관리자 action log
        adminActionLogService.regist(couponDto, Thread.currentThread().getStackTrace());
    }

    /**************************************************************************************
     * 쿠폰 사용 내역
     **************************************************************************************/

    /**
     * 쿠폰 사용 내역
     *
     * @param searchDto
     */
    @Transactional(readOnly = true)
    public JSONObject getCouponUsedList(SearchDto searchDto) {

        // 검색어 공백제거
        if (searchDto.getSearchWord() != null && !searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // 검색 기간 시분초 세팅
        setSearchDateTime(searchDto);

        // 내보낼 데이터 set
        JSONObject joData = new JSONObject();
        List<CouponUsedDto> couponUsedList = null;

        // 쿠폰 사용 내역 개수 카운트
        int totalCnt = couponDaoSub.getCouponUsedTotalCnt(searchDto);

        // 쿠폰 사용 내역이 있는 경우
        if (totalCnt > 0) {

            // recordSize set
            Integer recordSize = searchDto.getSearchCount();
            if (recordSize != null && recordSize > 0) {
                searchDto.setRecordSize(recordSize);
            }

            // paging
            PaginationLibray pagination = new PaginationLibray(totalCnt, searchDto);
            searchDto.setPagination(pagination);

            // 쿠폰 사용 내역 조회
            couponUsedList = couponDaoSub.getCouponUsedList(searchDto);

            // 문자변환
            convertUsedText(couponUsedList);

            // paging 담기
            joData.put("params", new JSONObject(searchDto));
        }
        // list 담기
        joData.put("list", couponUsedList);
        return joData;
    }

    /**
     * 쿠폰 사용 내역 엑셀 다운로드
     *
     * @param searchDto
     */
    @Transactional(readOnly = true)
    public Map<String, Object> excelCouponUsedList(SearchDto searchDto) {

        // 검색어 공백제거
        if (searchDto.getSearchWord() != null && !searchDto.getSearchWord().isEmpty()) {
            searchDto.setSearchWord(searchDto.getSearchWord().trim());
        }

        // 검색 기간 시분초 세팅
        setSearchDateTime(searchDto);

        // 내보낼 데이터 set
        List<CouponUsedDto> couponUsedList = null;

        // 쿠폰 사용 내역 개수 카운트
        int totalCnt = couponDaoSub.getCouponTotalCnt(searchDto);

        // 쿠폰 사용 내역이 있는 경우
        if (totalCnt > 0) {

            // recordSize set
            Integer recordSize = searchDto.getSearchCount();
            if (recordSize != null && recordSize > 0) {
                searchDto.setRecordSize(recordSize);
            }

            // 쿠폰 사용 내역 조회
            couponUsedList = couponDaoSub.getCouponUsedList(searchDto);

            // 상태값 문자변환
            convertUsedText(couponUsedList);
        }
        // 엑셀 데이터 생성
        return excelData.createExcelData(couponUsedList, CouponUsedDto.class);
    }

    /**************************************************************************************
     * 쿠폰 통계
     **************************************************************************************/

    /**
     * 쿠폰 통계
     *
     * @param searchDto
     */
    @Transactional(readOnly = true)
    public JSONObject getCouponStat(SearchDto searchDto) {

        // 검색 기간 시분초 세팅
        setSearchDateTime(searchDto);

        // 내보낼 데이터 set
        JSONObject joData = new JSONObject();
        List<CouponStatDto> couponStatList = null;

        // 쿠폰 통계 개수 카운트
        int totalCnt = couponDaoSub.getCouponStatTotalCnt(searchDto);

        // 쿠폰 통계가 있는 경우
        if (totalCnt > 0) {

            // paging
            PaginationLibray pagination = new PaginationLibray(totalCnt, searchDto);
            searchDto.setPagination(pagination);

            // 쿠폰 통계 조회
            couponStatList = couponDaoSub.getCouponStat(searchDto);

            // 비율 계산
            calculatePercent(couponStatList);

            // paging 담기
            joData.put("params", new JSONObject(searchDto));
        }
        // list 담기
        joData.put("list", couponStatList);
        return joData;
    }

    /**
     * 쿠폰 통계 엑셀 다운로드
     *
     * @param searchDto
     */
    @Transactional(readOnly = true)
    public Map<String, Object> excelCouponStat(SearchDto searchDto) {

        // 검색 기간 시분초 세팅
        setSearchDateTime(searchDto);

        // 내보낼 데이터 set
        List<CouponStatDto> couponStatList = null;

        // 쿠폰 통계 개수 카운트
        int totalCnt = couponDaoSub.getCouponStatTotalCnt(searchDto);

        // 쿠폰 통계가 있는 경우
        if (totalCnt > 0) {

            // 쿠폰 통계 조회
            couponStatList = couponDaoSub.getCouponStat(searchDto);

            // 비율 계산
            calculatePercent(couponStatList);
        }
        // 엑셀 데이터 생성
        return excelData.createExcelData(couponStatList, CouponStatDto.class);
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
     * 쿠폰 업체
     * 문자변환 list
     */
    private void convertStoreText(List<CouponStoreDto> list) {
        for (CouponStoreDto dto : list) {
            if (dto != null) {
                convertStoreText(dto);
            }
        }
    }

    /**
     * 쿠폰 업체
     * 문자변환 dto
     */
    private void convertStoreText(CouponStoreDto dto) {

        // 업체 상태 set
        if (dto.getState() != null) {

            // 탈퇴
            if (dto.getState() == 2) {
                dto.setStateText(super.langMessage("lang.coupon.store.state.out"));
                dto.setStateBg("badge-danger");

                // 정상
            } else if (dto.getState() == 1) {
                dto.setStateText(super.langMessage("lang.coupon.store.state.normal"));
                dto.setStateBg("badge-success");
            }
        }

        // 업체 유형 set
        if (dto.getType() != null) {

            // 온라인
            if (dto.getType() == 1) {
                dto.setTypeText(super.langMessage("lang.coupon.store.type.online"));
                dto.setTypeBg("badge-primary");

                // 오프라인
            } else if (dto.getType() == 2) {
                dto.setTypeText(super.langMessage("lang.coupon.store.type.offline"));
                dto.setTypeBg("badge-warning");
            }
        }
    }

    /**
     * 쿠폰
     * 문자변환 list
     */
    private void convertCouponText(List<CouponDto> list) {
        for (CouponDto dto : list) {
            if (dto != null) {
                convertCouponText(dto);
            }
        }
    }

    /**
     * 쿠폰
     * 문자변환 dto
     */
    private void convertCouponText(CouponDto dto) {

        // 쿠폰 상태 set
        if (dto.getState() != null) {

            // 미사용
            if (dto.getState() == 2) {
                dto.setStateText(super.langMessage("lang.coupon.state.unuse"));
                dto.setStateBg("badge-danger");

                // 사용
            } else if (dto.getState() == 1) {
                dto.setStateText(super.langMessage("lang.coupon.state.use"));
                dto.setStateBg("badge-success");
            }
        }

        // 쿠폰 중복 여부 set
        if (dto.getDuplication() != null) {

            // 미중복
            if (dto.getDuplication() == 0) {
                dto.setDuplicationText(super.langMessage("lang.coupon.duplication.false"));
                dto.setDuplicationBg("badge-danger");

                // 중복
            } else if (dto.getDuplication() == 1) {
                dto.setDuplicationText(super.langMessage("lang.coupon.duplication.true"));
                dto.setDuplicationBg("badge-success");
            }
        }

        // 쿠폰 종류 set
        if (dto.getType() != null) {

            // 한글
            if (dto.getType() == 1) {
                dto.setTypeText(super.langMessage("lang.coupon.type.korean"));

                // 영어
            } else if (dto.getType() == 2) {
                dto.setTypeText(super.langMessage("lang.coupon.type.english"));

                // 한글 + 난수
            } else if (dto.getType() == 3) {
                dto.setTypeText(super.langMessage("lang.coupon.type.random.number.korean"));

                // 영어 + 난수
            } else if (dto.getType() == 4) {
                dto.setTypeText(super.langMessage("lang.coupon.type.random.number.english"));
            }
        }

        // 업체 유형 set
        if (dto.getStoreType() != null) {

            // 온라인
            if (dto.getStoreType() == 1) {
                dto.setStoreTypeText(super.langMessage("lang.coupon.store.type.online"));
                dto.setStoreTypeBg("badge-primary");

                // 오프라인
            } else if (dto.getStoreType() == 2) {
                dto.setStoreTypeText(super.langMessage("lang.coupon.store.type.offline"));
                dto.setStoreTypeBg("badge-warning");
            }
        }

        // 쿠폰 지급 상태 set
        if (dto.getStartDate() != null && !dto.getStartDate().isEmpty() && dto.getEndDate() != null && !dto.getEndDate().isEmpty()) {

            // 쿠폰이 미사용 또는 삭제 상태일 경우
            if (dto.getState() != null && dto.getState() != 1) {
                dto.setProgressText(super.langMessage("lang.coupon.progress.stop")); // 중단
                dto.setProgressBg("badge-danger");

                // 쿠폰이 사용 가능 상태일 경우
            } else {
                // 시작일과 종료일을 받아 현재 지급이 진행 중인지 체크
                Integer result = dateLibrary.checkProgressState(dto.getStartDate(), dto.getEndDate());

                // 예약 상태
                if (result == -1) {
                    dto.setProgressText(super.langMessage("lang.coupon.progress.ready")); // 예약
                    dto.setProgressBg("badge-warning");

                    // 진행 상태
                } else if (result == 0) {
                    dto.setProgressText(super.langMessage("lang.coupon.progress.ongoing")); // 진행
                    dto.setProgressBg("badge-primary");

                    // 완료 상태
                } else if (result == 1) {
                    dto.setProgressText(super.langMessage("lang.coupon.progress.complete")); // 완료
                    dto.setProgressBg("badge-light");
                }
            }
        }
    }

    /**
     * 쿠폰 사용 내역
     * 문자변환 list
     */
    private void convertUsedText(List<CouponUsedDto> list) {
        for (CouponUsedDto dto : list) {
            if (dto != null) {
                convertUsedText(dto);
            }
        }
    }

    /**
     * 쿠폰 사용 내역
     * 문자변환 dto
     */
    private void convertUsedText(CouponUsedDto dto) {

        // 회원 구분 정보 set
        if (dto.getType() != null) {

            // 신규
            if (dto.getType() == 1) {
                dto.setTypeText(super.langMessage("lang.coupon.use.member.new"));
                dto.setTypeBg("badge-primary");

                // 기존
            } else if (dto.getType() == 2) {
                dto.setTypeText(super.langMessage("lang.coupon.use.member.old"));
                dto.setTypeBg("badge-warning");
            }
        }

        // 회원 접속 기기 set
        if (dto.getRouteText() != null) {

            // PC
            if (dto.getRouteText().equals("pc")) {
                dto.setRouteBg("badge-primary");

                // 모바일
            } else if (dto.getRouteText().equals("m")) {
                dto.setRouteBg("badge-success");

                // 어플
            } else if (dto.getRouteText().equals("app")) {
                dto.setRouteBg("badge-warning");
            }
        }

        // 업체 유형 set
        if (dto.getStoreType() != null) {

            // 온라인
            if (dto.getStoreType() == 1) {
                dto.setStoreTypeText(super.langMessage("lang.coupon.store.type.online"));
                dto.setStoreTypeBg("badge-primary");

                // 오프라인
            } else if (dto.getStoreType() == 2) {
                dto.setStoreTypeText(super.langMessage("lang.coupon.store.type.offline"));
                dto.setStoreTypeBg("badge-warning");
            }
        }
    }

    /**
     * 쿠폰 통계 비율 계산
     * 신규율 / 탈퇴율 / 결제율
     * 소수점 둘째자리까지 노출(반올림)
     */
    private void calculatePercent(List<CouponStatDto> list) {
        for (CouponStatDto dto : list) {
            if (dto != null) {

                // 신규율 계산(등록수 대비 신규수)
                if (dto.getRegisterCnt() > 0 && dto.getNewCnt() > 0) {
                    double newPercent = Math.round((dto.getNewCnt() / dto.getRegisterCnt()) * 100) / 100.0;
                    dto.setNewPercent(newPercent);
                }

                // 탈퇴율 계산(등록수 대비 탈퇴수)
                if (dto.getRegisterCnt() > 0 && dto.getOutCnt() > 0) {
                    double outPercent = Math.round((dto.getOutCnt() / dto.getRegisterCnt()) * 100) / 100.0;
                    dto.setOutPercent(outPercent);
                }

                // 결제율 계산(신규수 대비 신규결제수)
                if (dto.getNewCnt() > 0 && dto.getNewPaymentCnt() > 0) {
                    double paymentPercent = Math.round((dto.getNewPaymentCnt() / dto.getNewCnt()) * 100) / 100.0;
                    dto.setPaymentPercent(paymentPercent);
                }
            }
        }
    }

    /*************************************************************
     * Validation
     *************************************************************/

    /**
     * 선택한 쿠폰 업체 idx 유효성 검사
     * @param idx
     */
    private void isCouponStoreIdxValidate(Long idx) {

        // 쿠폰 업체 idx가 없는 경우
        if (idx == null || idx < 1) {
            throw new CustomException(CustomError.COUPON_STORE_IDX_EMPTY); // 요청하신 쿠폰 업체 상세 정보를 찾을 수 없습니다.
        }
    }

    /**
     * 쿠폰 업체
     * 입력 데이터 유효성 검사
     *
     * @param couponStoreDto
     */
    private void isStoreDataValidate(CouponStoreDto couponStoreDto) {

        // 업체 상태
        if (couponStoreDto.getState() == null || couponStoreDto.getState() < 1 || couponStoreDto.getState() > 2) {
            throw new CustomException(CustomError.COUPON_STORE_STATE_EMPTY); // 업체 상태를 선택해주세요.
        }

        // 업체 이름
        if (couponStoreDto.getName() == null || couponStoreDto.getName().isEmpty()) {
            throw new CustomException(CustomError.COUPON_STORE_NAME_EMPTY); // 업체 이름을 입력해주세요.
        }

        // 업체 담당자 연락처
        if (couponStoreDto.getPhone() != null && !couponStoreDto.getPhone().isEmpty()) {
            String numberRegex = "^[\\d]*$"; // 숫자 체크용 정규식
            String phoneRegex = "^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{5}$"; // 전화번호 형식 체크용 정규식
            if (!Pattern.matches(numberRegex, couponStoreDto.getPhone()) || !Pattern.matches(phoneRegex, couponStoreDto.getPhone())) {
                throw new CustomException(CustomError.COUPON_STORE_MANAGER_PHONE_ERROR); // 업체 담당자 연락처가 유효하지 않습니다.
            }
        }

        // 업체 유형
        if (couponStoreDto.getType() == null || couponStoreDto.getType() < 1 || couponStoreDto.getType() > 2) {
            throw new CustomException(CustomError.COUPON_STORE_TYPE_EMPTY); // 업체 유형을 선택해주세요.
        }
    }

    /**
     * 선택한 쿠폰 idx 유효성 검사
     * @param idx
     */
    private void isCouponIdxValidate(Long idx) {

        // 쿠폰 idx가 없는 경우
        if (idx == null || idx < 1) {
            throw new CustomException(CustomError.COUPON_IDX_EMPTY); // 요청하신 쿠폰 상세 정보를 찾을 수 없습니다.
        }
    }

    /**
     * 쿠폰
     * 입력 데이터 유효성 검사
     *
     * @param couponDto
     */
    private void isCouponDataValidate(CouponDto couponDto) {

        // 쿠폰 지급 상태
        if (couponDto.getState() == 1) { // 쿠폰이 사용 가능 상태일 경우
            if (couponDto.getStartDate() != null && !couponDto.getStartDate().isEmpty() && couponDto.getEndDate() != null && !couponDto.getEndDate().isEmpty()) {

                // 시작일과 종료일을 받아 현재 지급이 진행 중인지 체크
                Integer result = dateLibrary.checkProgressState(couponDto.getStartDate(), couponDto.getEndDate());
                if (result == 0 || result == 1) {
                    throw new CustomException(CustomError.COUPON_MODIFY_STATE_ERROR); // 지급이 진행중이거나 완료된 쿠폰은 수정할 수 없습니다.
                }
            }
        }

        // 쿠폰 상태
        if (couponDto.getState() == null || couponDto.getState() < 1 || couponDto.getState() > 2) {
            throw new CustomException(CustomError.COUPON_STATE_EMPTY); // 쿠폰 상태를 선택해주세요.
        }

        // 쿠폰 이름
        if (couponDto.getTitle() == null || couponDto.getTitle().isEmpty()) {
            throw new CustomException(CustomError.COUPON_TITLE_EMPTY); // 쿠폰 이름을 입력해주세요.
        }

        // 쿠폰 코드
        if (couponDto.getDuplication() == 1) {
            if (couponDto.getTitle() == null || couponDto.getTitle().isEmpty()) {
                throw new CustomException(CustomError.COUPON_CODE_EMPTY); // 쿠폰 코드를 입력해주세요.
            }
        }

        // 쿠폰 중복 여부
        if (couponDto.getDuplication() == null || couponDto.getDuplication() < 0 || couponDto.getDuplication() > 1) {
            throw new CustomException(CustomError.COUPON_DUPLE_EMPTY); // 쿠폰 중복 여부를 선택해주세요.
        }

        // 쿠폰 종류
        if (couponDto.getType() == null || couponDto.getType() < 1 || couponDto.getType() > 4) {
            throw new CustomException(CustomError.COUPON_TYPE_EMPTY); // 쿠폰 종류를 선택해주세요.
        }

        // 지급 마일리지
        if (couponDto.getMileage() == null || couponDto.getMileage() < 0) {
            throw new CustomException(CustomError.COUPON_MILEAGE_EMPTY); // 지급할 마일리지 개수를 입력해주세요.
        }

        // 발급 개수
        if (couponDto.getCouponCnt() == null || couponDto.getCouponCnt() < 0) {
            throw new CustomException(CustomError.COUPON_COUNT_EMPTY); // 쿠폰 발급 개수를 입력해주세요.
        }

        // 쿠폰 업체 IDX
        if (couponDto.getStoreIdx() == null || couponDto.getStoreIdx() < 1) {
            throw new CustomException(CustomError.COUPON_STORE_IDX_EMPTY); // 요청하신 쿠폰 업체 상세 정보를 찾을 수 없습니다.
        }

        // 입력 받은 사용 기간이 없는 경우
        if (couponDto.getStartDate() == null || couponDto.getStartDate().isEmpty() || couponDto.getEndDate() == null || couponDto.getEndDate().isEmpty()) {
            throw new CustomException(CustomError.COUPON_AVAILABLE_DATE_EMPTY); // 쿠폰 사용 가능 기간을 입력해주세요.

            // 입력 받은 사용 기간이 있는 경우
        } else {
            // 입력 받은 사용 기간 UTC 변환
            couponDto.setStartDate(dateLibrary.localTimeToUtc(couponDto.getStartDate()));
            couponDto.setEndDate(dateLibrary.localTimeToUtc(couponDto.getEndDate()));

            // 입력 받은 사용 기간이 유효한 기간인지 체크
            if (Boolean.FALSE.equals(dateLibrary.checkIsValidPeriod(couponDto.getStartDate(), couponDto.getEndDate()))) {
                throw new CustomException(CustomError.COUPON_AVAILABLE_DATE_ERROR); // 쿠폰 사용 가능 기간이 유효하지 않습니다.
            }
        }
    }
}
