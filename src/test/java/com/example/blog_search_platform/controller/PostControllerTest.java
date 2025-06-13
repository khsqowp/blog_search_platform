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
import org.springframework.test.web.servlet.MvcResult;
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

    // --- 기존 테스트 메서드 (생성, 조회) ---
    @Test
    @DisplayName("게시글 생성 API 요청이 들어오면, HTTP 201 상태와 함께 생성된 게시글 정보를 반환한다.")
    void createPostApiSuccess() throws Exception { /* ... */ }

    @Test
    @DisplayName("게시글 단건 조회 API 요청이 들어오면, HTTP 200 상태와 함께 게시글 정보를 반환한다.")
    void getPostApiSuccess() throws Exception { /* ... */ }

    @Test
    @DisplayName("존재하지 않는 ID로 게시글 조회 API 요청이 들어오면, HTTP 404 상태와 에러 메시지를 반환한다.")
    void getPostApiFail_whenPostNotFound() throws Exception { /* ... */ }


    // --- 수정 관련 테스트 메서드 ---
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
                .contents("수정된 API 내용")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(updateRequest);

        // when & then
        mockMvc.perform(patch("/api/posts/{postId}", savedPost.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedPost.getId()))
                .andExpect(jsonPath("$.title").value("수정된 API 제목"))
                .andExpect(jsonPath("$.contents").value("수정된 API 내용"))
                .andDo(print());

        // DB에 실제로 데이터가 수정되었는지 추가 검증
        Post updatedPost = postRepository.findById(savedPost.getId()).get();
        assertThat(updatedPost.getTitle()).isEqualTo("수정된 API 제목");
        assertThat(updatedPost.getContents()).isEqualTo("수정된 API 내용");
    }
}