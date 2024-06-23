package com.architecture.admin.models.dao.contents;

import com.architecture.admin.models.dto.contents.ViewerDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ViewerDao {

    /**
     * 뷰어 웹툰 이미지 등록
     * @param mapList
     * @return
     */
    Integer registerImage(List<HashMap<String, Object>> mapList);

    /**
     * 뷰어 소설 등록
     * @param mapList
     * @return
     */
    Integer registerNovel(List<Map<String, Object>> mapList);

    /**
     * 뷰어 소설 이미지(epub) 등록
     * @param uploadResponse
     */
    Integer registerNovelImage(List<HashMap<String, Object>> uploadResponse);

    /**
     * 뷰어 만화 등록
     * @param uploadResponse
     * @return
     */
    Integer registerComicImage(List<HashMap<String, Object>> uploadResponse);


    /**
     * 뷰어 웹툰 이미지 삭제(origin)
     * @param idx
     * @return
     */
    Integer deleteWebtoon(Integer idx);

    /**
     * 뷰어 만화 이미지 삭제(origin)
     * @param idx
     * @return
     */
    Integer deleteComic(Integer idx);

    /**
     * 뷰어 소설 이미지 삭제(origin)
     * @param idx
     * @return
     */
    Integer deleteNovel(Integer idx);


    /**
     * 뷰어 웹툰 이미지 순서 변경
     * @param viewerList
     * @return
     */
    Integer modifyWebtoon(List<ViewerDto> viewerList);


    /**
     * 뷰어 만화 이미지 순서 변경
     * @param viewerList
     * @return
     */
    Integer modifyComic(List<ViewerDto> viewerList);


    /**
     * 뷰어 소설 내용 수정
     * @param viewerList
     * @return
     */
    Integer modifyNovel(List<ViewerDto> viewerList);

}
