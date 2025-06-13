package com.example.blog_search_platform.dto;

import com.example.blog_search_platform.domain.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시글 생성을 요청할 때 사용하는 DTO
 */
@Getter
@NoArgsConstructor
public class PostCreateRequest {

    private String title;
    private String contents;

    @Builder
    public PostCreateRequest(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    /**
     * DTO를 Post 엔티티로 변환하는 메서드
     */
    public Post toEntity() {
        return Post.builder()
                .title(title)
                .contents(contents)
                .build();
    }
}