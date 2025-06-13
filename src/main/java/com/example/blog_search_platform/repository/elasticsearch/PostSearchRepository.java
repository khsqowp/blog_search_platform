package com.example.blog_search_platform.repository.elasticsearch;

import com.example.blog_search_platform.document.PostDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PostSearchRepository extends ElasticsearchRepository<PostDocument, Long> {

    /**
     * multi_match 쿼리를 사용하여 여러 필드(title, contents)에서 키워드로 검색합니다.
     * 이 방식은 nori 분석기를 올바르게 적용하여 정확한 전문 검색을 수행합니다.
     * @param keyword 검색할 키워드
     * @param pageable 페이징 정보
     * @return 검색된 PostDocument 페이지
     */
    @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"title\", \"contents\"]}}")
    Page<PostDocument> findByKeyword(String keyword, Pageable pageable);
}