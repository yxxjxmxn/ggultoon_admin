package com.architecture.admin.models.daosub.code;

import com.architecture.admin.models.dto.contents.TagDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface CodeDaoSub {

    /**
     * 코드 목록
     * @return
     */
    List<TagDto> getList();
}
