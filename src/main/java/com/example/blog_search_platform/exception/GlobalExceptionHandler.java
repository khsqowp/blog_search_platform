package com.example.blog_search_platform.exception;

import com.example.blog_search_platform.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 애플리케이션 전역의 예외를 처리하는 클래스
 */
@RestControllerAdvice // 모든 @RestController에서 발생하는 예외를 가로챕니다.
public class GlobalExceptionHandler {

    /**
     * PostNotFoundException 예외를 처리하는 핸들러 메서드
     * @param e 발생한 PostNotFoundException 예외
     * @return 에러 메시지와 HTTP 상태 코드 404 (Not Found)를 담은 응답
     */
    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePostNotFoundException(PostNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * 그 외 처리하지 않은 모든 예외를 처리하는 핸들러 메서드
     * @param e 발생한 Exception 예외
     * @return 에러 메시지와 HTTP 상태 코드 500 (Internal Server Error)를 담은 응답
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse("서버 내부 오류가 발생했습니다: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}