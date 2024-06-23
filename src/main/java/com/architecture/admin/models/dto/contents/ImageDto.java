package com.architecture.admin.models.dto.contents;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class ImageDto {

    private Integer idx;
    // 컨텐츠 idx
    private Integer contentsIdx;
    // 회차 idx
    private Integer episodeIdx;
    // origin idx
    private Integer parent;
    // 이미지 url
    private String url;
    // 이미지 파일명
    private String filename;
    // 이미지 타입(가로, 세로 구분)
    private String type;
    // 디바이스 (리사이즈 구분 : origin, pc, mobile, tablet)
    private String device;
    // resize 가로 사이즈
    private Integer width;
    // resize 높이 사이즈
    private Integer height;
    // 순서
    private Integer sort;
    // 상태값
    private Integer state;
    // 등록일
    private String regdate;

}
