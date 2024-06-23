package com.architecture.admin.models.dto.contents;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContentsDto {

    private Integer idx;
    // 카테고리
    private Integer categoryIdx;
    // 카테고리명
    private String categoryName;
    // 장르
    private Integer genreIdx;
    // 장르명
    private String genreName;
    // 제목
    private String title;
    // 설명
    private String description;
    // 시청 연령(성인유무)
    private Integer adult;
    // 시청 연령 문자변환
    private String adultText;
    // 시청 연령 color
    private String adultBg;
    // 성인관(0:일반, 1:성인관)
    private Integer adultPavilion;
    // 성인관 여부 문자변환
    private String adultPavilionText;
    // 성인관 여부 배지 정보
    private String adultPavilionBg;
    // 완결 타입 번호
    private Integer completeTypeIdx;
    // 완결 타입
    private String completeTypeName;
    // 연재정보(작품 상태)
    private Integer progress;
    // 작품상태 문자변환
    private String progressText;
    // 연재 요일
    private String weekly;
    // 레이블
    private String label;

    // 코드 매핑 번호
    private Integer codeIdx;
    // 작품 코드 번호
    private Integer code;
    // 작품 코드 이름
    private String codeText;

    // 조회수
    private Integer view;
    // view 카운팅 설정
    private Integer viewDummy;
    // 즐겨찾기 수
    private Integer favorite;
    // 별점
    private String rating;


    // 상태
    private Integer state;
    // 상태 문자변환
    private String stateText;
    // 상태 color
    private String stateBg;


    // 작가
    private String author;
    // 글작가
    private String writer;
    // 그림작가
    private String illustrator;


    // 서비스 유형 - 독점
    private Integer exclusive;
    private String exclusiveText;
    // 서비스 유형 - 단행본
    private Integer publication;
    // 서비스 유형 - 개정판
    private Integer revision;



    // 태그
    private String tagArr;

    // 태그 매핑
    private List<TagDto> tagList;

    // 판매종류 설정
    private Integer sellType;


    // 오픈일
    private String pubdate;
    // 오픈 시간
    private String pubTime;
    // 등록일
    private String regdate;

    // 마지막 회차 번호
    private Long lastEpisodeIdx;
    // 마지막 회차 제목
    private String lastEpisodeTitle;


    // 대표 이미지
    private List<MultipartFile> fileDataHeight;
    // 가로 이미지
    private List<MultipartFile> fileDataWidth;

    // 대표 이미지 - 수정 할때
    private String heightImage;

    // 가로 이미지 - 수정 할때
    private String widthImage;


    // 무료 회차수
    private Integer freeEpisodeCnt;
    // 이벤트 무료 회차
    private Integer eventFreeEpisodeCnt;
    // 이벤트 무료 사용 여부
    private Integer eventFreeUsed;
    // 이벤트 시작일
    private String eventStartDate;
    // 이벤트 종료일
    private String eventEndDate;

    // 전체 소장 - 최소 구매수
    private Integer minimumPurchase;
    // 전체 소장 - 할인율
    private Integer discount;
    // 전체 대여 - 최소 대여수
    private Integer minimumPurchaseRent;
    // 전체 대여 - 할인율
    private Integer discountRent;

    // 회차 개수
    private Integer episodeCnt;
    // 댓글 개수
    private Integer commentCnt;

    // sql
    private Integer insertedId;
    private Integer lastDateRow;


    // (idx, insertedId 같음)
    private Integer contentsIdx;

    /** 이미지 */
    private List<ImageDto> imageList;

    /** cp */
    // cp_member 번호
    private Integer cpMemberIdx;
    // 면세/과세 여부
    private Integer tax;
    // 면세 타입(1:면세(ISBN),2:면세(UCI),3:면세(ISSN))
    private Integer taxType;
    // 면세 코드
    private String taxCode;
    // 정산타입
    private Integer contract;
    // 웹 정산비율
    private Integer calculate;
    // 어플 정산비율
    private Integer calculateApp;
    // 개런티
    private Integer guarantee;
    // 회사명
    private String companyName;
    // 웹 수수료
    private Integer webFee;
    // 어플 수수료
    private Integer appFee;

}
