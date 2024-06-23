package com.architecture.admin.models.daosub.comment;

import com.architecture.admin.models.dto.SearchDto;
import com.architecture.admin.models.dto.comment.ContentCommentDto;
import com.architecture.admin.models.dto.comment.ContentReplyDto;
import com.architecture.admin.models.dto.comment.EpisodeCommentDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface CommentDaoSub {

    /**************************************************************************************
     * 작품 목록
     **************************************************************************************/

    /**
     * 작품 목록 개수 카운트
     *
     * @param searchDto
     * @return
     */
    int getTotalContentCount(SearchDto searchDto);

    /**
     * 작품 목록 조회
     *
     * @param searchDto
     * @return
     */
    List<ContentCommentDto> getContentList(SearchDto searchDto);

    /**************************************************************************************
     * 회차 목록
     **************************************************************************************/

    /**
     * 회차 목록 개수 카운트
     *
     * @param searchDto
     * @return
     */
    int getTotalEpisodeCount(SearchDto searchDto);

    /**
     * 회차 목록 조회
     *
     * @param searchDto
     * @return
     */
    List<EpisodeCommentDto> getEpisodeList(SearchDto searchDto);

    /**************************************************************************************
     * 작품 댓글 & 대댓글 목록
     **************************************************************************************/

    /**
     * 작품 댓글 목록 개수 카운트
     *
     * @param searchDto
     * @return
     */
    int getContentCommentTotalCnt(SearchDto searchDto);

    /**
     * 작품 댓글 목록 조회
     *
     * @param searchDto
     * @return
     */
    List<ContentCommentDto> getContentCommentList(SearchDto searchDto);

    /**
     * 대댓글 목록 개수 카운트
     *
     * @param searchDto
     * @return
     */
    int getContentReplyTotalCnt(SearchDto searchDto);

    /**
     * 대댓글 목록 조회
     *
     * @param searchDto
     * @return
     */
    List<ContentReplyDto> getContentReplyList(SearchDto searchDto);

    /**
     * 선택한 댓글 idx가 유효한지 체크
     *
     * @param idx
     * @return
     */
    int getContentCommentIdxCnt(Long idx);

    /**
     * 선택한 작품 댓글 정보 조회
     *
     * @param idx
     * @return
     */
    ContentCommentDto getContentComment(Long idx);

    /**************************************************************************************
     * 회차 댓글 목록
     **************************************************************************************/

    /**
     * 회차 댓글 목록 개수 카운트
     *
     * @param searchDto
     * @return
     */
    int getEpisodeCommentTotalCnt(SearchDto searchDto);

    /**
     * 회차 댓글 목록 조회
     *
     * @param searchDto
     * @return
     */
    List<EpisodeCommentDto> getEpisodeCommentList(SearchDto searchDto);

    /**
     * 선택한 회차 댓글 정보 조회
     *
     * @param idx
     * @return
     */
    EpisodeCommentDto getEpisodeComment(Long idx);

}
