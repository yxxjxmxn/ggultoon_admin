package com.architecture.admin.models.dto.curation;

import com.architecture.admin.libraries.excel.ExcelColumnName;
import com.architecture.admin.libraries.excel.ExcelFileName;
import com.architecture.admin.models.dto.excel.ExcelDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@ExcelFileName(filename = "lang.curation.title.list")
public class CurationDto implements ExcelDto {

    /** curation & curation_area_mapping **/
    @ExcelColumnName(headerName = "lang.curation.idx")
    private Long idx; // 큐레이션 idx
    @ExcelColumnName(headerName = "lang.curation.sort")
    private Integer sort; // 노출 순서
    private Integer state; // 상태값
    @ExcelColumnName(headerName = "lang.curation.state")
    private String stateText; // 상태값 문자 변환
    private String stateBg; // 상태값 배지
    @ExcelColumnName(headerName = "lang.curation.title")
    private String title; // 제목
    @ExcelColumnName(headerName = "lang.curation.name")
    private String code; // 노출 영역 코드
    private Integer reservation; // 예약 상태값
    @ExcelColumnName(headerName = "lang.curation.reservation")
    private String reservationText; // 예약 상태값 문자 변환
    private String reservationBg; // 예약 상태값 배지
    @ExcelColumnName(headerName = "lang.curation.startDate")
    private String startDate; // 예약 시작일
    private String startDateTz; // 예약 시작일 타임존
    @ExcelColumnName(headerName = "lang.curation.endDate")
    private String endDate; // 예약 종료일
    private String endDateTz; // 예약 종료일 타임존
    @ExcelColumnName(headerName = "lang.curation.content.count")
    private Integer contentCnt; // 작품 수
    @ExcelColumnName(headerName = "lang.curation.regdate")
    private String regdate; // 등록일
    private String regdateTz; // 등록일 타임존
    private Long areaIdx; // 노출 영역 idx
    private String area; // 노출 영역 idx
    private String name; // 노출 영역 이름
    private String description; // 노출 영역 설명

    /** 기타 **/
    private Integer maxSort;    // 마지막 순서 번호
    List<HashMap<String, Long>> list; // 순서 재정렬용 리스트
    List<Long> idxList; // 삭제용 리스트

    /** sql **/
    private Long insertedIdx;

    @Override
    public List<String> mapToList() {
        return Arrays.asList(
                String.valueOf(idx),
                String.valueOf(sort),
                stateText,
                title,
                code,
                reservationText,
                startDate,
                endDate,
                String.valueOf(contentCnt),
                regdate);
    }
}
