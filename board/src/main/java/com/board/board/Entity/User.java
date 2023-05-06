package com.board.board.Entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 4, max = 40, message = "아이디는 최소 4 최대40 입니다!")
    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Size(max = 50, message = "닉네임은 50글자까지 입니다!")
    @Column(unique = true, nullable = false)
    private String nickname;

    @Size(min = 4, message = "비밀번호는 최소 4글자입니다!")
    private String password;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createAt;

    @ElementCollection(fetch = FetchType.EAGER)
    List<UserRole> userRoles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Board> boardList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> commentList;
}
