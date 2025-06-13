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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 게시글 데이터 변경 시 Elasticsearch 동기화 로직을 검증하는 통합 테스트.
 * @TransactionalEventListener(phase = AFTER_COMMIT)을 테스트하기 위해
 * @Transactional을 사용하지 않고, 각 테스트 후 수동으로 데이터를 정리합니다.
 */
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
        // 테스트 격리성을 위해 RDBMS와 Elasticsearch의 모든 데이터를 삭제합니다.
        postRepository.deleteAllInBatch();
        postSearchRepository.deleteAll();
    }

    @Test
    @DisplayName("게시글을 생성하면, DB 저장 후 Elasticsearch에도 자동으로 색인된다.")
    void createPost_thenSyncToElasticsearch() throws Exception {
        // given
        PostCreateRequest request = PostCreateRequest.builder()
                .title("동기화 테스트 제목")
                .contents("동기화 테스트 내용입니다.")
                .build();
        String jsonRequest = objectMapper.writeValueAsString(request);

        // when: 게시글 생성 API 호출
        MvcResult result = mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                )
                .andExpect(status().isCreated())
                .andReturn();

        // 생성된 게시글의 ID 추출
        String responseBody = result.getResponse().getContentAsString();
        Integer postIdInt = JsonPath.read(responseBody, "$.id");
        Long createdPostId = postIdInt.longValue();

        // then: Elasticsearch에서 해당 ID로 조회
        PostDocument savedDocument = postSearchRepository.findById(createdPostId)
                .orElseThrow(() -> new AssertionError("Elasticsearch에서 문서를 찾을 수 없습니다."));

        assertThat(savedDocument.getTitle()).isEqualTo("동기화 테스트 제목");
        assertThat(savedDocument.getContents()).isEqualTo("동기화 테스트 내용입니다.");
    }

    @Test
    @DisplayName("게시글을 수정하면, DB 수정 후 Elasticsearch 문서도 자동으로 업데이트된다.")
    void updatePost_thenSyncToElasticsearch() throws Exception {
        // given: 먼저 게시글을 하나 생성하고 동기화
        createPost_thenSyncToElasticsearch();
        Long postId = postSearchRepository.findAll().iterator().next().getId();

        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
                .title("수정된 동기화 제목")
                .build(); // 제목만 수정
        String jsonRequest = objectMapper.writeValueAsString(updateRequest);

        // when: 게시글 수정 API 호출
        mockMvc.perform(patch("/api/posts/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                )
                .andExpect(status().isOk());

        // then: Elasticsearch에서 문서를 다시 조회하여 확인
        PostDocument updatedDocument = postSearchRepository.findById(postId)
                .orElseThrow(() -> new AssertionError("Elasticsearch에서 문서를 찾을 수 없습니다."));

        assertThat(updatedDocument.getTitle()).isEqualTo("수정된 동기화 제목");
        assertThat(updatedDocument.getContents()).isEqualTo("동기화 테스트 내용입니다."); // 내용은 그대로여야 함
    }

    @Test
    @DisplayName("게시글을 삭제하면, DB 삭제 후 Elasticsearch 문서도 자동으로 삭제된다.")
    void deletePost_thenSyncToElasticsearch() throws Exception {
        // given: 먼저 게시글을 하나 생성하고 동기화
        createPost_thenSyncToElasticsearch();
        Long postId = postSearchRepository.findAll().iterator().next().getId();
        assertThat(postSearchRepository.existsById(postId)).isTrue();

        // when: 게시글 삭제 API 호출
        mockMvc.perform(delete("/api/posts/{postId}", postId))
                .andExpect(status().isNoContent());

        // then: Elasticsearch에서 해당 ID의 문서가 존재하지 않음을 확인
        assertThat(postSearchRepository.existsById(postId)).isFalse();
    }
}
