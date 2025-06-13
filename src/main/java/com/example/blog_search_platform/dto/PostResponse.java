package com.example.blog_search_platform.dto;

import com.example.blog_search_platform.domain.Post;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 게시글 생성 후 응답으로 사용하는 DTO
 */
@Getter
public class PostResponse {
    private final Long id;
    private final String title;
    private final String contents;
    private final LocalDateTime createdAt;

    public PostResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.contents = post.getContents();
        this.createdAt = post.getCreatedAt();
    }
}