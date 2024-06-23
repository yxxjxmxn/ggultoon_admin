package com.architecture.admin.models.dto.banner;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BannerDto {
    // 배너 idx
    private Integer idx;
    // 배너 타이틀
    private String title;
    // 성인 유무(0:일반, 1: 성인, 2: 전체)
    private Integer adultPavilion;
    // 배너 코드
    private String code;
    // 배너 이동 링크
    private String link;
    // 배너 순번
    private Integer sort;
    // 상태
    private Integer state;
    // 상태 문자변환
    private String stateText;
    // 상태 색상
    private String stateBg;
    // 예약 시작일
    private String startDate;
    // 예약 종료일
    private String endDate;
    // 등록일
    private String regdate;
    // 클릭수
    private Integer clickCount;

    // 배너 위치
    private List<Integer> type;

    // 배너 위치 - 카테고리
    private List<Integer> webtoonGenre;
    private List<Integer> comicGenre;
    private List<Integer> novelGenre;

    // 배너 이미지 파일
    private List<MultipartFile> imageFile_364;
    private List<MultipartFile> imageFile_260;
    private List<MultipartFile> imageFile_160;

    // 배너 위치 String
    private String strType;
    private String strWebtoonGenre;
    private String strComicGenre;
    private String strNovelGenre;

    // 배너 위치 문자 변환 - type + genre(웹툰, 만화, 소설)
    private String typeText;

    // 배너 이미지 String
    private String strImage364;
    private String strImage260;
    private String strImage160;

    // 배너 이미지 DTO
    private List<BannerImageDto> bannerImageList;

    // sql
    private Integer insertedId;
}
