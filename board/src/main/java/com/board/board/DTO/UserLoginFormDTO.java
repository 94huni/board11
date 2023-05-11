package com.board.board.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserLoginFormDTO {
    private String username;
    private String password;
}
