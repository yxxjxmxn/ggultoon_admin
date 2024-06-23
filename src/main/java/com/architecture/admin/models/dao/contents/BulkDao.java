package com.architecture.admin.models.dao.contents;

import com.architecture.admin.models.dto.contents.AuthorDto;
import com.architecture.admin.models.dto.contents.CodeDto;
import com.architecture.admin.models.dto.contents.ContentsDto;
import com.architecture.admin.models.dto.contents.EpisodeDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface BulkDao {

    /**
     * 작품 대량 등록
     * @param contentsList
     * @return
     */
    Integer registerBulkContents(List<ContentsDto> contentsList);

    /**
     * 작품 추가 정보 대량 등록
     * @param contentsList
     * @return
     */
    Integer registerBulkInfo(List<ContentsDto> contentsList);

    /**
     * 코드 등록
     * @param codeList
     * @return
     */
    Integer registerBulkCode(List<CodeDto> codeList);

    /**
     * 연재 요일 등록
     * @param weeklyList
     * @return
     */
    Integer registerBulkWeekly(List<Map<String, Object>> weeklyList);

    /**
     * 태그 등록
     * @param tagList
     * @return
     */
    Integer registerBulkTag(List<Map<String, Object>> tagList);

    /**
     * 무료 회차 등록
     * @param contentsList
     * @return
     */
    Integer registerBulkEventFree(List<ContentsDto> contentsList);

    /**
     * 작가 조회
     * @param trim
     * @return
     */
    Integer getAuthor(String trim);

    /**
     * 작가 등록(author)
     * @param authorDto
     * @return
     */
    Integer registerAuthor(AuthorDto authorDto);

    /**
     * 작가 매핑 대량 등록
     * @param authorList
     * @return
     */
    Integer registerBulkAuthor(List<AuthorDto> authorList);

    /**
     * 작품 CP 등록
     * @param contentsList
     * @return
     */
    Integer registerBulkContentsCp(List<ContentsDto> contentsList);

    /**
     * 작품 이미지 등록
     * @param uploadResponse
     * @return
     */
    Integer registerBulkContentsImage(List<HashMap<String, Object>> uploadResponse);

    /**
     * 회차 대량 등록
     * @param episodeList
     * @return
     */
    Integer registerBulkEpisode(List<EpisodeDto> episodeList);

    /**
     * 회차 이벤트(할인) 등록
     * @param episodeList
     * @return
     */
    Integer registerBulkEpisodeEventCoin(List<EpisodeDto> episodeList);

    /**
     * 회차 추가 정보 대량 등록
     * @param episodeList
     * @return
     */
    Integer registerBulkEpisodeInfo(List<EpisodeDto> episodeList);

    /**
     * 회차 이미지 등록
     * @param uploadResponse
     * @return
     */
    Integer registerBulkEpisodeImage(List<HashMap<String, Object>> uploadResponse);

    /**
     * 뷰어 웹툰 이미지 등록
     * @param uploadResponse
     * @return
     */
    Integer registerBulkWebtoonImage(List<HashMap<String, Object>> uploadResponse);

    /**
     * 뷰어 만화 이미지 등록
     * @param uploadResponse
     * @return
     */
    Integer registerBulkComicImage(List<HashMap<String, Object>> uploadResponse);

    /**
     * 뷰어 소설 이미지 등록
     * @param uploadResponse
     * @return
     */
    Integer registerBulkNovelImage(List<HashMap<String, Object>> uploadResponse);

    /**
     * 뷰어 소설 내용 등록
     * @param viewerData
     * @return
     */
    Integer registerBulkViewerNovel(List<Map<String, Object>> viewerData);

    /**
     * 작품 마지막 회차 정보 등록
     * @param lastEpisodeList
     * @return
     */
    Integer modifyLastEpisode(List<EpisodeDto> lastEpisodeList);
}
