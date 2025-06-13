package com.example.blog_search_platform.controller;

import com.example.blog_search_platform.domain.Post;
import com.example.blog_search_platform.dto.PostCreateRequest;
import com.example.blog_search_platform.dto.PostUpdateRequest;
import com.example.blog_search_platform.repository.PostRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostRepository postRepository;

    @AfterEach
    void tearDown() {
        postRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("게시글 생성 API 요청이 들어오면, HTTP 201 상태와 함께 생성된 게시글 정보를 반환한다.")
    void createPostApiSuccess() throws Exception {
        // given
        PostCreateRequest request = PostCreateRequest.builder()
                .title("API 테스트 제목")
                .contents("API 테스트 내용")
                .build();
        String jsonRequest = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                )
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("게시글 단건 조회 API 요청이 들어오면, HTTP 200 상태와 함께 게시글 정보를 반환한다.")
    void getPostApiSuccess() throws Exception {
        // given
        Post savedPost = postRepository.save(Post.builder()
                .title("조회용 API 제목")
                .contents("조회용 API 내용")
                .build());

        // when & then
        mockMvc.perform(get("/api/posts/{postId}", savedPost.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("조회용 API 제목"));
    }

    @Test
    @DisplayName("존재하지 않는 ID로 게시글 조회 API 요청이 들어오면, HTTP 404 상태와 에러 메시지를 반환한다.")
    void getPostApiFail_whenPostNotFound() throws Exception {
        // given
        long nonExistentPostId = 999L;

        // when & then
        mockMvc.perform(get("/api/posts/{postId}", nonExistentPostId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("게시글 수정 API 요청이 들어오면, HTTP 200 상태와 함께 수정된 게시글 정보를 반환한다.")
    void updatePostApiSuccess() throws Exception {
        // given
        Post savedPost = postRepository.save(Post.builder()
                .title("원본 API 제목")
                .contents("원본 API 내용")
                .build());
        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
                .title("수정된 API 제목")
                .build();
        String jsonRequest = objectMapper.writeValueAsString(updateRequest);

        // when & then
        mockMvc.perform(patch("/api/posts/{postId}", savedPost.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("수정된 API 제목"));
    }

    @Test
    @DisplayName("게시글 삭제 API 요청이 들어오면, HTTP 204 상태를 반환한다.")
    void deletePostApiSuccess() throws Exception {
        // given
        Post savedPost = postRepository.save(Post.builder()
                .title("삭제될 제목")
                .contents("삭제될 내용")
                .build());

        // when & then
        mockMvc.perform(delete("/api/posts/{postId}", savedPost.getId()))
                .andExpect(status().isNoContent());
        assertThat(postRepository.existsById(savedPost.getId())).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 ID로 게시글 삭제 API 요청이 들어오면, HTTP 404 상태와 에러 메시지를 반환한다.")
    void deletePostApiFail_whenPostNotFound() throws Exception {
        // given
        long nonExistentPostId = 999L;

        // when & then
        mockMvc.perform(delete("/api/posts/{postId}", nonExistentPostId))
                .andExpect(status().isNotFound());
    }
}