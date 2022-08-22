package com.board.sample.controller.dto;

import com.board.sample.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CommentsDto {

    private List<Comment> list;
    private int size;
    private int pageNumber;
    private Boolean hasNext;

}
