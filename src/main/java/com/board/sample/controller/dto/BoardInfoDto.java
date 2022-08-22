package com.board.sample.controller.dto;

import com.board.sample.domain.Board;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BoardInfoDto {

    private List<Board> contents;
    private String keyword;
    private int number;
    private int totalPages;
    private int first;
    private int second;
    private int third;

}
