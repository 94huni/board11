package com.board.board.CommentTest;

import com.board.board.Config.Jwt.JwtProvider;
import com.board.board.DTO.CommentDTO;
import com.board.board.Entity.Board;
import com.board.board.Entity.Category;
import com.board.board.Entity.User;
import com.board.board.Repository.BoardRepository;
import com.board.board.Repository.CommentRepository;
import com.board.board.Repository.UserRepository;
import com.board.board.Service.CommentService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CommentTest {
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private JwtProvider jwtProvider;
    @InjectMocks
    private CommentService commentService;

    @Test
    public void createComment_Successful() {
        String token = "token";

        String content = "Comment_Test";

        User user = new User();
        user.setId(1L);
        user.setUsername("TestUser");
        user.setNickname("TestNickname");
        user.setEmail("test@test.com");

        Board board = new Board();
        board.setId(1L);
        board.setTitle("TestTitle");
        board.setContent("TestContent");
        board.setCategory(List.of(Category.FREE));
        board.setUser(user);


        when(jwtProvider.validateToken(token)).thenReturn(true);
        when(userRepository.findByUsername(jwtProvider.getUsername(token))).thenReturn(user);
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));

        CommentDTO commentDTO = commentService.createComment(1L, content, token);

        assertEquals("Comment_Test", commentDTO.getContent());
    }
}
