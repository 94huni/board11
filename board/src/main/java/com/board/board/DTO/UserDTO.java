package com.board.board.DTO;

import com.board.board.Entity.UserRole;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Data
public class UserDTO {
    private String username;

    private String email;

    private String nickname;

    private LocalDateTime createAt;

    List<UserRole> userRoles;
}
