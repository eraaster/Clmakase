package com.oliveyoung.sale.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 통일된 API 응답 형식
 *
 * [면접 포인트]
 * Q: "왜 통일된 응답 형식을 사용하나요?"
 * A: 1) 프론트엔드에서 일관된 에러 처리 가능
 *    2) success 필드로 성공/실패 즉시 판단
 *    3) errorCode로 세분화된 에러 처리 (다국어, 사용자 메시지 분리)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        T data,
        String message,
        String errorCode
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null, null);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, message, null);
    }

    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return new ApiResponse<>(false, null, message, errorCode);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, null, message, null);
    }
}
