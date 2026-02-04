package com.oliveyoung.sale.dto;

/**
 * 대기열 진입 응답 DTO
 *
 * [프론트엔드 연동 포인트]
 * - token: 이후 상태 조회 및 구매 시 필요 (세션 스토리지에 저장)
 * - position: 현재 대기 순번
 * - estimatedWaitSeconds: 예상 대기 시간 (UI 표시용)
 */
public record QueueEntryResponse(
        String token,
        int position,
        int estimatedWaitSeconds,
        String message
) {}
