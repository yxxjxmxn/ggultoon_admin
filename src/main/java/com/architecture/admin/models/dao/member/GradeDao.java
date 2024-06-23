package com.architecture.admin.models.dao.member;

import com.architecture.admin.models.dto.member.MemberGradeDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
@Mapper
public interface GradeDao {

    /*****************************************************
     * Modify
     ****************************************************/

    void modifyGrade(MemberGradeDto memberGradeDto);


    /*****************************************************
     * Select
     ****************************************************/

    /**
     * 결제 금액
     * @param map
     * @return
     */
    Integer getMemberPayment(Map<String, Object> map);

}
