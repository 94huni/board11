package com.board.board.Service;

import com.board.board.Config.Jwt.JwtProvider;
import com.board.board.DTO.UserDTO;
import com.board.board.DTO.UserSignUpFormDTO;
import com.board.board.Entity.User;
import com.board.board.Entity.UserRole;
import com.board.board.Exception.CustomException;
import com.board.board.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    //로그인시 jwt 반환
    public String signIn(String username, String password){
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            return jwtProvider.createToken(username, userRepository.findByUsername(username).getUserRoles());
        }catch (AuthenticationException e){
            throw new CustomException("사용자이름 또는 비밀번호가 틀렸습니다!", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    //회원가입시 jwt 반환
    public String signUp(UserSignUpFormDTO userSignUpFormDTO){
        if(userRepository.existsByUsername(userSignUpFormDTO.getUsername())){
            throw new CustomException("중복된 아이디가 있습니다!", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if(!userSignUpFormDTO.getPassword().equals(userSignUpFormDTO.getPassword2())){
            throw new CustomException("비밀번호가 다릅니다.", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        User user = new User();
        user.setUsername(userSignUpFormDTO.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(userSignUpFormDTO.getPassword()));
        user.setEmail(userSignUpFormDTO.getEmail());
        user.setNickname(userSignUpFormDTO.getNickname());
        user.setCreateAt(LocalDateTime.now());
        user.setUserRoles(Collections.singletonList(UserRole.ROLE_USER));
        userRepository.save(user);
        return jwtProvider.createToken(user.getUsername(), user.getUserRoles());
    }


    //회원정보 조회
    public UserDTO getUser(Long id){
        User user = userRepository.findById(id).orElseThrow(()->new RuntimeException("회원정보가없습니다!"));
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(user.getUsername());
        userDTO.setNickname(user.getNickname());
        userDTO.setUserRoles(user.getUserRoles());
        userDTO.setEmail(user.getEmail());
        userDTO.setCreateAt(user.getCreateAt());
        userDTO.setBoardList(user.getBoardList());
        userDTO.setCommentList(user.getCommentList());

        return userDTO;
    }

    //전체 회원 조회
    public List<UserDTO> getAllUser(){
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOS = new ArrayList<>();

        for(int i=0; i<users.size(); i++){
            User user = users.get(i);
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(user.getUsername());
            userDTO.setNickname(user.getNickname());
            userDTO.setEmail(user.getEmail());
            userDTO.setUserRoles(user.getUserRoles());
            userDTO.setCreateAt(user.getCreateAt());
            userDTOS.add(userDTO);
        }
        return userDTOS;
    }

}
