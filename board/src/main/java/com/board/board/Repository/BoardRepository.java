package com.board.board.Repository;

import com.board.board.Entity.Board;
import com.board.board.Entity.Category;
import com.board.board.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    Board findByTitle(String title);
    Page<Board> findAll(Pageable pageable);
    Page<Board> findByCategory(Category category, Pageable pageable);

    Page<Board> findByUser(Pageable pageable, User user);
}
