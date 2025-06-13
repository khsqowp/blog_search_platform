package com.example.blog_search_platform.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 블로그 게시글을 나타내는 도메인 클래스 (JPA Entity)
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA는 기본 생성자를 필요로 합니다. 접근 수준을 PROTECTED로 하여 안전성을 높입니다.
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB가 ID를 자동으로 생성하도록 설정 (MySQL의 AUTO_INCREMENT)
    private Long id;

    @Column(nullable = false, length = 200) // null을 허용하지 않고, 길이를 200으로 제한
    private String title;

    @Lob // 대용량 텍스트를 저장하기 위한 어노테이션
    @Column(nullable = false)
    private String contents;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 생성 시각과 수정 시각을 자동으로 관리하기 위한 JPA 생명주기 콜백
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }


    // 빌더 패턴을 사용하여 객체를 생성합니다.
    @Builder
    public Post(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }
}