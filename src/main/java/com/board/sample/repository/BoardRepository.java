package com.board.sample.repository;

import com.board.sample.domain.Board;
import com.board.sample.domain.status.BoardRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    Page<Board> findByBoardRole(BoardRole boardRole, Pageable pageable); //조회가 가능한 전체 게시물을 조회(페이징 적용)

    Page<Board> findByBoardRoleAndTitle(BoardRole boardRole, String title, Pageable pageable); //조회가 가능한 게시물들을 에서 제목을 기준으로 전체 게시물을 조회(페이징 적용)

    @Query("select b from Board b join b.account a where a.id = :id and b.boardRole = :boardRole")
    Page<Board> findBoardFromAccountId(@Param("id") Long accountId, @Param("boardRole") BoardRole boardRole, Pageable pageable);    //해당 사용자의 id값과 BoardRole이 Yes 기준에 부합하는 기준으로 전체 게시물을 조회(페이징 적용)
}
