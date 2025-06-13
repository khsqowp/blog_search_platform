package com.example.blog_search_platform.integration;

import com.example.blog_search_platform.document.PostDocument;
import com.example.blog_search_platform.repository.elasticsearch.PostSearchRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PostSearchApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostSearchRepository postSearchRepository;

    // 테스트 시작 전, 테스트를 위한 검색 데이터들을 미리 저장합니다.
    @BeforeEach
    void setUp() {
        postSearchRepository.saveAll(List.of(
                PostDocument.builder().id(1L).title("오늘의 점심 메뉴").contents("오늘은 맛있는 자바 카레를 먹었다.").build(),
                PostDocument.builder().id(2L).title("스프링부트 기초").contents("자바(Java) 언어로 스프링부트를 다뤄봅시다.").build(),
                PostDocument.builder().id(3L).title("Elasticsearch 설정").contents("검색 엔진 구축은 재미있다.").build(),
                PostDocument.builder().id(4L).title("리액트(React)란?").contents("프론트엔드 라이브러리인 리액트를 배워보자.").build()
        ));
    }

    // 각 테스트가 끝난 후 Elasticsearch의 모든 데이터를 삭제하여 테스트 격리성을 보장합니다.
    @AfterEach
    void tearDown() {
        postSearchRepository.deleteAll();
    }

    @Test
    @DisplayName("키워드로 '자바'를 검색하면, 제목 또는 본문에 '자바'가 포함된 게시글 2개가 조회된다.")
    void searchByKeyword_java() throws Exception {
        // when & then
        mockMvc.perform(get("/api/posts/search")
                        .param("keyword", "자바")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[?(@.id == 1)]").exists())
                .andExpect(jsonPath("$.content[?(@.id == 2)]").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("Nori 분석기를 통해, 키워드 '스프링'으로 검색하면 '스프링부트'가 포함된 게시글이 조회된다.")
    void searchByKeyword_usingNoriAnalyzer() throws Exception {
        // when & then
        mockMvc.perform(get("/api/posts/search")
                        .param("keyword", "스프링")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(2))
                .andDo(print());
    }

    @Test
    @DisplayName("키워드로 '리액트'를 검색하고 페이징을 적용하면, 해당 게시글 1개가 0번 페이지에 조회된다.")
    void searchByKeyword_withPaging() throws Exception {
        // when & then
        mockMvc.perform(get("/api/posts/search")
                        .param("keyword", "리액트")
                        .param("page", "0")
                        .param("size", "5")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.content[0].id").value(4))
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 키워드로 검색하면, 빈 결과 리스트가 반환된다.")
    void searchByNonExistentKeyword() throws Exception {
        // when & then
        mockMvc.perform(get("/api/posts/search")
                        .param("keyword", "파이썬")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.content").isEmpty())
                .andDo(print());
    }
}
