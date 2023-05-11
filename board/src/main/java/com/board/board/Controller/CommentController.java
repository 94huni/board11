package com.board.board.Controller;

import com.board.board.DTO.CommentDTO;
import com.board.board.Service.CommentService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

    @ApiOperation("댓글 생성")
    @PostMapping("/create/{board_id}")
    public ResponseEntity<CommentDTO> createComment(@RequestHeader("Authorization") String token,@RequestBody String content,@PathVariable Long board_id){
        CommentDTO commentDTO = commentService.createComment(board_id,content, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentDTO);
    }


    @GetMapping("/board/{board_id}")
    public ResponseEntity<Page<CommentDTO>> getCommentByBoard(@PathVariable Long board_id,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size){
        Page<CommentDTO> commentDTOS = commentService.getCommentByBoardPage(board_id, page, size);
        return ResponseEntity.ok(commentDTOS);
    }



    @ApiOperation("유저의 댓글 목록")
    @GetMapping("/user/{user_id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<CommentDTO>> getCommentByUserPage(@PathVariable Long user_id,
                                                                 @RequestHeader("Authorization") String token,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size){
        Page<CommentDTO> commentDTO = commentService.getCommentByUserPage(user_id,token, page, size);
        return ResponseEntity.ok(commentDTO);
    }

    
    @ApiOperation("댓글 수정")
    @PutMapping("/update/{comment_id}")
    public ResponseEntity<CommentDTO> updateComment(@RequestHeader("Authorization") String token, @PathVariable Long comment_id){
        return ResponseEntity.ok(commentService.updateComment(comment_id, token));
    }

    @ApiOperation("댓글 삭제")
    @DeleteMapping("/delete/{comment_id}")
    public ResponseEntity<Void> deleteComment(@RequestHeader("Authorization") String token, @PathVariable Long comment_id){
        boolean isDelete = commentService.deleteComment(comment_id, token) ;
        if(isDelete){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
