package com.architecture.admin.models.dto.board;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class FileResponseDto {

    /**
     * 이미지 임시 저장
     * ckeditor5
     */
    private boolean uploaded;
    private String url;
}
