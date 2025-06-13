package com.example.blog_search_platform.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시글 수정을 요청할 때 사용하는 DTO.
 * 수정되지 않는 필드는 null이 될 수 있습니다.
 */
@Getter
@NoArgsConstructor
public class PostUpdateRequest {
    private String title;
    private String contents;

    @Builder
    public PostUpdateRequest(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }
}