package com.board.board.DTO;

import com.board.board.Entity.Board;
import com.board.board.Entity.Comment;
import com.board.board.Entity.UserRole;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class UserSearchFormDTO {
    private String username;

    private String email;

    private String nickname;

    private LocalDateTime createAt;

    private List<UserRole> userRoles;

    private List<Board> boardList;

    private List<Comment> commentList;
}
