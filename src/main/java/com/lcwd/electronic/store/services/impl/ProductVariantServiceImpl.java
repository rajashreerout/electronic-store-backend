package com.lcwd.electronic.store.services.impl;

import com.lcwd.electronic.store.dtos.ProductVariantDto;
import com.lcwd.electronic.store.entities.Product;
import com.lcwd.electronic.store.entities.ProductVariant;
import com.lcwd.electronic.store.exceptions.ResourceNotFoundException;
import com.lcwd.electronic.store.repositories.ProductRepository;
import com.lcwd.electronic.store.repositories.ProductVariantRepository;
import com.lcwd.electronic.store.services.ProductVariantService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductVariantServiceImpl implements ProductVariantService {

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper mapper;

    @Override
    public ProductVariantDto createVariant(String productId, ProductVariantDto variantDto) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        ProductVariant variant = mapper.map(variantDto, ProductVariant.class);
        variant.setProduct(product);
        
        ProductVariant savedVariant = productVariantRepository.save(variant);
        return mapper.map(savedVariant, ProductVariantDto.class);
    }

    @Override
    public ProductVariantDto updateVariant(Long variantId, ProductVariantDto variantDto) {
        ProductVariant variant = productVariantRepository.findById(variantId)
            .orElseThrow(() -> new ResourceNotFoundException("Product variant not found with id: " + variantId));
        
        variant.setSize(variantDto.getSize());
        variant.setColor(variantDto.getColor());
        variant.setStock(variantDto.getStock());
        variant.setPrice(variantDto.getPrice());

        ProductVariant updatedVariant = productVariantRepository.save(variant);
        return mapper.map(updatedVariant, ProductVariantDto.class);
    }

    @Override
    public void deleteVariant(Long variantId) {
        ProductVariant variant = productVariantRepository.findById(variantId)
            .orElseThrow(() -> new ResourceNotFoundException("Product variant not found with id: " + variantId));
        productVariantRepository.delete(variant);
    }

    @Override
    public List<ProductVariantDto> getVariantsByProduct(String productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        
        return product.getVariants().stream()
            .map(variant -> mapper.map(variant, ProductVariantDto.class))
            .collect(Collectors.toList());
    }

    @Override
    public void updateStock(Long variantId, int newStock) {
        ProductVariant variant = productVariantRepository.findById(variantId)
            .orElseThrow(() -> new ResourceNotFoundException("Product variant not found with id: " + variantId));
        
        variant.setStock(newStock);
        productVariantRepository.save(variant);
    }
}