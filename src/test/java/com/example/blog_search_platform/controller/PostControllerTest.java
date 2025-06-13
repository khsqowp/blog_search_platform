package com.example.blog_search_platform.controller;

import com.example.blog_search_platform.domain.Post;
import com.example.blog_search_platform.dto.PostCreateRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("API 테스트 제목"))
                .andExpect(jsonPath("$.contents").value("API 테스트 내용"))
                .andDo(print());

        assertThat(postRepository.count()).isEqualTo(1);
        assertThat(postRepository.findAll().get(0).getTitle()).isEqualTo("API 테스트 제목");
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
                .andExpect(jsonPath("$.id").value(savedPost.getId()))
                .andExpect(jsonPath("$.title").value("조회용 API 제목"))
                .andExpect(jsonPath("$.contents").value("조회용 API 내용"))
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 ID로 게시글 조회 API 요청이 들어오면, HTTP 404 상태와 에러 메시지를 반환한다.")
    void getPostApiFail_whenPostNotFound() throws Exception {
        // given
        long nonExistentPostId = 999L;

        // when & then
        mockMvc.perform(get("/api/posts/{postId}", nonExistentPostId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("존재하지 않는 게시글 ID 입니다: " + nonExistentPostId))
                .andDo(print());
    }
}
