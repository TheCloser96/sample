package com.board.sample.controller.dto;

import com.board.sample.domain.Board;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserBoardDto {

    private List<Board> boardList;
    private int boardIndexNumber;
    private int boardTotalPages;
    private List<Integer> boardIndexBlock;

}
