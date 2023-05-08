package com.board.board.DTO;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class BoardDTO {
    private String title;
    private String content;
    private String nickname;
    private List<String> category;
    private LocalDateTime createAt;
    private int comment_count;
}
