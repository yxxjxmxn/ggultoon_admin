package com.architecture.admin.models.daosub.contents;

import com.architecture.admin.models.dto.contents.ImageDto;
import com.architecture.admin.models.dto.contents.ViewerDto;

import java.util.List;

public interface ViewerDaoSub {

    /**
     * 뷰어 웹툰
     * @param idx
     * @return
     */
    List<ImageDto> getViewerWebtoon(Integer idx);

    /**
     * 뷰어 만화
     * @param idx
     * @return
     */
    List<ImageDto> getViewerComic(Integer idx);

    /**
     * 뷰어 소설
     * @param idx
     * @return
     */
    List<ViewerDto> getViewerNovel(Integer idx);

    /**
     * 뷰어 소설 이미지
     * @param idx
     * @return
     */
    List<ImageDto> getViewerNovelImg(Integer idx);


    /**
     * 뷰어 웹툰 마지막 sort 번호
     * @param idx
     * @return
     */
    Integer getViewerWebtoonLastSort(Long idx);

    /**
     * 뷰어 만화 마지막 sort 번호
     * @param idx
     * @return
     */
    Integer getViewerComicLastSort(Long idx);
}
