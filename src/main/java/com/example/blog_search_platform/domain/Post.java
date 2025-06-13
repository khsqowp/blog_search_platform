package com.example.blog_search_platform.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob
    @Column(nullable = false)
    private String contents;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Builder
    public Post(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    /**
     * 게시글의 제목과 내용을 수정합니다.
     * 수정 요청 DTO의 필드가 null이 아닌 경우에만 값을 변경합니다.
     * @param newTitle 새 제목
     * @param newContents 새 내용
     */
    public void update(String newTitle, String newContents) {
        if (newTitle != null) {
            this.title = newTitle;
        }
        if (newContents != null) {
            this.contents = newContents;
        }
    }
}