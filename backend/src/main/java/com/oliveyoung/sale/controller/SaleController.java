package com.oliveyoung.sale.controller;

import com.oliveyoung.sale.dto.ApiResponse;
import com.oliveyoung.sale.service.SaleStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/sale")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SaleController {

    private final SaleStateService saleStateService;

    /**
     * 세일 시작
     * POST /api/sale/start
     *
     * [시연 포인트]
     * 발표 시 이 API를 호출하거나, 프론트의 '할인시작' 버튼으로 트리거
     */
    @PostMapping("/start")
    public ApiResponse<Map<String, Object>> startSale() {
        saleStateService.startSale();
        return ApiResponse.success(
                Map.of("saleActive", true),
                "세일이 시작되었습니다!"
        );
    }

    /**
     * 세일 종료
     * POST /api/sale/end
     */
    @PostMapping("/end")
    public ApiResponse<Map<String, Object>> endSale() {
        saleStateService.endSale();
        return ApiResponse.success(
                Map.of("saleActive", false),
                "세일이 종료되었습니다."
        );
    }

    /**
     * 세일 상태 조회
     * GET /api/sale/status
     */
    @GetMapping("/status")
    public ApiResponse<Map<String, Object>> getSaleStatus() {
        boolean isActive = saleStateService.isSaleActive();
        return ApiResponse.success(Map.of("saleActive", isActive));
    }
}
