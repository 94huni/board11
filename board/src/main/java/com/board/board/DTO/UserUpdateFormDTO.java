package com.board.board.DTO;

import lombok.Data;

@Data
public class UserUpdateFormDTO {
    private String email;

    private String nickname;

    private String password;

    private String password2;
}
