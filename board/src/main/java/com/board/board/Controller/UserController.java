package com.board.board.Controller;

import com.board.board.DTO.UserDTO;
import com.board.board.DTO.UserLoginFormDTO;
import com.board.board.DTO.UserSignUpFormDTO;
import com.board.board.DTO.UserUpdateFormDTO;
import com.board.board.Service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @ApiOperation("로그인")
    @PostMapping("/sign-in")
    public ResponseEntity<Map<String, String>> login(@RequestBody UserLoginFormDTO userLoginFormDTO){
        Map<String, String> response = new HashMap<>();
        String token = userService.signIn(userLoginFormDTO);
        response.put("token", token);
        response.put("username", userService.getCurrent(token).getUsername());
        response.put("nickname", userService.getCurrent(token).getNickname());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @ApiOperation("회원가입")
    @PostMapping("/sign-up")
    public ResponseEntity<Map<String, String>> signUp(@RequestBody UserSignUpFormDTO userSignUpFormDTO){
        Map<String, String> response = new HashMap<>();
        response.put("token",userService.signUp(userSignUpFormDTO));
        response.put("username", userSignUpFormDTO.getUsername());
        response.put("nickname", userSignUpFormDTO.getNickname());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @ApiOperation("관리자 계정 생성")
    @PostMapping("sign-up/ad")
    public ResponseEntity<Map<String, String>> signUpAd(@RequestBody UserSignUpFormDTO userSignUpFormDTO){
        Map<String, String> response = new HashMap<>();
        response.put("token",userService.signUpAdmin(userSignUpFormDTO));
        response.put("username", userSignUpFormDTO.getUsername());
        response.put("nickname", userSignUpFormDTO.getNickname());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @ApiOperation("현재 접속된 정보확인")
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@RequestHeader("Authorization")String token){
        return ResponseEntity.ok(userService.getCurrent(token));
    }

    @ApiOperation("유저정보 조회")
    @GetMapping("/get/{user_id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long user_id,@RequestHeader("Authorization") String token){
        UserDTO userDTO = userService.getUser(user_id, token);
        return ResponseEntity.ok(userDTO);
    }

    //페이징 처리 전 코드
//    @GetMapping("/admin/getAllUser")
//    public ResponseEntity<List<UserDTO>> getAllUser(@RequestHeader("Authorization") String token) {
//        return ResponseEntity.ok(userService.getAllUser(token));
//    }

    @ApiOperation(value = "모든유저정보목록", notes = "모든사용자의 정보를 찾아오거나, username 을 검색하여 결과목록을 가져옵니다.")
    @GetMapping("/users")
    public ResponseEntity<Page<UserDTO>> getAllUserPage(@ApiParam(value = "페이지 번호 (기본값: 0)", defaultValue = "0") @RequestParam(defaultValue = "0") int page,
                                                        @ApiParam(value = "페이지 크기 (기본값: 10)", defaultValue = "10") @RequestParam(defaultValue = "10") int size,
                                                        @ApiParam(value = "검색어 username (기본값: 빈 문자열)") @RequestParam(defaultValue = "") String search,
                                                        @ApiParam(value = "인증 토큰", required = true) @RequestHeader("Authorization") String token){
        return ResponseEntity.ok(userService.getAllUserPage(page, size, search, token));
    }

    @ApiOperation(value = "유저정보변경", notes = "사용자의 정보를 확인하고 유저정보를 변경할 수 있다.")
    @PutMapping("/update")
    public ResponseEntity<UserDTO> updateUser(@RequestHeader("Authorization")String token,@RequestBody UserUpdateFormDTO userUpdateFormDTO){
        return ResponseEntity.ok(userService.updateUser(userUpdateFormDTO, token));
    }


    @ApiOperation("유저정보 삭제")
    @ApiResponses({@ApiResponse(code = 204, message = "성공적으로 삭제되었습니다!")})
    @DeleteMapping("/delete/{user_id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long user_id, @RequestHeader("Authorization")String token){
        boolean isDelete = userService.deleteUser(user_id, token);
        if(isDelete){
            return ResponseEntity.noContent().build();
        }else {
            return ResponseEntity.notFound().build();
        }
    }

}
