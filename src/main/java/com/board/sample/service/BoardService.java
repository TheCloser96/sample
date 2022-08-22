/**
 *  Board - Service
 */

package com.board.sample.service;

import com.board.sample.domain.Account;
import com.board.sample.domain.Board;
import com.board.sample.domain.status.BoardRole;
import com.board.sample.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;


    @Transactional
    public Long save(Board board) {
        Board save = boardRepository.save(board);
        return save.getId();
    }


    public Page<Board> findByBoardRole(BoardRole boardRole, Pageable pageable) {
        return boardRepository.findByBoardRole(boardRole, pageable);
    }


    public Page<Board> findByBoardRoleAndTitle(BoardRole boardRole, String title, Pageable pageable) {
        return boardRepository.findByBoardRoleAndTitle(boardRole, title, pageable);
    }


    public Optional<Board> findById(Long boardId) {
        return boardRepository.findById(boardId);
    }


    public Page<Board> findBoardFromAccountId(Long accountId, BoardRole boardRole, Pageable pageable) {
        return boardRepository.findBoardFromAccountId(accountId, boardRole, pageable);
    }


    @Transactional
    public void modifyBoardInfo(Board board, String title, LocalDateTime recordTime) {
        board.modifyBoardInfo(title, recordTime);
    }


    @Transactional
    public void softDeleteBoard(Board board) {
        board.softDeleteBoard();
    }
}
