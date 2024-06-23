package com.architecture.admin.models.dao.comment;

import com.architecture.admin.models.dto.comment.ContentCommentDto;
import com.architecture.admin.models.dto.comment.EpisodeCommentDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface CommentDao {

    /**************************************************************************************
     * 작품 댓글 & 대댓글 목록
     **************************************************************************************/

    /**
     * 작품 댓글 숨김
     *
     * @param comment
     * @return
     */
    int hideContentComments(ContentCommentDto comment);

    /**
     * 작품 댓글 삭제
     *
     * @param comment
     * @return
     */
    int deleteContentComments(ContentCommentDto comment);

    /**
     * 작품 대댓글 숨김
     *
     * @param comment
     * @return
     */
    int hideContentReplies(ContentCommentDto comment);

    /**
     * 작품 대댓글 삭제
     *
     * @param comment
     * @return
     */
    int deleteContentReplies(ContentCommentDto comment);

    /**************************************************************************************
     * 회차 댓글
     **************************************************************************************/

    /**
     * 회차 댓글 숨김
     *
     * @param comment
     * @return
     */
    int hideEpisodeComments(EpisodeCommentDto comment);

    /**
     * 회차 댓글 삭제
     *
     * @param comment
     * @return
     */
    int deleteEpisodeComments(EpisodeCommentDto comment);
}
