package com.example.blog_search_platform.exception;

/**
 * 게시글을 찾을 수 없을 때 발생하는 커스텀 예외 클래스
 */
public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(String message) {
        super(message);
    }
}