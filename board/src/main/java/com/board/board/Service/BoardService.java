package com.board.board.Service;

import com.board.board.Config.Jwt.JwtProvider;
import com.board.board.DTO.BoardCreateFormDTO;
import com.board.board.DTO.BoardDTO;
import com.board.board.DTO.BoardUpdateFormDTO;
import com.board.board.Entity.Board;
import com.board.board.Entity.Category;
import com.board.board.Entity.Comment;
import com.board.board.Entity.User;
import com.board.board.Exception.CustomException;
import com.board.board.Repository.BoardRepository;
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
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;


    //글등록
    public BoardDTO createBoard(BoardCreateFormDTO boardCreateFormDTO, String token) {
        if (!jwtProvider.validateToken(token)) {
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

        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setTitle(board.getTitle());
        boardDTO.setNickname(board.getUser().getNickname());
        boardDTO.setCategory(board.getCategory().stream().map(Enum::toString).collect(Collectors.toList()));
        boardDTO.setCreateAt(board.getCreateAt());
        boardDTO.setContent(board.getContent());
        if (board.getCommentList() == null) {
            boardDTO.setComment_count(0);
        } else {
            boardDTO.setComment_count(board.getCommentList().size());
        }

        return boardDTO;
    }

    //글정보
    public BoardDTO getBoard(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new CustomException("글정보를 찾을 없습니다!", HttpStatus.NOT_FOUND));
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setNickname(board.getUser().getNickname());
        boardDTO.setTitle(board.getTitle());
        boardDTO.setContent(board.getContent());
        boardDTO.setCreateAt(board.getCreateAt());
        boardDTO.setCategory(board.getCategory().stream().map(Enum::toString).collect(Collectors.toList()));
        List<Comment> comments = board.getCommentList();
        if (comments == null) {
            boardDTO.setComment_count(0);
        } else {
            boardDTO.setComment_count(board.getCommentList().size());
        }
        return boardDTO;
    }

   /* public List<BoardDTO> getUserBoard(String token, String username){
        if(!jwtProvider.validateToken(token)){
            throw new CustomException("만료되었거나 토큰이 잘못됐습니다!", HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findByUsername(username);
        List<Board> boards = user.getBoardList();
        List<BoardDTO> boardDTOS = new ArrayList<>();

        for(int i=0; i<boards.size(); i++){
            BoardDTO boardDTO = new BoardDTO();
            boardDTO.setNickname(boards.get(i).getUser().getNickname());
            boardDTO.setTitle(boards.get(i).getTitle());
            boardDTO.setContent(boards.get(i).getContent());
            boardDTO.setCategory(boards.get(i).getCategory().toString());
            boardDTO.setComment_count(boards.get(i).getCommentList().size());
            boardDTO.setCreateAt(boards.get(i).getCreateAt());
            boardDTOS.add(boardDTO);
        }

        return boardDTOS;

    }*/

    //유저아이디 조회 서비스
    public Page<BoardDTO> getUserBoardPage(String token, String username, int page, int size) {
        if (!jwtProvider.validateToken(token)) {
            throw new CustomException("만료되었거나 토큰이 잘못됐습니다!", HttpStatus.UNAUTHORIZED);
        }
        Pageable pageable = PageRequest.of(page, size);


        User user = userRepository.findByUsername(username);
        Page<Board> boards = boardRepository.findByUser(pageable, user);
        Page<BoardDTO> boardDTOS = boards.map(board -> {
            BoardDTO boardDTO = new BoardDTO();
            boardDTO.setNickname(board.getUser().getNickname());
            boardDTO.setTitle(board.getTitle());
            boardDTO.setContent(board.getContent());
            boardDTO.setCategory(board.getCategory().stream().map(Enum::toString).collect(Collectors.toList()));
            boardDTO.setCreateAt(board.getCreateAt());
            List<Comment> comments = board.getCommentList();
            if (comments == null) {
                boardDTO.setComment_count(0);
            } else {
                boardDTO.setComment_count(board.getCommentList().size());
            }
            return boardDTO;
        });

        return boardDTOS;

    }

    //카테고리별 분류
    public Page<BoardDTO> getCategoryBoardPage(Category category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        //Category categoryEnum = Category.valueOf(category);

        if (category == null) {
            throw new CustomException("카테고리를 설정하지 않았습니다!", HttpStatus.BAD_REQUEST);
        }

        Page<Board> boards = boardRepository.findByCategory(category, pageable);
        Page<BoardDTO> boardDTOS = boards.map(board -> {
            BoardDTO boardDTO = new BoardDTO();
            boardDTO.setNickname(board.getUser().getNickname());
            boardDTO.setTitle(board.getTitle());
            boardDTO.setContent(board.getContent());
            boardDTO.setCategory(board.getCategory().stream().map(Enum::toString).collect(Collectors.toList()));
            boardDTO.setCreateAt(board.getCreateAt());
            List<Comment> comments = board.getCommentList();
            if (comments == null) {
                boardDTO.setComment_count(0);
            } else {
                boardDTO.setComment_count(board.getCommentList().size());
            }
            return boardDTO;
        });


        return boardDTOS;
    }

    //글수정
    public BoardDTO updateBoard(Long id, BoardUpdateFormDTO boardUpdateFormDTO, String token) {
        if (!jwtProvider.validateToken(token)) {
            throw new CustomException("만료되었거나 토큰이 잘못됐습니다!", HttpStatus.UNAUTHORIZED);
        }

        String username = jwtProvider.getUsername(token);
        String currentUser = ((UserDetails) jwtProvider.getAuthentication(token).getPrincipal()).getUsername();

        if (!currentUser.equals(username)) {
            throw new CustomException("권한이 없습니다!", HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findByUsername(username);
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new CustomException("글정보를 찾을 수 없습니다!", HttpStatus.NOT_FOUND));

        if (!user.getUsername().equals(board.getUser().getUsername())) {
            throw new CustomException("회원정보가 잘못됐습니다!", HttpStatus.UNAUTHORIZED);
        }


        board.setTitle(boardUpdateFormDTO.getTitle());
        board.setContent(boardUpdateFormDTO.getContent());
        board.setCategory(boardUpdateFormDTO.getCategory());
        board.setImage(boardUpdateFormDTO.getMultipartFile());
        boardRepository.save(board);

        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setTitle(board.getTitle());
        boardDTO.setNickname(user.getNickname());
        boardDTO.setContent(board.getContent());
        boardDTO.setCategory(board.getCategory().stream().map(Enum::toString).collect(Collectors.toList()));
        boardDTO.setCreateAt(board.getCreateAt());
        if (board.getCommentList() == null) {
            boardDTO.setComment_count(0);
        } else {
            boardDTO.setComment_count(board.getCommentList().size());
        }

        return boardDTO;
    }

    //글삭제
    public boolean deleteBoard(Long id, String token) {
        if (!jwtProvider.validateToken(token)) {
            throw new CustomException("만료되었거나 토큰이 잘못됐습니다!", HttpStatus.UNAUTHORIZED);
        }
        Board board = boardRepository.findById(id).orElseThrow(() -> new CustomException("글정보를 찾을 수 없습니다!", HttpStatus.NOT_FOUND));
        String username = jwtProvider.getUsername(token);
        User user = userRepository.findByUsername(username);
        if (!user.getUsername().equals(board.getUser().getUsername())) {
            throw new CustomException("권한이 없습니다!", HttpStatus.UNAUTHORIZED);
        }
        boardRepository.delete(board);

        return true;
    }

}
