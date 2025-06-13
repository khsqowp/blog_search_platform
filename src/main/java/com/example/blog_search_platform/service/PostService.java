package com.example.blog_search_platform.service;

import com.example.blog_search_platform.domain.Post;
import com.example.blog_search_platform.dto.PostCreateRequest;
import com.example.blog_search_platform.dto.PostResponse;
import com.example.blog_search_platform.exception.PostNotFoundException;
import com.example.blog_search_platform.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    @Transactional
    public PostResponse createPost(PostCreateRequest request) {
        Post newPost = request.toEntity();
        Post savedPost = postRepository.save(newPost);
        return new PostResponse(savedPost);
    }

    /**
     * 특정 ID의 게시글을 조회합니다.
     * @param postId 조회할 게시글의 ID
     * @return 조회된 게시글 정보 DTO
     * @throws PostNotFoundException 해당 ID의 게시글이 존재하지 않을 경우
     */
    @Transactional(readOnly = true) // 조회 기능이므로 readOnly=true 옵션으로 성능 최적화
    public PostResponse getPost(Long postId) {
        // 1. 리포지토리에서 ID로 게시글을 찾습니다.
        Post post = postRepository.findById(postId)
                // 2. 만약 게시글이 존재하지 않으면, 직접 정의한 예외를 발생시킵니다.
                .orElseThrow(() -> new PostNotFoundException("존재하지 않는 게시글 ID 입니다: " + postId));

        // 3. 조회된 엔티티를 응답 DTO로 변환하여 반환합니다.
        return new PostResponse(post);
    }
}