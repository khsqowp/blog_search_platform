package com.example.blog_search_platform.service;

import com.example.blog_search_platform.domain.Post;
import com.example.blog_search_platform.dto.PostCreateRequest;
import com.example.blog_search_platform.dto.PostEvent;
import com.example.blog_search_platform.dto.PostResponse;
import com.example.blog_search_platform.dto.PostUpdateRequest;
import com.example.blog_search_platform.exception.PostNotFoundException;
import com.example.blog_search_platform.repository.PostRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private static final String TOPIC_NAME = "post-events";

    @Transactional
    public PostResponse createPost(PostCreateRequest request) {
        Post savedPost = postRepository.save(request.toEntity());
        publishEventAfterCommit(new PostEvent(savedPost.getId(), PostEvent.EventType.CREATED));
        return new PostResponse(savedPost);
    }

    @Transactional
    public PostResponse updatePost(Long postId, PostUpdateRequest request) {
        Post postToUpdate = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("존재하지 않는 게시글 ID 입니다: " + postId));
        postToUpdate.update(request.getTitle(), request.getContents());
        // DB에 commit된 후에 Kafka 메시지를 보내도록 변경
        publishEventAfterCommit(new PostEvent(postToUpdate.getId(), PostEvent.EventType.UPDATED));
        return new PostResponse(postToUpdate);
    }

    @Transactional
    public void deletePost(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException("존재하지 않는 게시글 ID 입니다: " + postId);
        }
        postRepository.deleteById(postId);
        // DB에 commit된 후에 Kafka 메시지를 보내도록 변경
        publishEventAfterCommit(new PostEvent(postId, PostEvent.EventType.DELETED));
    }

    /**
     * 현재 진행중인 트랜잭션이 성공적으로 커밋된 후에만 Kafka 메시지를 발행합니다.
     * @param event 발행할 이벤트 객체
     */
    private void publishEventAfterCommit(PostEvent event) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                try {
                    String eventJson = objectMapper.writeValueAsString(event);
                    kafkaTemplate.send(TOPIC_NAME, eventJson);
                    log.info("Kafka message sent AFTER COMMIT: {}", eventJson);
                } catch (JsonProcessingException e) {
                    log.error("Failed to serialize PostEvent to JSON after commit", e);
                }
            }
        });
    }

    @Transactional(readOnly = true)
    public PostResponse getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("존재하지 않는 게시글 ID 입니다: " + postId));
        return new PostResponse(post);
    }
}
