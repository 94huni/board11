package com.board.board.Controller;

import com.board.board.DTO.BoardCreateFormDTO;
import com.board.board.DTO.BoardDTO;
import com.board.board.DTO.BoardUpdateFormDTO;
import com.board.board.Entity.Board;
import com.board.board.Entity.Category;
import com.board.board.Service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;

    //글등록
    @PostMapping("/create")
    public ResponseEntity<String> createBoard(BoardCreateFormDTO boardCreateFormDTO, @RequestHeader("Authorization") String token){
        Board board = boardService.createBoard(boardCreateFormDTO, token);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(board.getId())
                .toUri();

        return ResponseEntity.created(location).body("게시글 등록이 성공했습니다!");
    }

    //글조회
    @GetMapping("/board/{board_id}")
    public ResponseEntity<BoardDTO> getBoard(@PathVariable Long board_id){
        BoardDTO boardDTO = boardService.getBoard(board_id);
        return ResponseEntity.ok(boardDTO);
    }

    //유저의 전체 글 조회
    @GetMapping("/user/{username}")
    public ResponseEntity<Page<BoardDTO>> getBoardByUser(@PathVariable String username,
                                                         @RequestHeader("Authorization") String token,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size){
        Page<BoardDTO> boardDTOS = boardService.getUserBoardPage(token, username, page, size);
        return ResponseEntity.ok(boardDTOS);
    }

    //카테고리 분류
    @GetMapping("/category")
    public ResponseEntity<Page<BoardDTO>> getBoardByCategory(@RequestParam Category category,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size,
                                                             @RequestParam(defaultValue = "") String search){
        Page<BoardDTO> boardDTOS = boardService.getCategoryBoardPage(category, page, size, search);
        return ResponseEntity.ok(boardDTOS);
    }


    //글수정
    @PutMapping("/update/{board_id}")
    public ResponseEntity<BoardDTO> updateBoard(@PathVariable Long board_id, BoardUpdateFormDTO boardUpdateFormDTO,@RequestHeader("Authorization") String token){
        BoardDTO boardDTO = boardService.updateBoard(board_id, boardUpdateFormDTO, token);
        return ResponseEntity.ok(boardDTO);
    }

    //글삭제
    @DeleteMapping("/delete/{board_id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id,@RequestHeader("Authorization") String token){
        boolean isDelete = boardService.deleteBoard(id, token);
        if(isDelete){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
