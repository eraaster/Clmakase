package com.oliveyoung.sale.dto;

/**
 * 대기열 상태 응답 DTO
 *
 * [프론트엔드 연동 포인트]
 * - canPurchase가 true면 구매 페이지로 이동
 * - expired가 true면 대기열 재진입 필요
 * - position이 변화하면 UI 업데이트
 */
public record QueueStatusResponse(
        int position,
        int estimatedWaitSeconds,
        boolean canPurchase,
        boolean expired,
        String message
) {}
