package com.board.sample.service;

import com.board.sample.domain.Comment;
import com.board.sample.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;


    @Transactional
    public Long save(Comment comment) {
        Comment save = commentRepository.save(comment);
        return save.getId();
    }


    public Page<Comment> findCommentFromBoard(Long boardId, Pageable pageable) {
        return commentRepository.findCommentFromBoardId(boardId, pageable);
    }


    public Page<Comment> findCommentFromAccountId(Long accountId, Pageable pageable) {
        return commentRepository.findCommentFromAccountId(accountId, pageable);
    }
}
