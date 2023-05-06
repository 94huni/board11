package com.board.board.DTO;

import com.board.board.Entity.Category;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@NoArgsConstructor
@Data
public class BoardCreateFormDTO {
    private String title;
    private String content;
    private MultipartFile multipartFile;
    private List<Category> category;
}
