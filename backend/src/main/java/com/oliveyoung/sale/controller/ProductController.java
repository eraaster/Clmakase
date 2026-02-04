package com.oliveyoung.sale.controller;

import com.oliveyoung.sale.dto.ApiResponse;
import com.oliveyoung.sale.dto.ProductResponse;
import com.oliveyoung.sale.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // 개발용, 프로덕션에서는 제한 필요
public class ProductController {

    private final ProductService productService;

    /**
     * 상품 목록 조회
     * GET /api/products
     */
    @GetMapping
    public ApiResponse<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ApiResponse.success(products);
    }

    /**
     * 상품 상세 조회
     * GET /api/products/{id}
     */
    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getProduct(@PathVariable Long id) {
        ProductResponse product = productService.getProduct(id);
        return ApiResponse.success(product);
    }
}
