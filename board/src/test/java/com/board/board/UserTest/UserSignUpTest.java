package com.board.board.UserTest;

import com.board.board.Config.Jwt.JwtProvider;
import com.board.board.DTO.UserSignUpFormDTO;
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

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
public class UserSignUpTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private JwtProvider jwtProvider;
    @InjectMocks
    private UserService userService;

    @Test
    public void singup_Successful(){
        // 데이터
        UserSignUpFormDTO userSignUpFormDTO = new UserSignUpFormDTO();
        userSignUpFormDTO.setUsername("testUser");
        userSignUpFormDTO.setNickname("TestUser");
        userSignUpFormDTO.setPassword("1234");
        userSignUpFormDTO.setPassword2("1234");
        userSignUpFormDTO.setEmail("test@test.com");

        User user = new User();
        user.setUsername("testUser");
        user.setPassword("Password");
        user.setEmail("test@test.com");
        user.setNickname("TestUser");

        // 동작설정
        when(userRepository.existsByUsername("testUser")).thenReturn(false);
        when(bCryptPasswordEncoder.encode("1234")).thenReturn("Password");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtProvider.createToken("testUser", Collections.singletonList(UserRole.ROLE_USER))).thenReturn("testToken");

        String token = userService.signUp(userSignUpFormDTO);

        // 결과
        assertNotNull(token);
        assertEquals("testToken",token);
        verify(userRepository, times(1)).existsByUsername("testUser");
        verify(bCryptPasswordEncoder, times(1)).encode("1234");
        verify(jwtProvider, times(1)).createToken("testUser", Collections.singletonList(UserRole.ROLE_USER));


    }

    @Test
    public void signUp_passwordMismatch(){
        UserSignUpFormDTO userSignUpFormDTO = new UserSignUpFormDTO();
        userSignUpFormDTO.setUsername("testUser");
        userSignUpFormDTO.setPassword("1234");
        userSignUpFormDTO.setPassword2("4321");

        CustomException exception = assertThrows(CustomException.class, () ->{
            userService.signUp(userSignUpFormDTO);
        });
        assertEquals("비밀번호가 다릅니다.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        verify(bCryptPasswordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class ));
        verify(jwtProvider, never()).createToken(anyString(), anyList());

    }

    @Test
    public void signUp_UsernameExists(){
        UserSignUpFormDTO userSignUpFormDTO = new UserSignUpFormDTO();
        userSignUpFormDTO.setUsername("TestUser");
        userSignUpFormDTO.setPassword("1234");
        userSignUpFormDTO.setPassword2("1234");

        when(userRepository.existsByUsername("TestUser")).thenReturn(true);

        CustomException exception = assertThrows(CustomException.class, () ->{
            userService.signUp(userSignUpFormDTO);
        });

        assertEquals("중복된 아이디가 있습니다!", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT ,exception.getHttpStatus());
    }
}
