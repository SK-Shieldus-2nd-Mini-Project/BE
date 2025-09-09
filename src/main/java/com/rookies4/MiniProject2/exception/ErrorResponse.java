package com.rookies4.MiniProject2.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.springframework.validation.FieldError;

@Getter
@Builder
// JsonInclude 어노테이션은 null인 필드를 JSON으로 변환할 때 제외시키는 역할을 합니다.
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private String errorCode;
    private String message;
    private String field; // 유효성 검사 실패 시, 어떤 필드가 잘못되었는지 알려주는 필드

    public static ErrorResponse of(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .errorCode(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
    }

    public static ErrorResponse of(ErrorCode errorCode, FieldError fieldError) {
        return ErrorResponse.builder()
                .errorCode(errorCode.getCode())
                .message(fieldError.getDefaultMessage()) // DTO에 설정된 @NotBlank 등의 메시지를 사용
                .field(fieldError.getField())
                .build();
    }
}
