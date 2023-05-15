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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final JwtProvider jwtProvider;

    //댓글생성
    public CommentDTO createComment(Long board_id, String content, String token) {
        if (!jwtProvider.validateToken(token)) {
            throw new CustomException("만료되었거나 토큰이 잘못됐습니다!", HttpStatus.UNAUTHORIZED);
        }
        String username = jwtProvider.getUsername(token);
        User user = userRepository.findByUsername(username);
        Board board = boardRepository.findById(board_id).orElseThrow(() -> new CustomException("게시글 정보가 없거나 잘못됐습니다!", HttpStatus.NOT_FOUND));
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
        return commentDTO;
    }

    //게시글 정보로 댓글정보조회
    public Page<CommentDTO> getCommentByBoardPage(Long board_id, int page, int size) {
        Board board = boardRepository.findById(board_id)
                .orElseThrow(() -> new CustomException("게시글을 찾을 수 없습니다!", HttpStatus.NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> comments = commentRepository.findByBoard(pageable, board);

        Page<CommentDTO> commentDTOS = comments.map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            commentDTO.setBoard_id(comment.getBoard().getId());
            commentDTO.setBoard_title(comment.getBoard().getTitle());
            commentDTO.setUsername(comment.getUser().getUsername());
            commentDTO.setContent(comment.getContent());
            return commentDTO;
        });

        return commentDTOS;
    }


    //유저아이디로 댓글정보조회
    public Page<CommentDTO> getCommentByUserPage(Long user_id, String token, int page, int size) {
        if (!jwtProvider.validateToken(token)) {
            throw new CustomException("만료되었거나 토큰이 잘못됐습니다!", HttpStatus.UNAUTHORIZED);
        }

        Pageable pageable = PageRequest.of(page, size);
        User user = userRepository.findById(user_id).orElseThrow(() -> new CustomException("유저정보를 찾을 수 없습니다!", HttpStatus.NOT_FOUND));
        Page<Comment> comments = commentRepository.findByUser(pageable, user);

        Page<CommentDTO> commentDTOS = comments.map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            commentDTO.setBoard_id(comment.getBoard().getId());
            commentDTO.setUsername(comment.getUser().getUsername());
            commentDTO.setBoard_title(comment.getBoard().getTitle());
            commentDTO.setContent(comment.getContent());
            return commentDTO;
        });

        return commentDTOS;
    }


    //댓글 수정
    public CommentDTO updateComment(Long comment_id, String token) {
        if (!jwtProvider.validateToken(token)) {
            throw new CustomException("만료되었거나 토큰이 잘못됐습니다!", HttpStatus.UNAUTHORIZED);
        }

        String username = jwtProvider.getUsername(token);

        Comment comment = commentRepository.findById(comment_id).orElseThrow(() -> new CustomException("댓글을 찾을 수 없습니다!", HttpStatus.NOT_FOUND));

        if (!username.equals(comment.getUser().getUsername())) {
            throw new CustomException("권한이 없습니다!", HttpStatus.UNAUTHORIZED);
        }

        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setBoard_id(comment.getBoard().getId());
        commentDTO.setUsername(comment.getUser().getUsername());
        commentDTO.setBoard_title(comment.getBoard().getTitle());
        commentDTO.setContent(comment.getContent());


        return commentDTO;
    }

    public boolean deleteComment(Long comment_id, String token) {
        if (!jwtProvider.validateToken(token)) {
            throw new CustomException("만료되었거나 토큰이 잘못됐습니다!", HttpStatus.UNAUTHORIZED);
        }

        String username = jwtProvider.getUsername(token);

        Comment comment = commentRepository.findById(comment_id).orElseThrow(() -> new CustomException("댓글을 찾을 수 없습니다!", HttpStatus.NOT_FOUND));

        if (!username.equals(comment.getUser().getUsername())) {
            throw new CustomException("권한이 없습니다!", HttpStatus.UNAUTHORIZED);
        }

        commentRepository.delete(comment);

        return true;
    }

}
