package com.example.blog_search_platform.service;

import com.example.blog_search_platform.domain.Post;
import com.example.blog_search_platform.dto.PostCreateRequest;
import com.example.blog_search_platform.dto.PostEvent;
import com.example.blog_search_platform.dto.PostResponse;
import com.example.blog_search_platform.dto.PostUpdateRequest;
import com.example.blog_search_platform.exception.PostNotFoundException;
import com.example.blog_search_platform.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final ApplicationEventPublisher eventPublisher; // 이벤트 발행을 위한 객체

    @Transactional
    public PostResponse createPost(PostCreateRequest request) {
        Post newPost = request.toEntity();
        Post savedPost = postRepository.save(newPost);
        // DB 트랜잭션이 성공적으로 commit된 후에 실행될 이벤트를 발행합니다.
        eventPublisher.publishEvent(new PostEvent(savedPost.getId(), PostEvent.EventType.CREATED));
        return new PostResponse(savedPost);
    }

    @Transactional(readOnly = true)
    public PostResponse getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("존재하지 않는 게시글 ID 입니다: " + postId));
        return new PostResponse(post);
    }

    @Transactional
    public PostResponse updatePost(Long postId, PostUpdateRequest request) {
        Post postToUpdate = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("존재하지 않는 게시글 ID 입니다: " + postId));
        postToUpdate.update(request.getTitle(), request.getContents());
        eventPublisher.publishEvent(new PostEvent(postToUpdate.getId(), PostEvent.EventType.UPDATED));
        return new PostResponse(postToUpdate);
    }

    @Transactional
    public void deletePost(Long postId) {
        Post postToDelete = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("존재하지 않는 게시글 ID 입니다: " + postId));
        postRepository.delete(postToDelete);
        eventPublisher.publishEvent(new PostEvent(postId, PostEvent.EventType.DELETED));
    }
}
