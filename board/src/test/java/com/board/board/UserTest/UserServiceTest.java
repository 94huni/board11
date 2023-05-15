package com.board.board.UserTest;

import com.board.board.Config.Jwt.JwtProvider;
import com.board.board.DTO.UserDTO;
import com.board.board.DTO.UserUpdateFormDTO;
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
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @InjectMocks
    private UserService userService;

    @Test
    public void getUserTest() {
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
        assertEquals("userTest@test.com", userDTO.getEmail());
        assertEquals(Collections.singletonList(UserRole.ROLE_USER), userDTO.getUserRoles());
        assertEquals(user.getCreateAt(), userDTO.getCreateAt());

    }

    @Test
    public void getUserWithBoardAndCommentCountTest() {
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
        for (int i = 0; i < 10; i++) {
            Board board = new Board();
            board.setUser(user);
            board.setTitle("Test" + i);
            board.setContent("Test" + i);
            boards.add(board);
        }
        user.setBoardList(boards);

        List<Comment> comments = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
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
        assertEquals("userTest@test.com", userDTO.getEmail());
        assertEquals(Collections.singletonList(UserRole.ROLE_USER), userDTO.getUserRoles());
        assertEquals(user.getCreateAt(), userDTO.getCreateAt());
        assertEquals(user.getBoardList().size(), userDTO.getBoard_count());
        assertEquals(user.getCommentList().size(), userDTO.getComment_count());
    }

    @Test
    public void invalidTokenUser() {
        String token = "token";
        Long id = 1L;

        when(jwtProvider.validateToken(token)).thenReturn(false);

        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.getUser(id, token);
        });

        assertEquals("만료되었거나 토큰이 잘못됐습니다!", exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getHttpStatus());
    }

    @Test
    public void notFoundGetUser() {
        Long id = 1L;
        String token = "token";

        when(jwtProvider.validateToken(token)).thenReturn(true);
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.getUser(id, token);
        });

        assertEquals("정보를 찾을수 없습니다!", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }

    @Test
    public void getAllUser_SuccessfulTest() {
        String token = "AllUserTest";
        when(jwtProvider.validateToken(token)).thenReturn(true);
        int page = 0;
        int size = 10;
        String search = "test";
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        List<User> users = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            User user = new User();
            user.setId((long) i);
            user.setPassword(bCryptPasswordEncoder.encode("1234"));
            user.setUsername("AllUserTest" + i);
            user.setNickname("TestUser" + i);
            user.setEmail("Test" + i + "@test.com");
            user.setUserRoles(Collections.singletonList(UserRole.ROLE_USER));
            user.setCommentList(null);
            user.setBoardList(null);
            users.add(user);
        }
        Page<User> userPage = new PageImpl<>(users, pageable, 5);
        when(userRepository.findByUsernameContaining(search, pageable)).thenReturn(userPage);

        Page<UserDTO> userDTOPage = userService.getAllUserPage(page, size, search, token);
        assertNotNull(userDTOPage);
        assertEquals(page, userDTOPage.getNumber());
        assertEquals(size, userDTOPage.getSize());
        assertEquals(5, userDTOPage.getTotalElements());
        assertEquals(1, userDTOPage.getTotalPages());

        List<UserDTO> userDtoList = userDTOPage.getContent();
        UserDTO firstUserDTO = userDtoList.get(0);
        assertEquals("AllUserTest0", firstUserDTO.getUsername());
        assertEquals("Test0@test.com", firstUserDTO.getEmail());
        assertEquals("TestUser0", firstUserDTO.getNickname());

    }

    @Test
    public void getAllUser_NotSearchSuccessful() {
        String token = "AllUserTest";
        when(jwtProvider.validateToken(token)).thenReturn(true);
        int page = 0;
        int size = 10;
        String search = null;
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        List<User> users = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            User user = new User();
            user.setId((long) i);
            user.setPassword(bCryptPasswordEncoder.encode("1234"));
            user.setUsername("AllUserTest" + i);
            user.setNickname("TestUser" + i);
            user.setEmail("Test" + i + "@test.com");
            user.setUserRoles(Collections.singletonList(UserRole.ROLE_USER));
            user.setCommentList(null);
            user.setBoardList(null);
            users.add(user);
        }
        Page<User> userPage = new PageImpl<>(users, pageable, 5);
        when(userRepository.findByUsernameContaining(search, pageable)).thenReturn(userPage);

        Page<UserDTO> userDTOPage = userService.getAllUserPage(page, size, search, token);
        assertNotNull(userDTOPage);
        assertEquals(page, userDTOPage.getNumber());
        assertEquals(size, userDTOPage.getSize());
        assertEquals(5, userDTOPage.getTotalElements());
        assertEquals(1, userDTOPage.getTotalPages());

        List<UserDTO> userDtoList = userDTOPage.getContent();
        UserDTO firstUserDTO = userDtoList.get(0);
        assertEquals("AllUserTest0", firstUserDTO.getUsername());
        assertEquals("Test0@test.com", firstUserDTO.getEmail());
        assertEquals("TestUser0", firstUserDTO.getNickname());
    }

    @Test
    public void getAllUser_SearchFail() {

        String token = "AllUserTest";
        when(jwtProvider.validateToken(token)).thenReturn(true);
        int page = 0;
        int size = 10;
        String search = "Fail";
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        List<User> users = new ArrayList<>();

        Page<User> userPage = new PageImpl<>(users, pageable, 5);
        when(userRepository.findByUsernameContaining(search, pageable)).thenReturn(null);

        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.getAllUserPage(page, size, search, token);
        });

        assertEquals("검색결과가 없습니다!", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }

    @Test
    public void updateUser_PasswordNull_SuccessfulTest() {
        String token = "token";
        UserUpdateFormDTO userUpdateFormDTO = new UserUpdateFormDTO();
        userUpdateFormDTO.setNickname("new_nickname");
        userUpdateFormDTO.setEmail("new_email@test.com");

        User user = new User();
        user.setUsername("username");
        user.setEmail("old_email@test.com");
        user.setNickname("old_nickname");

        when(jwtProvider.validateToken(token)).thenReturn(true);
        when(jwtProvider.getUsername(token)).thenReturn(user.getUsername());
        when(userRepository.findByUsername("username")).thenReturn(user);

        UserDTO result = userService.updateUser(userUpdateFormDTO, token);

        assertEquals("username", result.getUsername());
        assertEquals("new_nickname", result.getNickname());
        assertEquals("new_email@test.com", result.getEmail());

        verify(userRepository).save(user);
    }

    @Test
    public void deleteUser_SuccessfulTest() {
        String token = "token";

        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(jwtProvider.validateToken(token)).thenReturn(true);
        when(jwtProvider.getUsername(token)).thenReturn(user.getUsername());

        boolean isDelete = userService.deleteUser(user.getId(), token);

        assertTrue(isDelete);
        verify(userRepository).delete(user);
    }

    @Test
    public void deleteUser_AuthenticationFail() {
        String token = "token";

        User user = new User();
        user.setId(1L);
        user.setUsername("TestUser");

        User falseUser = new User();
        falseUser.setId(2L);
        falseUser.setUsername("Test");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(jwtProvider.validateToken(token)).thenReturn(true);
        when(jwtProvider.getUsername(token)).thenReturn(falseUser.getUsername());

        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.deleteUser(user.getId(), token);
        });

        assertEquals("권한이 없습니다!", exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getHttpStatus());
    }

}
