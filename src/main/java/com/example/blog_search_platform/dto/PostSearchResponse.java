package com.example.blog_search_platform.dto;

import com.example.blog_search_platform.document.PostDocument;
import lombok.Builder;
import lombok.Getter;

/**
 * 게시글 검색 결과를 클라이언트에게 반환할 때 사용하는 DTO
 */
@Getter
public class PostSearchResponse {
    private final Long id;
    private final String title;
    private final String contents;

    @Builder
    public PostSearchResponse(Long id, String title, String contents) {
        this.id = id;
        this.title = title;
        this.contents = contents;
    }

    /**
     * PostDocument를 PostSearchResponse DTO로 변환하는 정적 팩토리 메서드
     * @param document Elasticsearch의 PostDocument
     * @return 변환된 PostSearchResponse
     */
    public static PostSearchResponse from(PostDocument document) {
        return PostSearchResponse.builder()
                .id(document.getId())
                .title(document.getTitle())
                .contents(document.getContents())
                .build();
    }
}