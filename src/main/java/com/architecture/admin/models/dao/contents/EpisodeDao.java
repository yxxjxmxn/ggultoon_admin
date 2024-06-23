package com.architecture.admin.models.dao.contents;

import com.architecture.admin.models.dto.contents.EpisodeDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface EpisodeDao {

    /**
     * 회차 등록
     * @param episodeDto
     * @return
     */
    Integer register(EpisodeDto episodeDto);

    /**
     * 회차 이미지 등록
     * @param mapList
     * @return
     */
    Integer registerImage(List<HashMap<String,Object>> mapList);

    /**
     * 회차 추가 정보 등록
     * @param episodeDto
     * @return
     */
    Integer registerInfo(EpisodeDto episodeDto);

    /**
     * 회차 이벤트(할인) 등록
     * @param episodeDto
     * @return
     */
    Integer registerEventCoin(EpisodeDto episodeDto);

    /**
     * 회차 수정
     * @param episodeDto
     * @return
     */
    Integer modify(EpisodeDto episodeDto);

    /**
     * 회차 이벤트(할인) 수정
     * @param episodeDto
     * @return
     */
    Integer modifyEventCoin(EpisodeDto episodeDto);

    /**
     * 회차 이미지 삭제(origin)
     * @param idx
     * @return
     */
    Integer deleteImage(Integer idx);

    /**
     * 회차 이미지 sort 수정
     * @param mapList
     * @return
     */
    Integer imageSort(List<Map<String, Object>> mapList);

}
