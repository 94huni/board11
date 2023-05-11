package com.board.board.DTO;

import com.board.board.Entity.Category;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
public class BoardUpdateFormDTO {
    private String title;
    private String content;
    private MultipartFile multipartFile;
    private List<Category> category;
}
