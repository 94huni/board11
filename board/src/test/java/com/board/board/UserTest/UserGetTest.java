package com.board.board.UserTest;

import com.board.board.Config.Jwt.JwtProvider;
import com.board.board.DTO.BoardDTO;
import com.board.board.DTO.UserDTO;
import com.board.board.Entity.Board;
import com.board.board.Entity.Comment;
import com.board.board.Entity.User;
import com.board.board.Entity.UserRole;
import com.board.board.Exception.CustomException;
import com.board.board.Repository.UserRepository;
import com.board.board.Service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserGetTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @InjectMocks
    private UserService userService;

    @Test
    public void getUserTest(){
        String token = "token";

        User user = new User();
        user.setUsername("userTest");
        user.setPassword(bCryptPasswordEncoder.encode("1234"));
        user.setEmail("userTest@test.com");
        user.setNickname("TestUser");
        user.setCreateAt(LocalDateTime.now());
        user.setId(1L);
        user.setUserRoles(Collections.singletonList(UserRole.ROLE_USER));

        when(jwtProvider.validateToken(token)).thenReturn(true);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserDTO userDTO = userService.getUser(user.getId(), token);
//        List<UserRole> userRoles = Collections.singletonList(UserRole.ROLE_USER);

        assertNotNull(userDTO);
        assertEquals("userTest", userDTO.getUsername());
        assertEquals("userTest@test.com",userDTO.getEmail());
        assertEquals(Collections.singletonList(UserRole.ROLE_USER) , userDTO.getUserRoles());
        assertEquals(user.getCreateAt(), userDTO.getCreateAt());

    }

    @Test
    public void getUserWithBoardAndCommentCountTest(){
        String token = "token";

        User user = new User();
        user.setUsername("userTest");
        user.setPassword(bCryptPasswordEncoder.encode("1234"));
        user.setEmail("userTest@test.com");
        user.setNickname("TestUser");
        user.setCreateAt(LocalDateTime.now());
        user.setId(1L);
        user.setUserRoles(Collections.singletonList(UserRole.ROLE_USER));

        when(jwtProvider.validateToken(token)).thenReturn(true);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        List<Board> boards = new ArrayList<>();
        for(int i=0; i<10; i++){
            Board board = new Board();
            board.setUser(user);
            board.setTitle("Test" + i);
            board.setContent("Test" + i);
            boards.add(board);
        }
        user.setBoardList(boards);

        List<Comment> comments = new ArrayList<>();
        for (int i=0; i<10; i++){
            Comment comment = new Comment();
            comment.setUser(user);
            comment.setBoard(boards.get(i));
            comment.setContent("Test" + i);
            comments.add(comment);
        }
        user.setCommentList(comments);

        UserDTO userDTO = userService.getUser(user.getId(), token);

        assertNotNull(userDTO);
        assertEquals("userTest", userDTO.getUsername());
        assertEquals("userTest@test.com",userDTO.getEmail());
        assertEquals(Collections.singletonList(UserRole.ROLE_USER) , userDTO.getUserRoles());
        assertEquals(user.getCreateAt(), userDTO.getCreateAt());
        assertEquals(user.getBoardList().size(), userDTO.getBoard_count());
        assertEquals(user.getCommentList().size(), userDTO.getComment_count());
    }

    @Test
    public void invalidTokenGetUser(){
        String token = "token";
        Long id = 1L;

        when(jwtProvider.validateToken(token)).thenReturn(false);

        CustomException exception = assertThrows(CustomException.class, () -> {
           userService.getUser(id,token);
        });

        assertEquals("만료되었거나 토큰이 잘못됐습니다!", exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getHttpStatus());
    }

    @Test
    public void notFoundGetUser(){
        Long id = 1L;
        String token = "token";

        when(jwtProvider.validateToken(token)).thenReturn(true);
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, ()->{
            userService.getUser(id, token);
        });

        assertEquals("정보를 찾을수 없습니다!", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }


}
