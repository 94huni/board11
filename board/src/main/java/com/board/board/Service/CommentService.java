package com.board.board.Service;

import com.board.board.Config.Jwt.JwtProvider;
import com.board.board.DTO.CommentDTO;
import com.board.board.Entity.Board;
import com.board.board.Entity.Comment;
import com.board.board.Entity.User;
import com.board.board.Exception.CustomException;
import com.board.board.Repository.BoardRepository;
import com.board.board.Repository.CommentRepository;
import com.board.board.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final JwtProvider jwtProvider;

    //댓글생성
    public CommentDTO createComment(Long board_id, String content, String token){
        if(!jwtProvider.validateToken(token)){
            throw new CustomException("만료되었거나 토큰이 잘못됐습니다!", HttpStatus.UNAUTHORIZED);
        }
        String username = jwtProvider.getUsername(token);
        User user = userRepository.findByUsername(username);
        Board board = boardRepository.findById(board_id).orElseThrow(()->new CustomException("게시글 정보가 없거나 잘못됐습니다!", HttpStatus.NOT_FOUND));
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setBoard(board);
        comment.setUser(user);
        comment.setCreateAt(LocalDateTime.now());
        commentRepository.save(comment);

        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setContent(comment.getContent());
        commentDTO.setUsername(comment.getUser().getUsername());
        commentDTO.setBoard_id(board.getId());
        commentDTO.setBoard_title(board.getTitle());
        return  commentDTO;
    }

    //게시글 정보로 댓글정보조회
    public List<CommentDTO> getCommentByBoard(Long board_id){
        Board board = boardRepository.findById(board_id)
                .orElseThrow(()->new CustomException("게시글을 찾을 수 없습니다!", HttpStatus.NOT_FOUND));

        List<CommentDTO> commentDTOs = new ArrayList<>();
        for (int i=0; i<board.getCommentList().size(); i++){
            CommentDTO commentDTO = new CommentDTO();
            commentDTO.setContent(board.getCommentList().get(i).getContent());
            commentDTO.setUsername(board.getUser().getUsername());
            commentDTO.setBoard_id(board.getId());
            commentDTO.setBoard_title(board.getTitle());
            commentDTOs.add(commentDTO);
        }
        return commentDTOs;
    }

    //유저아이디로 댓글정보조회
    public List<CommentDTO> getCommentByUser(String username, String token){
        if(!jwtProvider.validateToken(token)){
            throw new CustomException("만료되었거나 토큰이 잘못됐습니다!", HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findByUsername(username);

        if(!userRepository.existsByUsername(username)){
            throw new CustomException("사용자를 찾을 수 없습니다!", HttpStatus.NOT_FOUND);
        }

        List<CommentDTO> commentDTOS = new ArrayList<>();
        for (int i=0; i<user.getCommentList().size(); i++){
            CommentDTO commentDTO = new CommentDTO();
            commentDTO.setUsername(user.getUsername());
            commentDTO.setBoard_id(user.getCommentList().get(i).getBoard().getId());
            commentDTO.setBoard_title(user.getCommentList().get(i).getBoard().getTitle());
            commentDTO.setContent(user.getCommentList().get(i).getContent());
            commentDTOS.add(commentDTO);
        }
        return commentDTOS;

    }

    
    //댓글 수정
    public CommentDTO updateComment(Long comment_id, String token){
        if(!jwtProvider.validateToken(token)){
            throw new CustomException("만료되었거나 토큰이 잘못됐습니다!", HttpStatus.UNAUTHORIZED);
        }

        String username = jwtProvider.getUsername(token);

        Comment comment = commentRepository.findById(comment_id).orElseThrow(()->new CustomException("댓글을 찾을 수 없습니다!", HttpStatus.NOT_FOUND));

        if(!username.equals(comment.getUser().getUsername())){
            throw new CustomException("권한이 없습니다!", HttpStatus.UNAUTHORIZED);
        }

        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setBoard_id(comment.getBoard().getId());
        commentDTO.setUsername(comment.getUser().getUsername());
        commentDTO.setBoard_title(comment.getBoard().getTitle());
        commentDTO.setContent(comment.getContent());


        return commentDTO;
    }

    public boolean deleteComment(Long comment_id, String token){
        if(!jwtProvider.validateToken(token)){
            throw new CustomException("만료되었거나 토큰이 잘못됐습니다!", HttpStatus.UNAUTHORIZED);
        }

        String username = jwtProvider.getUsername(token);

        Comment comment = commentRepository.findById(comment_id).orElseThrow(()->new CustomException("댓글을 찾을 수 없습니다!", HttpStatus.NOT_FOUND));

        if(!username.equals(comment.getUser().getUsername())){
            throw new CustomException("권한이 없습니다!", HttpStatus.UNAUTHORIZED);
        }

        commentRepository.delete(comment);

        return true;
    }

}
