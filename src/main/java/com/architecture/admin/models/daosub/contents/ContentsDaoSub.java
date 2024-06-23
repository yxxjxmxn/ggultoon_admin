package com.architecture.admin.models.daosub.contents;

import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.contents.ContentsDto;
import com.architecture.admin.models.dto.notification.NotificationDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
@Mapper
public interface ContentsDaoSub {

    /**
     * 컨텐츠 전체 목록 count
     * @param searchDto
     * @return
     */
    int getTotalCount(SearchDto searchDto);

    /**
     * 컨텐츠 목록
     * @param searchDto
     * @return
     */
    List<ContentsDto> getList(SearchDto searchDto);

    /**
     * 컨텐츠 상세
     * @param idx
     * @return
     */
    ContentsDto getView(Integer idx);

    /**
     * 컨텐츠 회차 개수 count
     * @param idx
     * @return
     */
    int getTotalEpisodeCnt(Integer idx);

    /**
     * 컨텐츠 댓글 개수 count
     * @param idx
     * @return
     */
    int getTotalCommentCnt(Integer idx);

    /**
     * 신규 회차가 업로드된 작품 및 회차 번호 조회
     * @param dto
     * @return
     */
    HashMap<String, Object> getContentInfo(NotificationDto dto);

    /**
     * 카테고리별 장르 개수 카운트
     * @param searchDto
     * @return
     */
    int getTotalGenreCount(SearchDto searchDto);

    /**
     * 카테고리별 장르 목록
     * @param searchDto
     * @return
     */
    List<ContentsDto> getGenreList(SearchDto searchDto);

    /**
     * 큐레이션 개수 카운트
     * @param searchDto
     * @return
     */
    int getTotalCurationCnt(SearchDto searchDto);

    /**
     * 큐레이션 리스트 조회
     * @param searchDto
     * @return
     */
    List<ContentsDto> getCurationList(SearchDto searchDto);
}
