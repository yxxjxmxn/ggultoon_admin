package com.architecture.admin.models.dao.curation;

import com.architecture.admin.models.dto.curation.CurationContentDto;
import com.architecture.admin.models.dto.curation.CurationDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface CurationDao {

    /**
     * 작품 큐레이션 순서 재정렬
     * @param curationDto
     * @return
     */
    int reorderCuration(CurationDto curationDto);

    /**
     * 작품 큐레이션 등록
     * @param curationDto
     * @return
     */
    void insertCuration(CurationDto curationDto);

    /**
     * 작품 큐레이션 매핑 노출 영역 등록
     * @param list
     * @return
     */
    int insertCurationAreaList(List<CurationDto> list);

    /**
     * 작품 큐레이션 수정
     * @param curationDto
     * @return
     */
    int updateCuration(CurationDto curationDto);

    /**
     * 작품 큐레이션 삭제
     *
     * (1) 단일 건 삭제
     * (2) 복수 건 삭제
     * @param idxList
     * @return
     */
    void deleteCuration(List<Long> idxList);

    /**
     * 작품 큐레이션 매핑 노출 영역 삭제
     * @param idxList
     * @return
     */
    void deleteCurationAreaList(List<Long> idxList);

    /**
     * 작품 큐레이션 매핑 작품 전체 삭제
     * @param idxList 큐레이션 idx 리스트
     * @return
     */
    void deleteAllCurationContent(List<Long> idxList);

    /**
     * 작품 큐레이션 노출 영역 등록
     * @param curationDto
     * @return
     */
    int insertCurationArea(CurationDto curationDto);

    /**
     * 작품 큐레이션 노출 영역 삭제
     * @param idx
     * @return
     */
    int deleteCurationArea(Long idx);

    /**
     * 작품 큐레이션 노출 영역 수정
     * @param curationDto
     * @return
     */
    int updateCurationArea(CurationDto curationDto);

    /**
     * 작품 큐레이션 매핑 작품 등록
     * @param curationContentDto
     * @return
     */
    int insertCurationContent(CurationContentDto curationContentDto);

    /**
     * 작품 큐레이션 매핑 작품 순서 재정렬
     * @param curationContentDto
     * @return
     */
    int reorderCurationContent(CurationContentDto curationContentDto);

    /**
     * 작품 큐레이션 매핑 작품 수정
     * @param curationContentDto
     * @return
     */
    int updateCurationContent(CurationContentDto curationContentDto);

    /**
     * 작품 큐레이션 매핑 작품 선택 삭제
     * @param idxList 큐레이션 매핑 idx 리스트
     * @return
     */
    void deleteCurationContent(List<Long> idxList);
}
