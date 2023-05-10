package com.board.board.Controller;

import com.board.board.DTO.UserDTO;
import com.board.board.DTO.UserSignUpFormDTO;
import com.board.board.DTO.UserUpdateFormDTO;
import com.board.board.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/sign-in")
    public ResponseEntity<Map<String, String>> login(@RequestParam String username, @RequestParam String password){
        Map<String, String> response = new HashMap<>();
        String token = userService.signIn(username, password);
        response.put("token", token);
        response.put("username", userService.getCurrent(token).getUsername());
        response.put("nickname", userService.getCurrent(token).getNickname());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<Map<String, String>> signUp(@RequestBody UserSignUpFormDTO userSignUpFormDTO){
        Map<String, String> response = new HashMap<>();
        response.put("token",userService.signUp(userSignUpFormDTO));
        response.put("username", userSignUpFormDTO.getUsername());
        response.put("nickname", userSignUpFormDTO.getNickname());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@RequestHeader("Authorization")String token){
        return ResponseEntity.ok(userService.getCurrent(token));
    }

    @GetMapping("/get/{user_id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long user_id, String token){
        return ResponseEntity.ok(userService.getUser(user_id, token));
    }

    @GetMapping("/admin/getAllUser")
    public ResponseEntity<List<UserDTO>> getAllUser(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(userService.getAllUser(token));
    }

    @GetMapping("/getAllUserPage")
    public ResponseEntity<Page<UserDTO>> getAllUserPage(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size,
                                                        @RequestParam(defaultValue = "") String search,
                                                        @RequestHeader("Authorization") String token){
        return ResponseEntity.ok(userService.getAllUserPage(page, size, search, token));
    }

    @PutMapping("/update")
    public ResponseEntity<UserDTO> updateUser(@RequestHeader("Authorization")String token, UserUpdateFormDTO userUpdateFormDTO){
        return ResponseEntity.ok(userService.updateUser(userUpdateFormDTO, token));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUser(@PathVariable Long user_id){
        boolean isDelete = userService.deleteUser(user_id);
        if(isDelete){
            return ResponseEntity.noContent().build();
        }else {
            return ResponseEntity.notFound().build();
        }
    }

}
