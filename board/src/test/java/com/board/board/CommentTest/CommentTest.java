package com.board.board.CommentTest;

import com.board.board.Config.Jwt.JwtProvider;
import com.board.board.DTO.CommentDTO;
import com.board.board.Entity.Board;
import com.board.board.Entity.Category;
import com.board.board.Entity.Comment;
import com.board.board.Entity.User;
import com.board.board.Repository.BoardRepository;
import com.board.board.Repository.CommentRepository;
import com.board.board.Repository.UserRepository;
import com.board.board.Service.CommentService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CommentTest {
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private JwtProvider jwtProvider;
    @InjectMocks
    private CommentService commentService;

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("User");
        user.setNickname("User");
        return user;
    }

    private Board createBoard(User user) {
        Board board = new Board();
        board.setId(1L);
        board.setUser(user);
        board.setTitle("TestBoard");
        board.setContent("TestContent");
        board.setCategory(List.of(Category.FREE));
        return board;
    }

    private List<Comment> createComments(Board board, User user) {
        List<Comment> comments = new ArrayList<>();
        for (int i=0; i<5; i++){
            Comment comment = new Comment();
            comment.setBoard(board);
            comment.setUser(user);
            comment.setId((long) i);
            comment.setContent("TestComment"+i);
            comments.add(comment);
        }
        return comments;
    }

    @Test
    public void createComment_Successful() {
        String token = "token";

        String content = "Comment_Test";

        User user = createUser();

        Board board = createBoard(user);


        when(jwtProvider.validateToken(token)).thenReturn(true);
        when(userRepository.findByUsername(jwtProvider.getUsername(token))).thenReturn(user);
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));

        CommentDTO commentDTO = commentService.createComment(1L, content, token);

        assertEquals("Comment_Test", commentDTO.getContent());
    }

    @Test
    public void getComment_Successful() {
        String token = "token";
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        User user = createUser();

        Board board = createBoard(user);

        List<Comment> comments = createComments(board, user);

        board.setCommentList(comments);

        Page<Comment> commentPage = new PageImpl<>(comments, pageable, 5);

        when(jwtProvider.validateToken(token)).thenReturn(true);
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
        when(commentRepository.findByBoard(pageable, board)).thenReturn(commentPage);

        Page<CommentDTO> commentDTOS = commentService.getCommentByBoardPage(1L, page, size);
        List<CommentDTO> commentDTOList = commentDTOS.getContent();
        CommentDTO commentDTO = commentDTOList.get(0);


        assertEquals(commentDTO.getBoard_id(), 1L);
        assertEquals(commentDTO.getContent(), "TestComment0");
        assertEquals(commentDTO.getBoard_title(), "TestBoard");
        assertEquals(commentDTO.getUsername(), "User");
    }
}
