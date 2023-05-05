package com.board.board.DTO;

import lombok.Data;

@Data
public class UserSignUpFormDTO {
    private String username;

    private String nickname;

    private String email;

    private String password;

    private String password2;
}
