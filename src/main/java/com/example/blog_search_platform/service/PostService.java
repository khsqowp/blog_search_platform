package com.example.blog_search_platform.service;

import com.example.blog_search_platform.domain.Post;
import com.example.blog_search_platform.dto.PostCreateRequest;
import com.example.blog_search_platform.dto.PostResponse;
import com.example.blog_search_platform.dto.PostUpdateRequest;
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

    @Transactional(readOnly = true)
    public PostResponse getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("존재하지 않는 게시글 ID 입니다: " + postId));
        return new PostResponse(post);
    }

    /**
     * 특정 ID의 게시글을 수정합니다.
     * @param postId 수정할 게시글의 ID
     * @param request 수정할 내용을 담은 DTO
     * @return 수정된 게시글 정보 DTO
     */
    @Transactional
    public PostResponse updatePost(Long postId, PostUpdateRequest request) {
        // 1. 수정할 게시글을 조회합니다. 없으면 예외가 발생합니다.
        Post postToUpdate = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("존재하지 않는 게시글 ID 입니다: " + postId));

        // 2. 도메인 객체의 update 메서드를 호출하여 변경합니다.
        //    @Transactional 어노테이션 덕분에 메서드가 종료될 때 변경된 내용이 DB에 자동으로 반영됩니다 (Dirty Checking).
        postToUpdate.update(request.getTitle(), request.getContents());

        // 3. 수정된 결과를 응답 DTO로 변환하여 반환합니다.
        return new PostResponse(postToUpdate);
    }
}