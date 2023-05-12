package com.board.board.UserTest;

import com.board.board.Config.Jwt.JwtProvider;
import com.board.board.DTO.UserDTO;
import com.board.board.Entity.User;
import com.board.board.Entity.UserRole;
import com.board.board.Repository.UserRepository;
import com.board.board.Service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
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
}
