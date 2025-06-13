package com.example.blog_search_platform.listener;

import com.example.blog_search_platform.document.PostDocument;
import com.example.blog_search_platform.dto.PostEvent;
import com.example.blog_search_platform.repository.PostRepository;
import com.example.blog_search_platform.repository.elasticsearch.PostSearchRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostKafkaConsumer {

    private final PostRepository postRepository;
    private final PostSearchRepository postSearchRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "post-events", groupId = "${spring.kafka.consumer.group-id}")
    public void consumePostEvent(String message) {
        try {
            PostEvent event = objectMapper.readValue(message, PostEvent.class);
            Long postId = event.getPostId();
            log.info("Kafka message consumed: type={}, postId={}", event.getEventType(), postId);

            switch (event.getEventType()) {
                case CREATED, UPDATED:
                    postRepository.findById(postId).ifPresent(post -> {
                        postSearchRepository.save(PostDocument.from(post));
                        log.info("Post document saved/updated to Elasticsearch: id={}", post.getId());
                    });
                    break;
                case DELETED:
                    postSearchRepository.deleteById(postId);
                    log.info("Post document deleted from Elasticsearch: id={}", postId);
                    break;
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize message: {}", message, e);
        }
    }
}
