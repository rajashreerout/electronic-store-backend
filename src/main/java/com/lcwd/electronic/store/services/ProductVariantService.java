package com.lcwd.electronic.store.services;

import com.lcwd.electronic.store.dtos.ProductVariantDto;
import java.util.List;

public interface ProductVariantService {
    ProductVariantDto createVariant(String productId, ProductVariantDto variantDto);
    ProductVariantDto updateVariant(Long variantId, ProductVariantDto variantDto);
    void deleteVariant(Long variantId);
    List<ProductVariantDto> getVariantsByProduct(String productId);
    void updateStock(Long variantId, int newStock);
}