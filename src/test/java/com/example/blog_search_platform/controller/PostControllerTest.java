package com.example.blog_search_platform.controller;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * PostController에 대한 통합 테스트.
 * MockMvc를 사용하여 실제 API 요청과 응답을 시뮬레이션하고, 전체 애플리케이션의 흐름을 테스트합니다.
 */
@SpringBootTest // 스프링 부트 애플리케이션 컨텍스트를 모두 로드하여 테스트
@AutoConfigureMockMvc // MockMvc를 DI 컨테이너에 빈으로 등록
@Transactional // 각 테스트가 끝난 후 데이터베이스를 롤백하여 테스트 격리성 보장
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc; // API 요청을 시뮬레이션하기 위한 객체

    @Autowired
    private ObjectMapper objectMapper; // Java 객체를 JSON 문자열로 변환하기 위한 객체

    @Autowired
    private PostRepository postRepository; // DB 상태를 직접 확인하기 위한 객체

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
                .andExpect(status().isCreated()) // HTTP 상태 코드가 201 (Created)인지 확인
                .andExpect(jsonPath("$.id").exists()) // 응답 JSON에 id 필드가 있는지 확인
                .andExpect(jsonPath("$.title").value("API 테스트 제목"))
                .andExpect(jsonPath("$.contents").value("API 테스트 내용"))
                .andDo(print()); // 요청/응답 전체 내용 출력

        // DB에 실제로 데이터가 저장되었는지 추가 검증
        assertThat(postRepository.count()).isEqualTo(1);
        assertThat(postRepository.findAll().get(0).getTitle()).isEqualTo("API 테스트 제목");
    }
}
