package com.example.blog_search_platform.listener;

import com.example.blog_search_platform.document.PostDocument;
import com.example.blog_search_platform.domain.Post;
import com.example.blog_search_platform.dto.PostEvent;
import com.example.blog_search_platform.repository.PostRepository;
import com.example.blog_search_platform.repository.elasticsearch.PostSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostSearchEventListener {

    private final PostRepository postRepository;
    private final PostSearchRepository postSearchRepository;

    /**
     * PostEvent를 수신하여 Elasticsearch와 데이터를 동기화합니다.
     * @TransactionalEventListener: DB 트랜잭션의 특정 시점에서 이벤트를 수신합니다.
     * phase = TransactionPhase.AFTER_COMMIT: DB 작업이 성공적으로 commit된 후에만 메서드가 실행되도록 보장합니다.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePostEvent(PostEvent event) {
        Long postId = event.getPostId();
        log.info("Received post event: type={}, postId={}", event.getEventType(), postId);

        switch (event.getEventType()) {
            case CREATED, UPDATED:
                // 커밋된 최신 데이터를 DB에서 다시 조회하여 동기화합니다.
                postRepository.findById(postId).ifPresent(post -> {
                    postSearchRepository.save(PostDocument.from(post));
                    log.info("Post document saved to Elasticsearch: id={}", post.getId());
                });
                break;
            case DELETED:
                postSearchRepository.deleteById(postId);
                log.info("Post document deleted from Elasticsearch: id={}", postId);
                break;
        }
    }
}