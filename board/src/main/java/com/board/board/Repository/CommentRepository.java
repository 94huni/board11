package com.board.board.Repository;

import com.board.board.Entity.Board;
import com.board.board.Entity.Comment;
import com.board.board.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByUser(Pageable pageable, User user);

    Page<Comment> findByBoard(Pageable pageable, Board board);
}
