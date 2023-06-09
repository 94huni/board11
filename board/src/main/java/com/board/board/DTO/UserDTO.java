package com.board.board.DTO;

import com.board.board.Entity.Board;
import com.board.board.Entity.Comment;
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

    private List<UserRole> userRoles;

    private int board_count;

    private int comment_count;

}
