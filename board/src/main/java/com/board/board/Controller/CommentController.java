package com.board.board.Controller;

import com.board.board.DTO.CommentDTO;
import com.board.board.Service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/create")
    public ResponseEntity<CommentDTO> createComment(String token, String content, Long board_id){
        CommentDTO commentDTO = commentService.createComment(board_id,content, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentDTO);
    }

    @GetMapping("/board/{board_id}")
    public ResponseEntity<List<CommentDTO>> getCommentByBoard(@RequestParam Long board_id){
        return ResponseEntity.ok(commentService.getCommentByBoard(board_id));
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<CommentDTO>> getCommentByUser(@PathVariable String username, @RequestHeader("Authorization") String token){
        return ResponseEntity.ok(commentService.getCommentByUser(username, token));
    }

    @PutMapping("/update/{comment_id}")
    public ResponseEntity<CommentDTO> updateComment(@RequestHeader("Authorization") String token, @PathVariable Long comment_id){
        return ResponseEntity.ok(commentService.updateComment(comment_id, token));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteComment(@RequestHeader("Authorization") String token, @PathVariable Long comment_id){
        boolean isDelete = commentService.deleteComment(comment_id, token) ;
        if(isDelete){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
