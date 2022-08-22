package com.board.sample.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BeforeBoardInfo {

    private Long boardId;
    private String title;
    private String fileLocation;
    private String contents;

}
