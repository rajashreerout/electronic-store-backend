package com.lcwd.electronic.store.services.impl;

import com.lcwd.electronic.store.dtos.InventoryDto;
import com.lcwd.electronic.store.dtos.PageableResponse;
import com.lcwd.electronic.store.entities.Inventory;
import com.lcwd.electronic.store.entities.Product;
import com.lcwd.electronic.store.exceptions.ResourceNotFoundException;
import com.lcwd.electronic.store.exceptions.BadApiRequestException;
import com.lcwd.electronic.store.repositories.InventoryRepository;
import com.lcwd.electronic.store.repositories.ProductRepository;
import com.lcwd.electronic.store.services.InventoryService;
import com.lcwd.electronic.store.services.NotificationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final NotificationService notificationService;
    private final InventoryNotificationService inventoryNotificationService;
    private final ModelMapper mapper;

    @Autowired
    public InventoryServiceImpl(
            InventoryRepository inventoryRepository,
            ProductRepository productRepository,
            NotificationService notificationService,
            InventoryNotificationService inventoryNotificationService,
            ModelMapper mapper) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.notificationService = notificationService;
        this.inventoryNotificationService = inventoryNotificationService;
        this.mapper = mapper;
    }

    private Inventory getOrCreateInventory(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
        
        return inventoryRepository.findByProduct(product)
                .orElseGet(() -> {
                    Inventory inventory = new Inventory();
                    inventory.setProduct(product);
                    inventory.setCurrentStock(product.getQuantity());
                    inventory.setLastUpdated(LocalDateTime.now());
                    return inventoryRepository.save(inventory);
                });
    }

    @Override
    public InventoryDto create(InventoryDto inventoryDto) {
        Product product = productRepository.findById(inventoryDto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (inventoryRepository.findByProduct(product).isPresent()) {
            throw new BadApiRequestException("Inventory already exists for this product");
        }

        Inventory inventory = mapper.map(inventoryDto, Inventory.class);
        inventory.setProduct(product);
        inventory.setLastUpdated(LocalDateTime.now());
        
        Inventory savedInventory = inventoryRepository.save(inventory);
        return mapper.map(savedInventory, InventoryDto.class);
    }

    @Override
    public InventoryDto update(InventoryDto inventoryDto, Long inventoryId) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));

        inventory.setCurrentStock(inventoryDto.getCurrentStock());
        inventory.setLastUpdated(LocalDateTime.now());

        Inventory updatedInventory = inventoryRepository.save(inventory);
        return mapper.map(updatedInventory, InventoryDto.class);
    }

    @Override
    public void delete(Long inventoryId) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));
        inventoryRepository.delete(inventory);
    }

    @Override
    public InventoryDto get(Long inventoryId) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));
        return mapper.map(inventory, InventoryDto.class);
    }

    @Override
    public InventoryDto getByProduct(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        Inventory inventory = inventoryRepository.findByProduct(product)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product"));
        
        return mapper.map(inventory, InventoryDto.class);
    }

    @Override
    public PageableResponse<InventoryDto> getAll(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("desc")) ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Inventory> page = inventoryRepository.findAll(pageable);
        
        return new PageableResponse<>(
            page.getContent().stream()
                .map(inventory -> mapper.map(inventory, InventoryDto.class))
                .collect(Collectors.toList()),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isLast()
        );
    }

    @Override
    @Transactional
    public InventoryDto updateStock(String productId, int quantity) {
        Inventory inventory = getOrCreateInventory(productId);
        int oldStock = inventory.getCurrentStock();
        inventory.setCurrentStock(quantity);
        inventory.setLastUpdated(LocalDateTime.now());
        Inventory updatedInventory = inventoryRepository.save(inventory);

        if (oldStock <= 0 && quantity > 0) {
            inventoryNotificationService.processNotifications(productId);
        }

        if (quantity <= 10) {
            notificationService.createNotification(
                "wetrsdfwetwfasfwdf", // admin role id from application.properties
                "Low Stock Alert",
                String.format("Product %s is running low on stock (Current: %d)", productId, quantity),
                "INVENTORY"
            );
        }

        return mapper.map(updatedInventory, InventoryDto.class);
    }

    @Override
    public PageableResponse<InventoryDto> getLowStock(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Inventory> page = inventoryRepository.findByCurrentStockLessThanEqual(10, pageable);
        
        return new PageableResponse<>(
            page.getContent().stream()
                .map(inventory -> mapper.map(inventory, InventoryDto.class))
                .collect(Collectors.toList()),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isLast()
        );
    }

    @Override
    public List<InventoryDto> bulkUpdateStock(List<String> productIds, List<Integer> quantities) {
        if (productIds.size() != quantities.size()) {
            throw new BadApiRequestException("Product IDs and quantities lists must have the same length");
        }

        List<InventoryDto> updatedInventories = new ArrayList<>();
        for (int i = 0; i < productIds.size(); i++) {
            updatedInventories.add(updateStock(productIds.get(i), quantities.get(i)));
        }
        return updatedInventories;
    }

    @Override
    public void checkAndUpdateStockAlerts() {
        List<Inventory> lowStockInventories = inventoryRepository.findByCurrentStockLessThanEqual(10);
        for (Inventory inventory : lowStockInventories) {
            notificationService.createNotification(
                "wetrsdfwetwfasfwdf", // admin role id from application.properties
                "Low Stock Alert",
                String.format("Product %s is running low on stock (Current: %d)", 
                    inventory.getProduct().getProductId(), 
                    inventory.getCurrentStock()),
                "INVENTORY"
            );
        }
    }
}