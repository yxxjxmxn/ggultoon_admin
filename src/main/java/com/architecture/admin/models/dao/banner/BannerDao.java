package com.architecture.admin.models.dao.banner;

import com.architecture.admin.models.dto.banner.BannerDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface BannerDao {

    /**
     * 배너 등록
     * @param bannerDto
     * @return
     */
    Integer register(BannerDto bannerDto);

    /**
     * 배너 노출 영역 등록
     * @param map
     */
    void registerTypeMapping(Map<String, Object> map);

    /**
     * 배너 노출 영역 등록 - 장르
     * @param map
     */
    void registerGenreMapping(Map<String, Object> map);

    /**
     * 배너 이미지 등록
     * @param uploadResponse
     */
    void registerImage(List<HashMap<String, Object>> uploadResponse);

    /**
     * 배너 노출 영역 삭제
     * @param mapList
     */
    void deleteTypeMapping(List<Map<String, Object>> mapList);

    /**
     * 배너 노출 영역 삭제 - 장르
     * @param mapList
     */
    void deleteGenreMapping(List<Map<String, Object>> mapList);

    /**
     * 배너 수정
     * @param bannerDto
     * @return
     */
    Integer modify(BannerDto bannerDto);

    /**
     * 배너 이미지 삭제
     * @param bannerMap
     * @return
     */
    Integer deleteImage(Map<String, Object> bannerMap);
}
