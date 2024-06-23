package com.architecture.admin.models.daosub.admin;

import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.admin.AdminDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface AdminDaoSub {

    /**
     * 관리자 id 중복 체크
     * @param adminDto
     * @return
     */
    Integer getCountById(AdminDto adminDto);

    /**
     * 관리자 로그인 성공 시 회원 정보 조회
     * @param adminDto
     * @return
     */
    AdminDto getInfoForLogin(AdminDto adminDto);

    /**
     * 관리자 전체 카운트
     * @param searchDto
     * @return
     */
    int getAdminTotalCnt(SearchDto searchDto);

    /**
     * 관리자 목록 전체 조회
     * @param searchDto
     * @return
     */
    List<AdminDto> getAdminList(SearchDto searchDto);

    /**
     * 관리자 상세
     * @param idx (admin.idx)
     * @return
     */
    AdminDto getViewAdmin(Long idx);

}
