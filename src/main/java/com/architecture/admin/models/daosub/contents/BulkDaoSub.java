package com.architecture.admin.models.daosub.contents;

import com.architecture.admin.models.dto.contents.ContentsDto;
import com.architecture.admin.models.dto.contents.EpisodeDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface BulkDaoSub {

    /**
     * 작품 타이틀 목록
     * @return
     */
    List<ContentsDto> getListContentsTitle();

    /**
     * 작품 등록 체크
     * @param idx
     * @return
     */
    Integer getContent(Integer idx);

    /**
     * 작품 이미지 등록 체크
     * @param idx
     * @return
     */
    Integer getContentImg(Integer idx);

    /**
     * 회차 등록 체크
     * @param idx
     * @return
     */
    Integer getEpisode(Long idx);

    /**
     * 회차 이미지 등록 체크
     * @param idx
     * @return
     */
    Integer getEpisodeImg(Long idx);

    /**
     * 뷰어 이미지 확인
     * @param episodeDto
     * @return
     */
    Integer getCheckViewer(EpisodeDto episodeDto);

    /*************************************************/
    List<EpisodeDto> getListEpisodeAll();

    List<ContentsDto> getListContentsAll();



}
