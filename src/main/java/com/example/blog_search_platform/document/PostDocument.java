package com.example.blog_search_platform.document;

import com.example.blog_search_platform.domain.Post;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(indexName = "posts")
public class PostDocument {

    @Id
    private Long id;

    // 우리가 NoriAnalyzerConfig에서 정의한 커스텀 분석기 이름("nori_analyzer_custom")을 지정합니다.
    @Field(type = FieldType.Text, analyzer = "nori_analyzer_custom")
    private String title;

    @Field(type = FieldType.Text, analyzer = "nori_analyzer_custom")
    private String contents;

    @Builder
    public PostDocument(Long id, String title, String contents) {
        this.id = id;
        this.title = title;
        this.contents = contents;
    }

    public static PostDocument from(Post post) {
        return PostDocument.builder()
                .id(post.getId())
                .title(post.getTitle())
                .contents(post.getContents())
                .build();
    }
}