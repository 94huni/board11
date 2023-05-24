package com.board.board.CommentTest;

import com.board.board.Config.Jwt.JwtProvider;
import com.board.board.Repository.BoardRepository;
import com.board.board.Repository.CommentRepository;
import com.board.board.Repository.UserRepository;
import com.board.board.Service.CommentService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

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
}
