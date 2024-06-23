package com.architecture.admin.models.daosub.contents;

import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.contents.TagDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface TagDaoSub {

    /**
     * 작품 등록용 태그 개수 전체 카운트
     */
    int getRegisterTagTotalCnt(SearchDto searchDto);

    /**
     * 작품 등록용 태그 리스트 조회
     */
    List<TagDto> getRegisterTagList(SearchDto searchDto);

    /**
     * 작품 태그 개수 전체 카운트
     */
    int getTagTotalCnt(SearchDto searchDto);

    /**
     * 작품 태그 리스트 조회
     */
    List<TagDto> getTagList(SearchDto searchDto);

    /**
     * 작품 태그 상세
     */
    TagDto getViewTag(Integer idx);

    /**
     * 유효한 작품 태그 idx인지 조회
     */
    int getTagIdxCnt(Integer idx);

}
