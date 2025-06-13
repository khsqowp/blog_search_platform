package com.example.blog_search_platform.dto;

import lombok.Getter;

/**
 * API 예외 발생 시, 클라이언트에게 반환할 에러 응답 DTO
 */
@Getter
public class ErrorResponse {
    private final String message;
    private final int status;

    public ErrorResponse(String message, int status) {
        this.message = message;
        this.status = status;
    }
}