package com.board.board.Service;

import com.board.board.Config.Jwt.JwtProvider;
import com.board.board.DTO.BoardCreateFormDTO;
import com.board.board.DTO.BoardDTO;
import com.board.board.Entity.Board;
import com.board.board.Entity.User;
import com.board.board.Exception.CustomException;
import com.board.board.Repository.BoardRepository;
import com.board.board.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;


    public Board createBoard(BoardCreateFormDTO boardCreateFormDTO, String token){
        if(!jwtProvider.validateToken(token)){
            throw new CustomException("만료되었거나 토큰이 잘못됐습니다!", HttpStatus.UNAUTHORIZED);
        }
        String username = jwtProvider.getUsername(token);
        User user = userRepository.findByUsername(username);
        Board board = new Board();
        board.setTitle(boardCreateFormDTO.getTitle());
        board.setContent(boardCreateFormDTO.getContent());
        board.setImage(boardCreateFormDTO.getMultipartFile());
        board.setCreateAt(LocalDateTime.now());
        board.setUser(user);
        board.setCategory(boardCreateFormDTO.getCategory());
        boardRepository.save(board);
        return board;
    }

    public BoardDTO getBoard(Long id){
        Board board = boardRepository.findById(id).orElseThrow(()->new CustomException("글정보를 찾을 없습니다!", HttpStatus.NOT_FOUND));
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setNickname(board.getUser().getNickname());
        boardDTO.setTitle(board.getTitle());
        boardDTO.setContent(board.getContent());
        boardDTO.setCreateAt(board.getCreateAt());
        boardDTO.setCategory(board.getCategory().toString());
        boardDTO.setComments(board.getCommentList());
        return boardDTO;
    }
}
