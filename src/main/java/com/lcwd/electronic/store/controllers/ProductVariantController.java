package com.lcwd.electronic.store.controllers;

import com.lcwd.electronic.store.dtos.ProductVariantDto;
import com.lcwd.electronic.store.dtos.ApiResponseMessage;
import com.lcwd.electronic.store.services.ProductVariantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products/{productId}/variants")
public class ProductVariantController {

    @Autowired
    private ProductVariantService variantService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductVariantDto> createVariant(
            @PathVariable String productId,
            @RequestBody ProductVariantDto variantDto) {
        return new ResponseEntity<>(variantService.createVariant(productId, variantDto), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{variantId}")
    public ResponseEntity<ProductVariantDto> updateVariant(
            @PathVariable Long variantId,
            @RequestBody ProductVariantDto variantDto) {
        return new ResponseEntity<>(variantService.updateVariant(variantId, variantDto), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{variantId}")
    public ResponseEntity<ApiResponseMessage> deleteVariant(@PathVariable Long variantId) {
        variantService.deleteVariant(variantId);
        ApiResponseMessage response = ApiResponseMessage.builder()
            .message("Variant deleted successfully!!")
            .status(HttpStatus.OK)
            .success(true)
            .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ProductVariantDto>> getVariants(@PathVariable String productId) {
        return new ResponseEntity<>(variantService.getVariantsByProduct(productId), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{variantId}/stock")
    public ResponseEntity<ApiResponseMessage> updateStock(
            @PathVariable Long variantId,
            @RequestParam int stock) {
        variantService.updateStock(variantId, stock);
        ApiResponseMessage response = ApiResponseMessage.builder()
            .message("Stock updated successfully!!")
            .status(HttpStatus.OK)
            .success(true)
            .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}