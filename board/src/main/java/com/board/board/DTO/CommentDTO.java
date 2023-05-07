package com.board.board.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentDTO {
    private String content;
    private Long board_id;
    private String board_title;
    private String username;
}
