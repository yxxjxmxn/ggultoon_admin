package com.architecture.admin.models.dao.admin;

import com.architecture.admin.models.dto.admin.AdminDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


@Repository
@Mapper
public interface AdminDao {

    /**
     * 관리자 등록
     * @param adminDto
     * @return
     */
    Integer insertAdmin(AdminDto adminDto);

    /**
     * 관리자 마지막 로그인 날짜 업데이트
     * @param adminDto
     * @return
     */
    Integer updateLastDate(AdminDto adminDto);

    /**
     * 관리자 수정
     * @param adminDto
     * @return
     */
    int modifyAdmin(AdminDto adminDto);

    /**
     * 내 정보 수정
     * @param adminDto
     * @return
     */
    int modifyMyPage(AdminDto adminDto);

    /**
     * 비밀번호 변경
     * @param adminDto
     * @return
     */
    int modifyPassword(AdminDto adminDto);
}
