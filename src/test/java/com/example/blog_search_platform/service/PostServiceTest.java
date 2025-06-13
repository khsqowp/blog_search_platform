package com.example.blog_search_platform.service;

import com.example.blog_search_platform.domain.Post;
import com.example.blog_search_platform.dto.PostCreateRequest;
import com.example.blog_search_platform.dto.PostResponse;
import com.example.blog_search_platform.dto.PostUpdateRequest;
import com.example.blog_search_platform.exception.PostNotFoundException;
import com.example.blog_search_platform.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    // --- 기존 테스트 메서드 (생성, 조회) ---
    @Test
    @DisplayName("게시글 생성 요청이 들어오면, 게시글을 성공적으로 생성한다.")
    void createPostSuccess() { /* ... */ }

    @Test
    @DisplayName("존재하는 ID로 게시글을 조회하면, 성공적으로 조회된다.")
    void getPostSuccess() { /* ... */ }

    @Test
    @DisplayName("존재하지 않는 ID로 게시글을 조회하면, PostNotFoundException 예외가 발생한다.")
    void getPostFail_whenPostNotFound() { /* ... */ }


    // --- 수정 관련 테스트 메서드 ---
    @Test
    @DisplayName("존재하는 게시글의 제목과 내용을 수정하면, 성공적으로 수정된다.")
    void updatePostSuccess() {
        // given
        long postId = 1L;
        Post existingPost = Post.builder()
                .title("원본 제목")
                .contents("원본 내용")
                .build();

        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
                .title("수정된 제목")
                .contents("수정된 내용")
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));

        // when
        PostResponse response = postService.updatePost(postId, updateRequest);

        // then
        assertThat(response.getTitle()).isEqualTo("수정된 제목");
        assertThat(response.getContents()).isEqualTo("수정된 내용");
        verify(postRepository).findById(postId);
    }

    @Test
    @DisplayName("게시글 수정 시, 제목만 수정 요청하면 제목만 변경된다.")
    void updatePost_onlyTitle() {
        // given
        long postId = 1L;
        Post existingPost = Post.builder()
                .title("원본 제목")
                .contents("원본 내용")
                .build();

        // 내용(contents)은 null로 보내서 제목만 수정되도록 요청
        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
                .title("수정된 제목")
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));

        // when
        PostResponse response = postService.updatePost(postId, updateRequest);

        // then
        assertThat(response.getTitle()).isEqualTo("수정된 제목");
        assertThat(response.getContents()).isEqualTo("원본 내용"); // 내용은 그대로여야 한다.
        verify(postRepository).findById(postId);
    }

    @Test
    @DisplayName("존재하지 않는 게시글을 수정하려고 하면, PostNotFoundException 예외가 발생한다.")
    void updatePostFail_whenPostNotFound() {
        // given
        long nonExistentPostId = 999L;
        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
                .title("수정될 리 없는 제목")
                .build();

        when(postRepository.findById(nonExistentPostId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postService.updatePost(nonExistentPostId, updateRequest))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessage("존재하지 않는 게시글 ID 입니다: " + nonExistentPostId);

        verify(postRepository).findById(nonExistentPostId);
    }
}