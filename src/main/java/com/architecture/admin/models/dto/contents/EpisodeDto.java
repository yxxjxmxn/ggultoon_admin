package com.architecture.admin.models.dto.contents;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EpisodeDto {

    private Long idx;
    private Long episodeIdx;
    // 작품 idx
    private Integer contentsIdx;
    // 작품 제목
    private String contentsTitle;
    // 카테고리 idx
    private Integer categoryIdx;

    // 판매 코인
    private Integer coin;
    // 대여 코인
    private Integer coinRent;
    // 회차 번호
    private Integer episodeNumber;
    // 회차 순서
    private Integer sort;
    // 회차 타입
    private Integer episodeTypeIdx;
    // 완료 타입
    private Integer completeTypeIdx;
    // 제목
    private String title;
    // 마지막 회차 제목
    private String lastEpisodeTitle;
    // 로그인 여부
    private Integer checkLogin;
    // 뷰어 보는 방향
    private Integer checkArrow;
    // 상태값
    private Integer state;
    // 상태값 문자 변환
    private String stateText;
    // 상태값 Background color
    private String stateBg;
    // 오픈일
    private String pubdate;
    // 오픈 시간
    private String pubTime;
    // 등록일
    private String regdate;

    // info
    private Integer view;
    private String rating;


    // episode_event
    // 이벤트(할인) 소장코인
    private Integer eventCoin;
    // 이벤트(할인) 대여코인
    private Integer eventCoinRent;
    // 이벤트(할인) 시작일
    private String eventStartDate;
    // 이벤트(할인) 시작 시간
    private String eventStartTime;
    // 이벤트(할인) 종료일
    private String eventEndDate;
    // 이벤트(할인) 종료 시간
    private String eventEndTime;


    // 세로 썸네일
    private List<MultipartFile> fileDataHeight;
    // 가로 썸네일
    private List<MultipartFile> fileDataWidth;

    // 세로 썸네일 - 수정 할때
    private String heightImage;
    // 가로 썸네일 - 수정 할때
    private String widthImage;


    // sql
    private Long insertedId;

    // 썸네일 리스트
    private List<ImageDto> imageList;

    private MultipartFile zipFile;


    // 대량 등록시 압축푼 파일
    private File file;

}
