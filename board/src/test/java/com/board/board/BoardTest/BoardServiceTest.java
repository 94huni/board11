package com.board.board.BoardTest;

import com.board.board.Config.Jwt.JwtProvider;
import com.board.board.DTO.BoardCreateFormDTO;
import com.board.board.DTO.BoardDTO;
import com.board.board.Entity.Board;
import com.board.board.Entity.Category;
import com.board.board.Entity.User;
import com.board.board.Exception.CustomException;
import com.board.board.Repository.BoardRepository;
import com.board.board.Repository.UserRepository;
import com.board.board.Service.BoardService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

@SpringBootTest
public class BoardServiceTest {
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private BoardService boardService;

    @Test
    public void createBoard_Successful() {
        String token = "token";

        BoardCreateFormDTO boardCreateFormDTO = new BoardCreateFormDTO();
        boardCreateFormDTO.setTitle("Test_Title");
        boardCreateFormDTO.setContent("Test_Content");
        boardCreateFormDTO.setCategory(List.of(Category.FREE));

        User user = new User();
        user.setId(1L);
        user.setUsername("TestUser");
        user.setNickname("TestUser");

        when(jwtProvider.validateToken(token)).thenReturn(true);
        when(userRepository.findByUsername(jwtProvider.getUsername(token))).thenReturn(user);

        BoardDTO result = boardService.createBoard(boardCreateFormDTO, token);

        assertEquals(result.getNickname(), "TestUser");
        assertEquals(result.getTitle(), "Test_Title");
        assertEquals(result.getContent(), "Test_Content");

        verify(boardRepository).save(any(Board.class));

    }

    @Test
    public void createBoard_InvalidToken() {
        String token = "token";

        BoardCreateFormDTO boardCreateFormDTO = new BoardCreateFormDTO();

        when(jwtProvider.validateToken(token)).thenReturn(false);

        CustomException exception = assertThrows(CustomException.class, ()->{
           boardService.createBoard(boardCreateFormDTO, token);
        });

        assertEquals("만료되었거나 토큰이 잘못됐습니다!", exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getHttpStatus());
    }
}