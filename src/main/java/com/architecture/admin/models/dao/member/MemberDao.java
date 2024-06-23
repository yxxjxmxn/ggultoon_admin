package com.architecture.admin.models.dao.member;

import com.architecture.admin.models.dto.member.MemberDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface MemberDao {
    /**
     * 회원 기본정보 수정
     *
     * @param memberDto
     * @return
     */
    int modifyMemberInfo(MemberDto memberDto);

    /**
     * 회원 닉네임 수정
     *
     * @param memberDto
     * @return
     */
    int modifyMemberNick(MemberDto memberDto);

    /**
     * 회원 탈퇴 처리(member 테이블)
     *
     * @param memberIdx
     * @return
     */
    int deleteMemberByAdmin(long memberIdx);

    /**
     * 회원 탈퇴 처리(info 테이블)
     *
     * @param memberIdx
     * @return
     */
    int deleteMemberInfoByAdmin(long memberIdx);

    /**
     * 회원 탈퇴 처리(member_out 테이블 insert)
     *
     * @param outMember
     */
    void insertMemberOutByAdmin(MemberDto outMember);

    /**
     * 회원 복구 처리(member 테이블)
     *
     * @param memberIdx
     */
    void restoreMemberInfoByAdmin(Long memberIdx);

    /**
     * 회원 복구 처리(info 테이블)
     *
     * @param memberIdx
     */
    void restoreMemberByAdmin(Long memberIdx);

    /**
     * 회원 복구 처리(member_out 테이블에서 row 삭제)
     *
     * @param memberIdx
     */
    void deleteMemberOut(Long memberIdx);

    /**
     * 회원 복구 처리(member_simple_out 테이블에서 row 삭제)
     *
     * @param memberIdx
     */
    void deleteMemberSimpleOut(Long memberIdx);

}
