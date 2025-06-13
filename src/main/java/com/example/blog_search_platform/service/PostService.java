package com.example.blog_search_platform.service;

import com.example.blog_search_platform.domain.Post;
import com.example.blog_search_platform.dto.PostCreateRequest;
import com.example.blog_search_platform.dto.PostResponse;
import com.example.blog_search_platform.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Post 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor // final이 붙은 필드를 인자로 받는 생성자를 자동으로 생성
public class PostService {

    private final PostRepository postRepository;

    /**
     * 새로운 게시글을 생성합니다.
     * @param request 게시글 생성 요청 DTO
     * @return 생성된 게시글 정보 DTO
     */
    @Transactional // 메서드 전체가 하나의 트랜잭션으로 동작하도록 보장
    public PostResponse createPost(PostCreateRequest request) {
        // 1. 요청 DTO를 엔티티로 변환
        Post newPost = request.toEntity();

        // 2. 리포지토리를 통해 엔티티를 데이터베이스에 저장
        Post savedPost = postRepository.save(newPost);

        // 3. 저장된 엔티티를 응답 DTO로 변환하여 반환
        return new PostResponse(savedPost);
    }
}