package com.architecture.admin.models.daosub.contents;

import com.architecture.admin.models.dto.contents.CategoryDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface CategoryDaoSub {

    /**
     * 카테고리 목록
     * @return
     */
    List<CategoryDto> getCategory();
}
