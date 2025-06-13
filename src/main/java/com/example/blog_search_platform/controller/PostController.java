package com.example.blog_search_platform.controller;

import com.example.blog_search_platform.dto.PostCreateRequest;
import com.example.blog_search_platform.dto.PostResponse;
import com.example.blog_search_platform.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody PostCreateRequest request) {
        PostResponse response = postService.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 게시글 단건 조회 API
     * @param postId URL 경로에서 추출한 게시글 ID
     * @return 조회된 게시글 정보와 HTTP 상태 코드 200 (OK)
     */
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long postId) {
        PostResponse response = postService.getPost(postId);
        return ResponseEntity.ok(response);
    }
}