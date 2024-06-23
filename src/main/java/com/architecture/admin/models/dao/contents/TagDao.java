package com.architecture.admin.models.dao.contents;

import com.architecture.admin.models.dto.contents.TagDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface TagDao {

    /**
     * 작품 태그 등록
     * @param tagDto
     * @return
     */
    int registerTag(TagDto tagDto);

    /**
     * 작품 태그 수정
     * @param tagDto
     * @return
     */
    int modifyTag(TagDto tagDto);

    /**
     * 작품 태그 삭제
     * @param idx
     * @return
     */
    int deleteTag(Integer idx);
}
