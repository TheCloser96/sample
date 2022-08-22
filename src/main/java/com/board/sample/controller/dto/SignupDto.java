package com.board.sample.controller.dto;

import lombok.Data;

@Data
public class SignupDto {
    private String mail;
    private String nick;
    private String firstPass;
    private String secondPass;
}
