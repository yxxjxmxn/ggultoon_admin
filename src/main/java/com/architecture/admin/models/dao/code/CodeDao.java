package com.architecture.admin.models.dao.code;

import com.architecture.admin.models.dto.contents.CodeDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface CodeDao {

    /**
     * 코드 추가
     * @param codeDto
     * @return
     */
    Integer register(CodeDto codeDto);
}
