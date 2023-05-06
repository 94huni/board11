package com.board.board.Controller;

import com.board.board.DTO.BoardCreateFormDTO;
import com.board.board.DTO.BoardDTO;
import com.board.board.Entity.Board;
import com.board.board.Service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;

    //글등록
    @PostMapping("/create")
    public ResponseEntity<String> createBoard(BoardCreateFormDTO boardCreateFormDTO, String token){
        Board board = boardService.createBoard(boardCreateFormDTO, token);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(board.getId())
                .toUri();

        return ResponseEntity.created(location).body("게시글 등록이 성공했습니다!");
    }

    //글조회
    @GetMapping("/get/{board_id}")
    public ResponseEntity<BoardDTO> getBoard(@PathVariable Long board_id){
        return ResponseEntity.ok(boardService.getBoard(board_id));
    }
}
