package com.architecture.admin.models.daosub.member;

import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.member.MemberDto;
import com.architecture.admin.models.dto.member.MemberOttDto;
import com.architecture.admin.models.dto.member.MemberOttListDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface MemberDaoSub {

    /**
     * 전체 회원 집계 리스트 조회
     */
    List<MemberDto> getMemberCntList();

    /**
     * 회원 전체 개수
     *
     * @param searchDto
     * @return
     */
    int getMemberTotalCnt(SearchDto searchDto);

    /**
     * 회원 리스트
     *
     * @param searchDto
     * @return
     */
    List<MemberDto> getMemberList(SearchDto searchDto);

    /**
     * 회원 기본 정보 조회(회원 상세 페이지)
     *
     * @param memberIdx
     * @return
     */
    MemberDto getMemberBasicInfo(long memberIdx);

    /**
     * 닉네임 중복 검사
     *
     * @param nick
     * @return
     */
    int inspectDuplicateNick(String nick);

    /**
     * 회원 정책 정보 조회(회원 상세 페이지)
     *
     * @param searchDto
     * @return
     */
    List<MemberDto> getMemberPolicyInfo(SearchDto searchDto);
    /**
     * ott 가입 회원
     *
     * @param searchDto
     */
    List<MemberOttListDto> getMemberOtt(SearchDto searchDto);


    /**
     * ott 가입 회원 전체 개수
     *
     * @param searchDto
     * @return
     */
    int getMemberOttCnt(SearchDto searchDto);


    /**
     * ott 회원 통계
     *
     * @param searchDto
     */
    List<MemberOttDto> getOttMemberStat(SearchDto searchDto);

    /**
     * ott 회원 통계 전체 개수
     *
     * @param searchDto
     * @return
     */
    int getOttMemberStatCnt(SearchDto searchDto);

    /**
     * 전체 회원 idx 리스트 조회
     */
    List<Long> getAllMemberIdxList();

    /**
     * 탈퇴 회원 정보 조회
     *
     * @param memberIdx
     * @return
     */
    MemberDto getOutMemberInfo(Long memberIdx);


}
