package com.lcwd.electronic.store.services.impl;

import com.lcwd.electronic.store.dtos.InventoryDto;
import com.lcwd.electronic.store.dtos.PageableResponse;
import com.lcwd.electronic.store.dtos.ProductDto;
import com.lcwd.electronic.store.entities.Category;
import com.lcwd.electronic.store.entities.Product;
import com.lcwd.electronic.store.exceptions.BadApiRequestException;
import com.lcwd.electronic.store.exceptions.ResourceNotFoundException;
import com.lcwd.electronic.store.helper.Helper;
import com.lcwd.electronic.store.repositories.CategoryRepository;
import com.lcwd.electronic.store.repositories.ProductRepository;
import com.lcwd.electronic.store.services.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    private static final String PRODUCT_NOT_FOUND = "Product not found of given Id !!";

    private final ProductRepository productRepository;
    private final ModelMapper mapper;
    private final CategoryRepository categoryRepository;
    private final StockNotificationService stockNotificationService;
    private final NotificationService notificationService;
    private final InventoryService inventoryService;
    private final InventoryInitializationService inventoryInitializationService;

    public ProductServiceImpl(
            ProductRepository productRepository,
            ModelMapper mapper,
            CategoryRepository categoryRepository,
            StockNotificationService stockNotificationService,
            NotificationService notificationService,
            InventoryService inventoryService,
            InventoryInitializationService inventoryInitializationService) {
        this.productRepository = productRepository;
        this.mapper = mapper;
        this.categoryRepository = categoryRepository;
        this.stockNotificationService = stockNotificationService;
        this.notificationService = notificationService;
        this.inventoryService = inventoryService;
        this.inventoryInitializationService = inventoryInitializationService;
    }

    private String generateSku(String productTitle) {
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(8);
        String titleCode = productTitle.replaceAll("[^A-Z]", "")
                .substring(0, Math.min(productTitle.length(), 3))
                .toUpperCase();
        return titleCode + "-" + timestamp;
    }

    @Override
    public ProductDto create(ProductDto productDto) {
        InventoryDto inventoryDto = InventoryDto.builder()
                .productId(productDto.getProductId())
                .currentStock(productDto.getQuantity())
                .lowStockThreshold(10)
                .reorderPoint(20)
                .sku(generateSku(productDto.getTitle()))
                .stockAlert(false)
                .build();

        try {
            inventoryService.create(inventoryDto);
        } catch (Exception e) {
            throw new BadApiRequestException("Failed to initialize inventory for product");
        }

        if (productDto.getQuantity() < 0) {
            throw new BadApiRequestException("Product quantity cannot be negative");
        }

        Product product = mapper.map(productDto, Product.class);
        String productId = UUID.randomUUID().toString();
        product.setProductId(productId);
        product.setAddedDate(new Date());
        product.setStock(productDto.getQuantity() > 0);
        Product savedProduct = productRepository.save(product);
        inventoryInitializationService.initializeInventoryForNewProduct(savedProduct);
        return mapper.map(savedProduct, ProductDto.class);
    }

    @Override
    public ProductDto update(ProductDto productDto, String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND));
        
        product.setTitle(Helper.sanitizeHtml(productDto.getTitle()));
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setDiscountedPrice(productDto.getDiscountedPrice());
        product.setQuantity(productDto.getQuantity());
        product.setLive(productDto.isLive());
        product.setStock(productDto.isStock());
        product.setProductImageName(productDto.getProductImageName());

        Product updatedProduct = productRepository.save(product);

        if (product.isStock() && product.getQuantity() > 0) {
            stockNotificationService.processStockNotifications(productId);
        }

        if (product.isStock() && product.getQuantity() <= 10) {
            notificationService.createNotification(
                    "admin",
                    "Low Stock Alert",
                    "Product '" + product.getTitle() + "' is running low on stock (" + product.getQuantity() + " remaining)",
                    "STOCK_LOW"
            );
        }

        return mapper.map(updatedProduct, ProductDto.class);
    }

    @Override
    public void delete(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND));
        productRepository.delete(product);
    }

    @Override
    public ProductDto get(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND));
        return mapper.map(product, ProductDto.class);
    }

    @Override
    public PageableResponse<ProductDto> getAll(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("desc")) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> page = productRepository.findAll(pageable);
        return Helper.getPageableResponse(page, ProductDto.class);
    }

    @Override
    public PageableResponse<ProductDto> getAllLive(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("desc")) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> page = productRepository.findByLiveTrue(pageable);
        return Helper.getPageableResponse(page, ProductDto.class);
    }

    @Override
    public PageableResponse<ProductDto> searchByTitle(String subTitle, int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("desc")) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> page = productRepository.findByTitleContaining(subTitle, pageable);
        return Helper.getPageableResponse(page, ProductDto.class);
    }

    @Override
    public ProductDto createWithCategory(ProductDto productDto, String categoryId) {
        Category category = categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found !!"));
        
        Product product = mapper.map(productDto, Product.class);
        String productId = UUID.randomUUID().toString();
        product.setProductId(productId);
        product.setAddedDate(new Date());
        product.setCategory(category);
        Product savedProduct = productRepository.save(product);
        return mapper.map(savedProduct, ProductDto.class);
    }

    @Override
    public ProductDto updateCategory(String productId, String categoryId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND));
        Category category = categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found !!"));
        
        product.setCategory(category);
        Product savedProduct = productRepository.save(product);
        return mapper.map(savedProduct, ProductDto.class);
    }

    @Override
    public PageableResponse<ProductDto> getAllOfCategory(String categoryId, int pageNumber, int pageSize, String sortBy, String sortDir) {
        Category category = categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found !!"));
        
        Sort sort = (sortDir.equalsIgnoreCase("desc")) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> page = productRepository.findByCategory(category, pageable);
        return Helper.getPageableResponse(page, ProductDto.class);
    }

    @Override
    public PageableResponse<ProductDto> getAllLiveProductsOfCategory(String categoryId, int pageNumber, int pageSize, String sortBy, String sortDir) {
        Category category = categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found !!"));
        
        Sort sort = (sortDir.equalsIgnoreCase("desc")) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> page = productRepository.findByCategoryAndLiveTrue(category, pageable);
        return Helper.getPageableResponse(page, ProductDto.class);
    }
}
