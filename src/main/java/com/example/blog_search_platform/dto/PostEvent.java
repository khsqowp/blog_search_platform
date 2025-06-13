package com.example.blog_search_platform.dto;

import lombok.Getter;

/**
 * 게시글의 데이터 변경(C/U/D) 이벤트를 나타내는 객체
 */
@Getter
public class PostEvent {

    private final Long postId;
    private final EventType eventType;

    public PostEvent(Long postId, EventType eventType) {
        this.postId = postId;
        this.eventType = eventType;
    }

    public enum EventType {
        CREATED,
        UPDATED,
        DELETED
    }
}