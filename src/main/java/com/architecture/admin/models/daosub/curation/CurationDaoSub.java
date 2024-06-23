package com.architecture.admin.models.daosub.curation;

import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.curation.CurationContentDto;
import com.architecture.admin.models.dto.curation.CurationDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface CurationDaoSub {

    /**
     * 작품 큐레이션 개수 조회
     * state = 1,2만 조회
     * @return
     */
    int getCurationTotalCnt(SearchDto searchDto);

    /**
     * 작품 큐레이션 리스트 조회
     * state = 1,2만 조회
     * @return
     */
    List<CurationDto> getCurationList(SearchDto searchDto);

    /**
     * 작품 큐레이션 상세 조회
     * @return
     */
    CurationDto getViewCuration(Long idx);

    /**
     * 작품 큐레이션 노출 영역 개수 조회
     * @return
     */
    int getCurationAreaTotalCnt(SearchDto searchDto);

    /**
     * 작품 큐레이션 노출 영역 리스트 조회
     * @return
     */
    List<CurationDto> getCurationAreaList(SearchDto searchDto);

    /**
     * 작품 큐레이션 노출 영역 상세
     * @return
     */
    CurationDto getViewCurationArea(Long idx);

    /**
     * 큐레이션 매핑 작품 개수 조회
     * @return
     */
    int getCurationContentTotalCnt(SearchDto searchDto);

    /**
     * 큐레이션 매핑 작품 리스트 조회
     * @return
     */
    List<CurationContentDto> getCurationContentList(SearchDto searchDto);

    /**
     * 큐레이션 매핑 작품 상세
     * @return
     */
    CurationContentDto getViewCurationContent(CurationContentDto dto);

    /**
     * 큐레이션 매핑 작품 검색 결과 개수 조회
     * @return
     */
    int getSearchContentTotalCnt(SearchDto searchDto);

    /**
     * 큐레이션 매핑 작품 검색 결과 리스트 조회
     * @return
     */
    List<CurationContentDto> getSearchContentList(SearchDto searchDto);

    /**
     * 큐레이션 매핑 작품 중복 여부 체크
     * @return
     */
    int getContentTotalCnt(CurationContentDto curationContentDto);
}
