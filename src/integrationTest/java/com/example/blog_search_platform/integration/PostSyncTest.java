package com.example.blog_search_platform.integration;

import com.example.blog_search_platform.document.PostDocument;
import com.example.blog_search_platform.dto.PostCreateRequest;
import com.example.blog_search_platform.dto.PostUpdateRequest;
import com.example.blog_search_platform.repository.PostRepository;
import com.example.blog_search_platform.repository.elasticsearch.PostSearchRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PostSyncTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostSearchRepository postSearchRepository;

    @AfterEach
    void tearDown() {
        postRepository.deleteAllInBatch();
        postSearchRepository.deleteAll();
    }

    @Test
    @DisplayName("게시글을 생성하면, Kafka를 통해 Elasticsearch에도 자동으로 색인된다.")
    void createPost_thenSyncToElasticsearch() throws Exception {
        // given
        PostCreateRequest request = PostCreateRequest.builder()
                .title("동기화 테스트 제목")
                .contents("동기화 테스트 내용입니다.")
                .build();
        String jsonRequest = objectMapper.writeValueAsString(request);

        // when
        MvcResult result = mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                )
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        Long createdPostId = ((Integer) JsonPath.read(responseBody, "$.id")).longValue();

        // then: Elasticsearch에 문서가 생길 때까지 최대 5초간 기다렸다가 검증한다.
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            PostDocument savedDocument = postSearchRepository.findById(createdPostId)
                    .orElseThrow(() -> new AssertionError("Elasticsearch에서 문서를 찾을 수 없습니다. ID: " + createdPostId));
            assertThat(savedDocument.getTitle()).isEqualTo("동기화 테스트 제목");
        });
    }

    @Test
    @DisplayName("게시글을 수정하면, Kafka를 통해 Elasticsearch 문서도 자동으로 업데이트된다.")
    void updatePost_thenSyncToElasticsearch() throws Exception {
        // given
        // 1. 게시글 생성 및 동기화 대기
        PostCreateRequest createRequest = PostCreateRequest.builder().title("원본 제목").contents("원본 내용").build();
        MvcResult createResult = mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        String responseBody = createResult.getResponse().getContentAsString();
        Long postId = ((Integer) JsonPath.read(responseBody, "$.id")).longValue();

        // 2. ES에 색인될 때까지 대기
        await().atMost(5, TimeUnit.SECONDS)
                .until(() -> postSearchRepository.existsById(postId));

        // 3. 수정 요청 준비
        PostUpdateRequest updateRequest = PostUpdateRequest.builder().title("수정된 동기화 제목").build();

        // when
        mockMvc.perform(patch("/api/posts/{postId}", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
        ).andExpect(status().isOk());

        // then: Elasticsearch 문서가 업데이트될 때까지 기다렸다가 검증한다.
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            PostDocument updatedDocument = postSearchRepository.findById(postId)
                    .orElseThrow(() -> new AssertionError("Elasticsearch에서 문서를 찾을 수 없습니다."));
            assertThat(updatedDocument.getTitle()).isEqualTo("수정된 동기화 제목");
        });
    }

    @Test
    @DisplayName("게시글을 삭제하면, Kafka를 통해 Elasticsearch 문서도 자동으로 삭제된다.")
    void deletePost_thenSyncToElasticsearch() throws Exception {
        // given
        // 1. 게시글 생성 및 동기화 대기
        PostCreateRequest createRequest = PostCreateRequest.builder().title("삭제될 제목").contents("삭제될 내용").build();
        MvcResult createResult = mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        String responseBody = createResult.getResponse().getContentAsString();
        Long postId = ((Integer) JsonPath.read(responseBody, "$.id")).longValue();

        // 2. ES에 색인될 때까지 대기
        await().atMost(5, TimeUnit.SECONDS)
                .until(() -> postSearchRepository.existsById(postId));

        // when
        mockMvc.perform(delete("/api/posts/{postId}", postId))
                .andExpect(status().isNoContent());

        // then: Elasticsearch 문서가 삭제될 때까지 기다렸다가 검증한다.
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() ->
                assertThat(postSearchRepository.existsById(postId)).isFalse()
        );
    }
}