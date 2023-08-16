package com.example.demo.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_info") // 테이블 이름 지정
@Getter
@Setter
@NoArgsConstructor
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "picture")
    private String picture;

    @Column(name = "sns_provider")
    private String snsProvider;

    @Column(name = "token_info", length = 5000) // 여기서 길이 조정
    private String tokenInfo;

    @Column(name = "join_date") // 예: LocalDateTime 필드를 추가하였음
    private LocalDateTime joinDate;

    public LocalDateTime getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDateTime joinDate) {
        this.joinDate = joinDate;
    }
    // Getters and setters
}