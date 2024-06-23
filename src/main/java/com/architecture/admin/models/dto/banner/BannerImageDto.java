package com.architecture.admin.models.dto.banner;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BannerImageDto {
    private Integer idx;
    // banner.idx
    private Integer bannerIdx;
    // 이미지 타입
    private Integer imgType;
    // 이미지 url
    private String url;
    // 이미지 파일명
    private String filename;
    // 순서
    private Integer sort;
    // 상태값
    private Integer state;
    // 등록일
    private String regdate;
}
