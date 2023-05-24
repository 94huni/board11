package com.board.board.BoardTest;

import com.board.board.Config.Jwt.JwtProvider;
import com.board.board.DTO.BoardCreateFormDTO;
import com.board.board.DTO.BoardDTO;
import com.board.board.DTO.BoardUpdateFormDTO;
import com.board.board.Entity.Board;
import com.board.board.Entity.Category;
import com.board.board.Entity.User;
import com.board.board.Entity.UserRole;
import com.board.board.Exception.CustomException;
import com.board.board.Repository.BoardRepository;
import com.board.board.Repository.UserRepository;
import com.board.board.Service.BoardService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Test
    public void getBoard_Successful() {
        User user = new User();
        user.setNickname("Test_Nickname");
        Board board = new Board();
        board.setUser(user);
        board.setTitle("Test");
        board.setCategory(List.of(Category.FREE));
        board.setContent("Test_Content");
        board.setId(1L);

        when(boardRepository.findById(board.getId())).thenReturn(Optional.of(board));

        BoardDTO boardDTO = boardService.getBoard(board.getId());

        assertEquals("Test_Nickname", boardDTO.getNickname());
        assertEquals("Test", boardDTO.getTitle());
        assertEquals("Test_Content", boardDTO.getContent());
    }

    @Test
    public void getBoard_NotFoundBoard() {
        Long id = 1L;

        when(boardRepository.findById(id)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, ()->{
            boardService.getBoard(id);
        });

        assertEquals("글정보를 찾을 없습니다!", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }

    @Test
    public void getBoardPageByCategory_Successful() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Category category = Category.FREE;

        List<User> users = new ArrayList<>();
        for (int i=0; i<5; i++){
            User user = new User();
            user.setId((long) i);
            user.setEmail("TestEmail"+i);
            user.setPassword("1234");
            user.setNickname("TestNickname"+i);
            user.setUsername("TestUsername"+i);
            user.setUserRoles(List.of(UserRole.ROLE_USER));
            users.add(user);
        }

        List<Board> boards = new ArrayList<>();
        for(int i=0; i<5; i++){
            Board board = new Board();
            board.setId((long) i);
            board.setTitle("TestTitle"+i);
            board.setContent("TestContent"+i);
            board.setUser(users.get(i));
            board.setCategory(List.of(Category.FREE));
            boards.add(board);
        }

        Page<Board> boardPage = new PageImpl<>(boards, pageable, 5);

        when(boardRepository.findByCategory(category, pageable)).thenReturn(boardPage);

        Page<BoardDTO> resultBoard = boardService.getCategoryBoardPage(category, page, size);
        List<BoardDTO> result = resultBoard.getContent();
        BoardDTO resultPage = result.get(0);

        assertEquals("TestNickname0", resultPage.getNickname());
        assertEquals("TestContent0", resultPage.getContent());
        assertEquals("TestTitle0", resultPage.getTitle());

    }

    @Test
    public void getBoardPageByCategory_BadRequestCategory() {
        int size = 10;
        int page = 0;
        Pageable pageable = PageRequest.of(page, size);

        when(boardRepository.findByCategory(null, pageable)).thenReturn(null);

        CustomException exception = assertThrows(CustomException.class, ()->{
            boardService.getCategoryBoardPage(null,page,size);
        });

        assertEquals(exception.getHttpStatus(), HttpStatus.BAD_REQUEST);
        assertEquals(exception.getMessage(), "카테고리를 설정하지 않았습니다!");
    }

    @Test
    public void getBoardPageByUser_Successful() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        String token = "Token";

        User user = new User();
        user.setId(1L);
        user.setUsername("TestUser");
        user.setEmail("testUser@test.com");
        user.setNickname("TestNickname");
        user.setPassword("1234");

        List<Board> boards = new ArrayList<>();
        for(int i=0; i<5; i++){
            Board board = new Board();
            board.setId((long) i);
            board.setTitle("TestTitle"+i);
            board.setContent("TestContent"+i);
            board.setUser(user);
            board.setCategory(List.of(Category.FREE));
            boards.add(board);
        }

        Page<Board> boardPage = new PageImpl<>(boards, pageable, 5);

        when(boardRepository.findByUser(pageable, user)).thenReturn(boardPage);
        when(jwtProvider.validateToken(token)).thenReturn(true);
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        Page<BoardDTO> boardDTOS = boardService.getUserBoardPage(token, user.getUsername(), page, size);
        List<BoardDTO> resultPage = boardDTOS.getContent();
        BoardDTO boardDTO = resultPage.get(0);

        assertEquals("TestNickname", boardDTO.getNickname());
        assertEquals("TestContent0", boardDTO.getContent());
        assertEquals("TestTitle0", boardDTO.getTitle());
    }

    @Test
    public void getBoardPageByUser_UserNotFound() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        User user = new User();
        user.setId(1L);
        user.setUsername("TestName");
        user.setNickname("Test");
        user.setEmail("test@test.com");

        String token = "token";

        List<Board> boards = new ArrayList<>();
        Board board = new Board();
        board.setUser(user);
        board.setTitle("Title");
        board.setContent("Content");
        boards.add(board);
        Page<Board> boardPage = new PageImpl<>(boards, pageable, 1);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(null);
        when(jwtProvider.validateToken(token)).thenReturn(true);
        when(boardRepository.findByUser(pageable, user)).thenReturn(boardPage);

        CustomException exception = assertThrows(CustomException.class, ()->{
            boardService.getUserBoardPage(token, user.getUsername(), page, size);
        });

        assertEquals("유저정보를 찾을 수 없습니다!", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }

    @Test
    public void getBoardPageByUser_BoardNotFound() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        User user = new User();
        user.setId(1L);
        user.setUsername("TestName");
        user.setNickname("Test");
        user.setEmail("test@test.com");

        String token = "token";

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(jwtProvider.validateToken(token)).thenReturn(true);
        when(boardRepository.findByUser(pageable, user)).thenReturn(null);

        CustomException exception = assertThrows(CustomException.class, ()-> {
            boardService.getUserBoardPage(token, user.getUsername(), page, size);
        });

        assertEquals("게시글 정보를 찾을 수 없습니다!", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }

    @Test
    public void updateBoard_Successful() {
        String token = "token";
        User user = new User();
        user.setId(1L);
        user.setEmail("testEmail@test.com");
        user.setNickname("TestNickname");
        user.setUsername("testUser");

        Board board = new Board();
        board.setId(1L);
        board.setTitle("TestTitle");
        board.setCategory(List.of(Category.FREE));
        board.setUser(user);
        board.setContent("TestContent");

        BoardUpdateFormDTO boardUpdateFormDTO = new BoardUpdateFormDTO();
        boardUpdateFormDTO.setTitle("UpdateTitle");
        boardUpdateFormDTO.setContent("UpdateContent");
        boardUpdateFormDTO.setCategory(List.of(Category.GAME));

        when(jwtProvider.validateToken(token)).thenReturn(true);
        when(userRepository.findByUsername(jwtProvider.getUsername(token))).thenReturn(user);
        when(boardRepository.findById(board.getId())).thenReturn(Optional.of(board));

        BoardDTO boardDTO = boardService.updateBoard(board.getId(), boardUpdateFormDTO, token);

        assertEquals("UpdateContent", boardDTO.getContent());
        assertEquals("UpdateTitle", boardDTO.getTitle());
        assertEquals("[GAME]", boardDTO.getCategory().toString());
    }
}
