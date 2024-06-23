package com.architecture.admin.libraries;

import com.architecture.admin.models.dto.contents.ImageDto;
import com.squareup.pollexor.Thumbor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Component
@Data
public class ThumborLibrary {

    private final S3Library s3Library;

    @Value("${cloud.aws.cf.imgurl}")
    private String cfImgURL;

    @Value("${cloud.aws.cf.url}")
    private String domain;

    @Value("${thumbor.key}")
    private String thumborKey;


    public List<ImageDto> getImageCFUrl(List<ImageDto> imageList) {
        try {
            for(ImageDto dto : imageList) {

                // 이미지 full url
                String fullUrl = s3Library.getUploadedFullUrl(dto.getUrl());

                Thumbor thumbor = Thumbor.create(domain, thumborKey);

                // thumbor full url
                String thumborUrl = fullUrl;
                if (dto.getWidth() != null && dto.getHeight() != null) {
                    thumborUrl = thumbor.buildImage(fullUrl).resize(dto.getWidth(), dto.getHeight()).toUrl();
                }
                dto.setUrl(thumborUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imageList;
    }

    /**
     * Thumbor full url
     * @param fileUrl
     * @return
     */
    public String getUploadedFullUrl(String fileUrl) {
        try {
            return domain + fileUrl;
        } catch (StringIndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "TO get uploaded file(" + fileUrl + ") full url is error.");
        }
    }
}
