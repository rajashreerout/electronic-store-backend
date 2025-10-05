package com.lcwd.electronic.store.controllers;

import com.lcwd.electronic.store.dtos.ApiResponseMessage;
import com.lcwd.electronic.store.dtos.BulkStockUpdate;
import com.lcwd.electronic.store.dtos.InventoryDto;
import com.lcwd.electronic.store.dtos.PageableResponse;
import com.lcwd.electronic.store.services.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import javax.validation.Valid;

@RestController
@RequestMapping("/inventory")
@PreAuthorize("hasRole('ADMIN')")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<InventoryDto> create(@Valid @RequestBody InventoryDto inventoryDto) {
        return new ResponseEntity<>(inventoryService.create(inventoryDto), HttpStatus.CREATED);
    }

    @PutMapping("/{inventoryId}")
    public ResponseEntity<InventoryDto> update(
            @Valid @RequestBody InventoryDto inventoryDto,
            @PathVariable("inventoryId") Long inventoryId) {
        return new ResponseEntity<>(inventoryService.update(inventoryDto, inventoryId), HttpStatus.OK);
    }

    @DeleteMapping("/{inventoryId}")
    public ResponseEntity<ApiResponseMessage> delete(@PathVariable("inventoryId") Long inventoryId) {
        inventoryService.delete(inventoryId);
        return new ResponseEntity<>(
                ApiResponseMessage.builder()
                    .message("Inventory record deleted successfully")
                    .success(true)
                    .status(HttpStatus.OK)
                    .build(),
                HttpStatus.OK);
    }

    @GetMapping("/{inventoryId}")
    public ResponseEntity<InventoryDto> get(@PathVariable("inventoryId") Long inventoryId) {
        return new ResponseEntity<>(inventoryService.get(inventoryId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PageableResponse<InventoryDto>> getAll(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "lastUpdated") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        return new ResponseEntity<>(
                inventoryService.getAll(pageNumber, pageSize, sortBy, sortDir),
                HttpStatus.OK);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<InventoryDto> getByProduct(@PathVariable("productId") String productId) {
        return new ResponseEntity<>(inventoryService.getByProduct(productId), HttpStatus.OK);
    }

    @PatchMapping("/product/{productId}/stock")
    public ResponseEntity<InventoryDto> updateStock(
            @PathVariable("productId") String productId,
            @RequestParam int quantity) {
        return new ResponseEntity<>(inventoryService.updateStock(productId, quantity), HttpStatus.OK);
    }

    @GetMapping("/low-stock")
    public ResponseEntity<PageableResponse<InventoryDto>> getLowStock(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        return new ResponseEntity<>(inventoryService.getLowStock(pageNumber, pageSize), HttpStatus.OK);
    }

    @PostMapping("/bulk-update")
    public ResponseEntity<List<InventoryDto>> bulkUpdateStock(@RequestBody BulkStockUpdate request) {
        return new ResponseEntity<>(
            inventoryService.bulkUpdateStock(request.getProductIds(), request.getQuantities()), 
            HttpStatus.OK
        );
    }
}