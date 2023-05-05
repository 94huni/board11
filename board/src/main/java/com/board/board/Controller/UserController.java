package com.board.board.Controller;

import com.board.board.DTO.UserSignUpFormDTO;
import com.board.board.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/signin")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.signIn(username, password));
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody UserSignUpFormDTO userSignUpFormDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.signUp(userSignUpFormDTO));
    }

}
