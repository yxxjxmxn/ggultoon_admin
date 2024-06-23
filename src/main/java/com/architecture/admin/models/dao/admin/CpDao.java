package com.architecture.admin.models.dao.admin;

import com.architecture.admin.models.dto.admin.AdminDto;
import com.architecture.admin.models.dto.admin.CpDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;


@Repository
@Mapper
public interface CpDao {

    /**
     *
     * @param cpDto
     * @return
     */
    int modifyCp(CpDto cpDto);

    /**
     *  cp관리자 등록
     * @param cpDto
     */
    void insert(CpDto cpDto);

    /**
     *  cp관리자 정보 등록
     * @param cpDto
     */
    void insertInfo(CpDto cpDto);

    /**
     *  cp관리자 수정
     * @param cpDto
     */
    int modify(CpDto cpDto);

    /**
     *  cp관리자 정보 수정
     * @param cpDto
     */
    int modifyInfo(CpDto cpDto);

    /**
     * cp관리자 파일 업로드
     * @param map
     * @return
     */
    int fileUpload( HashMap<String, Object> map);
}
