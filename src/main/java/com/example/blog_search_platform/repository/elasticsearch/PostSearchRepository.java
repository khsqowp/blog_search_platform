package com.example.blog_search_platform.repository.elasticsearch;

import com.example.blog_search_platform.document.PostDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * PostDocument에 대한 CRUD 및 검색을 담당하는 Elasticsearch 리포지토리
 */
public interface PostSearchRepository extends ElasticsearchRepository<PostDocument, Long> {
}
