package com.example.blog_search_platform.service;

import com.example.blog_search_platform.domain.Post;
import com.example.blog_search_platform.dto.PostCreateRequest;
import com.example.blog_search_platform.dto.PostResponse;
import com.example.blog_search_platform.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * PostService에 대한 단위 테스트.
 * 외부 의존성(PostRepository)을 Mock(가짜 객체)으로 대체하여 서비스 로직만 순수하게 테스트합니다.
 */
@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks // 테스트 대상이 되는 클래스. @Mock으로 생성된 객체를 주입받습니다.
    private PostService postService;

    @Mock // 가짜 객체로 만들 클래스
    private PostRepository postRepository;

    @Test
    @DisplayName("게시글 생성 요청이 들어오면, 게시글을 성공적으로 생성한다.")
    void createPostSuccess() {
        // given: 테스트를 위한 사전 준비
        // 1. 생성 요청 DTO 생성
        PostCreateRequest request = PostCreateRequest.builder()
                .title("테스트 제목")
                .contents("테스트 내용")
                .build();

        // 2. Mock Repository가 반환할 Post 엔티티를 미리 정의
        Post mockPost = Post.builder()
                .title("테스트 제목")
                .contents("테스트 내용")
                .build();
        // 실제로는 DB에 저장되지 않으므로, ID와 날짜를 수동으로 설정해줍니다.
        // ReflectionTestUtils.setField(mockPost, "id", 1L);

        // 3. postRepository.save() 메서드가 어떤 Post 객체든 인자로 받으면,
        //    미리 정의한 mockPost를 반환하도록 설정
        when(postRepository.save(any(Post.class))).thenReturn(mockPost);

        // when: 테스트하려는 실제 동작 실행
        PostResponse response = postService.createPost(request);

        // then: 테스트 결과 검증
        assertThat(response.getTitle()).isEqualTo("테스트 제목");
        assertThat(response.getContents()).isEqualTo("테스트 내용");

        // verify: postRepository.save() 메서드가 정확히 1번 호출되었는지 검증
        verify(postRepository).save(any(Post.class));
    }
}