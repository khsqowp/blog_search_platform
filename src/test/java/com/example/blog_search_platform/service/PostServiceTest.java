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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Test
    @DisplayName("게시글 생성 요청이 들어오면, 게시글을 성공적으로 생성한다.")
    void createPostSuccess() {
        // given
        PostCreateRequest request = PostCreateRequest.builder()
                .title("테스트 제목")
                .contents("테스트 내용")
                .build();
        Post mockPost = Post.builder()
                .title("테스트 제목")
                .contents("테스트 내용")
                .build();
        when(postRepository.save(any(Post.class))).thenReturn(mockPost);

        // when
        PostResponse response = postService.createPost(request);

        // then
        assertThat(response.getTitle()).isEqualTo("테스트 제목");
        assertThat(response.getContents()).isEqualTo("테스트 내용");
        verify(postRepository).save(any(Post.class));
    }

    @Test
    @DisplayName("존재하는 ID로 게시글을 조회하면, 성공적으로 조회된다.")
    void getPostSuccess() {
        // given
        long postId = 1L;
        Post mockPost = Post.builder()
                .title("조회용 제목")
                .contents("조회용 내용")
                .build();
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(mockPost));

        // when
        PostResponse response = postService.getPost(postId);

        // then
        assertThat(response.getTitle()).isEqualTo("조회용 제목");
        assertThat(response.getContents()).isEqualTo("조회용 내용");
        verify(postRepository).findById(postId);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 게시글을 조회하면, PostNotFoundException 예외가 발생한다.")
    void getPostFail_whenPostNotFound() {
        // given
        long nonExistentPostId = 999L;
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postService.getPost(nonExistentPostId))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessage("존재하지 않는 게시글 ID 입니다: " + nonExistentPostId);

        verify(postRepository).findById(nonExistentPostId);
    }

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
    @DisplayName("존재하지 않는 게시글을 수정하려고 하면, PostNotFoundException 예외가 발생한다.")
    void updatePostFail_whenPostNotFound() {
        // given
        long nonExistentPostId = 999L;
        PostUpdateRequest updateRequest = PostUpdateRequest.builder().build();
        when(postRepository.findById(nonExistentPostId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postService.updatePost(nonExistentPostId, updateRequest))
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    @DisplayName("존재하는 ID로 게시글 삭제를 요청하면, 성공적으로 삭제된다.")
    void deletePostSuccess() {
        // given
        long postId = 1L;
        Post existingPost = Post.builder().build();
        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        doNothing().when(postRepository).delete(existingPost);

        // when
        postService.deletePost(postId);

        // then
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).delete(existingPost);
    }

    @Test
    @DisplayName("존재하지 않는 게시글을 삭제하려고 하면, PostNotFoundException 예외가 발생한다.")
    void deletePostFail_whenPostNotFound() {
        // given
        long nonExistentPostId = 999L;
        when(postRepository.findById(nonExistentPostId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postService.deletePost(nonExistentPostId))
                .isInstanceOf(PostNotFoundException.class);
        verify(postRepository, never()).delete(any(Post.class));
    }
}