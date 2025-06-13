package com.example.blog_search_platform.service;

import com.example.blog_search_platform.dto.PostSearchResponse;
import com.example.blog_search_platform.repository.elasticsearch.PostSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Elasticsearch를 사용한 게시글 검색 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostSearchService {

    private final PostSearchRepository postSearchRepository;

    /**
     * 키워드를 사용하여 게시글의 제목 또는 내용에서 검색을 수행합니다.
     * @param keyword 검색할 키워드
     * @param pageable 페이징 정보 (e.g., page, size)
     * @return 검색 결과 (페이징 포함)
     */
    public Page<PostSearchResponse> search(String keyword, Pageable pageable) {
        // @Query로 정의된 새로운 검색 메서드를 호출하도록 변경합니다.
        return postSearchRepository.findByKeyword(keyword, pageable)
                .map(PostSearchResponse::from);
    }
}