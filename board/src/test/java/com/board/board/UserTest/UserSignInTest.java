package com.board.board.UserTest;

import com.board.board.Config.Jwt.JwtProvider;
import com.board.board.DTO.UserLoginFormDTO;
import com.board.board.Entity.User;
import com.board.board.Entity.UserRole;
import com.board.board.Exception.CustomException;
import com.board.board.Repository.UserRepository;
import com.board.board.Service.UserService;
import org.apache.tomcat.websocket.AuthenticationException;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
public class UserSignInTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private AuthenticationManager authenticationManager;
    @InjectMocks
    private UserService userService;

    @Test
    public void signIn_Successful() {
        UserLoginFormDTO userLoginFormDTO = new UserLoginFormDTO();
        userLoginFormDTO.setUsername("TestUser");
        userLoginFormDTO.setPassword("1234");

        User user = new User();
        user.setUserRoles(Collections.singletonList(UserRole.ROLE_USER));
        user.setPassword(userLoginFormDTO.getPassword());
        user.setUsername(userLoginFormDTO.getUsername());
        user.setId(1L);
        user.setEmail("TESTUSER@test.com");
        user.setNickname("TestUSER");

        when(userRepository.findByUsername(userLoginFormDTO.getUsername())).thenReturn(user);
        when(jwtProvider.createToken(userLoginFormDTO.getUsername(), user.getUserRoles())).thenReturn("token");


        String token = userService.signIn(userLoginFormDTO);

        assertNotNull(token);
    }

    @Test
    public void signIn_InvalidPassword() {
        UserLoginFormDTO userLoginFormDTO = new UserLoginFormDTO();
        userLoginFormDTO.setUsername("TestUser");
        userLoginFormDTO.setPassword("1234");

        User user = new User();
        user.setUserRoles(Collections.singletonList(UserRole.ROLE_USER));
        user.setUsername(userLoginFormDTO.getUsername());
        user.setPassword("4321");
        user.setId(1L);
        user.setEmail("TESTUSER@test.com");
        user.setNickname("TestUSER");

        when(userRepository.findByUsername(userLoginFormDTO.getUsername())).thenReturn(user);


        doThrow(CustomException.class).when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(CustomException.class, () -> userService.signIn(userLoginFormDTO));


    }

    @Test
    public void signIn_InvalidUsername() {
        UserLoginFormDTO userLoginFormDTO = new UserLoginFormDTO();
        userLoginFormDTO.setUsername("TestUser");
        userLoginFormDTO.setPassword("1234");

        User user = new User();
        user.setUserRoles(Collections.singletonList(UserRole.ROLE_USER));
        user.setUsername("Invalid");
        user.setPassword("4321");
        user.setId(1L);
        user.setEmail("TESTUSER@test.com");
        user.setNickname("TestUSER");

        when(userRepository.findByUsername(userLoginFormDTO.getUsername())).thenReturn(user);


        doThrow(CustomException.class).when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(CustomException.class, () -> userService.signIn(userLoginFormDTO));
    }


}
