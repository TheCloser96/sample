package com.board.sample.repository;

import com.board.sample.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {


    @Query("select c from Comment c join c.board b where b.id = :id")
    Page<Comment> findCommentFromBoardId(@Param("id") Long boardId, Pageable pageable);   //해당 Board id값과 해당하는 Comment를 조회(페이징 적용)

    @Query("select c from Comment c join c.account a where a.id = :id")
    Page<Comment> findCommentFromAccountId(@Param("id") Long accountId, Pageable pageable);   //해당 Account id값과 해당하는 Comment를 조회(페이징 적용)
}
