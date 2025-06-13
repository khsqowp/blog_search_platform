package com.example.blog_search_platform.controller;

import com.example.blog_search_platform.dto.PostCreateRequest;
import com.example.blog_search_platform.dto.PostResponse;
import com.example.blog_search_platform.dto.PostSearchResponse;
import com.example.blog_search_platform.dto.PostUpdateRequest;
import com.example.blog_search_platform.service.PostSearchService;
import com.example.blog_search_platform.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostSearchService postSearchService; // 검색 서비스 주입

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody PostCreateRequest request) {
        PostResponse response = postService.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long postId) {
        PostResponse response = postService.getPost(postId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Long postId, @RequestBody PostUpdateRequest request) {
        PostResponse response = postService.updatePost(postId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 게시글 검색 API
     * @param keyword URL 쿼리 파라미터로 전달된 검색 키워드
     * @param pageable 페이징 정보 (e.g., ?page=0&size=10)
     * @return 검색된 게시글 목록 (페이징 포함)과 HTTP 상태 코드 200 (OK)
     */
    @GetMapping("/search")
    public ResponseEntity<Page<PostSearchResponse>> searchPosts(
            @RequestParam String keyword,
            Pageable pageable
    ) {
        Page<PostSearchResponse> results = postSearchService.search(keyword, pageable);
        return ResponseEntity.ok(results);
    }
}
