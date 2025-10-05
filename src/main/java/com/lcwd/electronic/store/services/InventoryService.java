package com.lcwd.electronic.store.services;

import com.lcwd.electronic.store.dtos.InventoryDto;
import com.lcwd.electronic.store.dtos.PageableResponse;
import java.util.List;

public interface InventoryService {
    InventoryDto create(InventoryDto inventoryDto);
    InventoryDto update(InventoryDto inventoryDto, Long inventoryId);
    void delete(Long inventoryId);
    InventoryDto get(Long inventoryId);
    InventoryDto getByProduct(String productId);
    PageableResponse<InventoryDto> getAll(int pageNumber, int pageSize, String sortBy, String sortDir);
    PageableResponse<InventoryDto> getLowStock(int pageNumber, int pageSize);
    InventoryDto updateStock(String productId, int quantity);
    void checkAndUpdateStockAlerts();
    List<InventoryDto> bulkUpdateStock(List<String> productIds, List<Integer> quantities);
}