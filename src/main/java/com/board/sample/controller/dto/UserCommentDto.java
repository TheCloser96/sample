package com.board.sample.controller.dto;

import com.board.sample.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserCommentDto {

    private List<Comment> commentList;
    private int commentIndexNumber;
    private Boolean commentHasNext;
}
