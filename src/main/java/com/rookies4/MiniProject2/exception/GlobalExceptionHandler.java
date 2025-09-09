package com.rookies4.MiniProject2.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @Valid 어노테이션을 사용한 DTO의 유효성 검사에 실패했을 때 발생하는 예외를 처리합니다.
     * HTTP 400 Bad Request 상태와 함께 발생한 필드와 에러 메시지를 응답합니다.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("handleMethodArgumentNotValidException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, e.getBindingResult().getFieldError());
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 잘못된 인수나 상태로 인해 발생하는 예외를 처리합니다.
     * (예: "이미 가입된 모임입니다.", "최대 인원수를 현재 인원수보다 적게 설정할 수 없습니다.")
     * HTTP 400 Bad Request 상태와 에러 메시지를 응답합니다.
     */
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    protected ResponseEntity<ErrorResponse> handleBusinessException(RuntimeException e) {
        log.error("handleBusinessException", e);
        final ErrorResponse response = ErrorResponse.builder()
                .errorCode(ErrorCode.BAD_REQUEST.getCode())
                .message(e.getMessage())
                .build();
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 인증된 사용자가 특정 리소스에 접근할 권한이 없을 때 발생하는 예외를 처리합니다.
     * (예: 일반 사용자가 관리자 API를 호출)
     * HTTP 403 Forbidden 상태와 함께 접근 거부 메시지를 응답합니다.
     */
    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        log.error("handleAccessDeniedException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.ACCESS_DENIED);
        return ResponseEntity.status(ErrorCode.ACCESS_DENIED.getStatus()).body(response);
    }

    /**
     * 존재하지 않는 사용자를 조회하려고 할 때 발생하는 예외를 처리합니다.
     * HTTP 404 Not Found 상태와 함께 사용자 없음 메시지를 응답합니다.
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    protected ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException e) {
        log.error("handleUsernameNotFoundException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.USER_NOT_FOUND);
        return ResponseEntity.status(ErrorCode.USER_NOT_FOUND.getStatus()).body(response);
    }


    /**
     * 위에서 정의되지 않은 모든 예외를 처리합니다.
     * HTTP 500 Internal Server Error 상태와 함께 서버 내부 오류 메시지를 응답합니다.
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("handleException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
        return ResponseEntity.internalServerError().body(response);
    }
}
