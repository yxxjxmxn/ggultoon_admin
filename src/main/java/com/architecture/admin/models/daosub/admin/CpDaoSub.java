package com.architecture.admin.models.daosub.admin;

import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.admin.CpDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface CpDaoSub {

    /**
     * CP관리자 전체 카운트
     * @param searchDto
     * @return
     */
    int getCpTotalCnt(SearchDto searchDto);

    /**
     * CP관리자 목록 전체 조회
     * @param searchDto
     * @return
     */
    List<CpDto> getCpList(SearchDto searchDto);

    /**
     * CP관리자 상세
     * @param idx
     * @return
     */
    CpDto getViewCp(Long idx);


    /**
     * 회사명 목록
     * @return
     */
    List<CpDto> getCompanyList();
}
