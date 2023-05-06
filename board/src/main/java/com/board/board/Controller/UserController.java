package com.board.board.Controller;

import com.board.board.DTO.UserDTO;
import com.board.board.DTO.UserSearchFormDTO;
import com.board.board.DTO.UserSignUpFormDTO;
import com.board.board.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/get/{username}")
    public ResponseEntity<UserSearchFormDTO> getUser(@PathVariable String username){
        return ResponseEntity.ok(userService.getUser(username));
    }

    @GetMapping("/admin/getAllUser")
    public ResponseEntity<List<UserDTO>> getAllUser() {
        return ResponseEntity.ok(userService.getAllUser());
    }

    @GetMapping("/getAllUserPage")
    public ResponseEntity<Page<UserDTO>> getAllUserPage(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size,
                                                        @RequestParam(defaultValue = "") String search){
        return ResponseEntity.ok(userService.getAllUserPage(page, size, search));
    }

    @DeleteMapping("/delete/{user_id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long user_id){
        boolean isDelete = userService.deleteUser(user_id);
        if(isDelete){
            return ResponseEntity.noContent().build();
        }else {
            return ResponseEntity.notFound().build();
        }
    }

}
