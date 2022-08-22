package com.board.sample.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BoardViewDto {

    private Long boardNumber;
    private String boardTitle;
    private LocalDateTime firstRecordTime;
    private LocalDateTime modifyRecordTime;
    private String boardContents;

}
