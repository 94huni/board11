package com.board.board.Controller;

import com.board.board.DTO.BoardCreateFormDTO;
import com.board.board.DTO.BoardDTO;
import com.board.board.DTO.BoardUpdateFormDTO;
import com.board.board.Entity.Category;
import com.board.board.Service.BoardService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;



    @ApiOperation(value = "글등록")
    @PostMapping("/create")
    public ResponseEntity<BoardDTO> createBoard(BoardCreateFormDTO boardCreateFormDTO, @RequestHeader("Authorization") String token){
        BoardDTO board = boardService.createBoard(boardCreateFormDTO, token);

/*
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(board.getId())
                .toUri();
*/


        return ResponseEntity.status(HttpStatus.CREATED).body(board);

    }


    @ApiOperation(value = "글 조회", notes = "게시판 고유번호를 받아서 그 번호의 게시글을 가져옵니다.")
    @GetMapping("/get/{board_id}")
    public ResponseEntity<BoardDTO> getBoard(@PathVariable Long board_id){
        BoardDTO boardDTO = boardService.getBoard(board_id);
        return ResponseEntity.ok(boardDTO);
    }

    //유저의 전체 글 조회
    @ApiOperation(value = "회원의 글 전체 조회", notes = "회원 아이디를 입력받아 jwt 검증이후 회원의 전체 글 목록을 가져옵니다.")
    @GetMapping("/get/{username}")
    public ResponseEntity<Page<BoardDTO>> getBoardByUser(@PathVariable String username,
                                                         @RequestHeader("Authorization") String token,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size){
        Page<BoardDTO> boardDTOS = boardService.getUserBoardPage(token, username, page, size);
        return ResponseEntity.ok(boardDTOS);
    }

    //카테고리 분류
    @ApiOperation(value = "카테고리별 분류", notes = "카테고리를 선택하여 선택 카테고리에 맞게 분류하여 글 목록을 가져옵니다.")
    @GetMapping("/{category}")
    public ResponseEntity<Page<BoardDTO>> getBoardByCategory(@RequestParam Category category,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size){
        Page<BoardDTO> boardDTOS = boardService.getCategoryBoardPage(category, page, size);
        return ResponseEntity.ok(boardDTOS);
    }


    //글수정
    @ApiOperation(value = "글 수정", notes = "자신의 글을 수정할 수 있습니다.")
    @PutMapping("/update/{board_id}")
    public ResponseEntity<BoardDTO> updateBoard(@ApiParam(value = "글 고유 번호", required = true)@PathVariable Long board_id,
                                                @ApiParam(value = "업데이트할 정보", required = true) @RequestBody BoardUpdateFormDTO boardUpdateFormDTO,
                                                @ApiParam(value = "인증 토큰", required = true) @RequestHeader("Authorization") String token){
        BoardDTO boardDTO = boardService.updateBoard(board_id, boardUpdateFormDTO, token);
        return ResponseEntity.ok(boardDTO);
    }

    //글삭제
    @ApiOperation("글 삭제")
    @DeleteMapping("/delete/{board_id}")
    public ResponseEntity<Void> deleteBoard(@ApiParam(value = "글 고유번호", required = true) @PathVariable Long id,@RequestHeader("Authorization") @ApiParam(value = "인증 토큰", required = true) String token){
        boolean isDelete = boardService.deleteBoard(id, token);
        if(isDelete){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
