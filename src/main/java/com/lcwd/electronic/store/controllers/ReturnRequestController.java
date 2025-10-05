package com.lcwd.electronic.store.controllers;

import com.lcwd.electronic.store.dtos.ApiResponseMessage;
import com.lcwd.electronic.store.dtos.ReturnRequestDto;
import com.lcwd.electronic.store.dtos.PageableResponse;
import com.lcwd.electronic.store.services.ReturnRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/returns")
public class ReturnRequestController {

    @Autowired
    private ReturnRequestService returnService;

    @PostMapping
    public ResponseEntity<ReturnRequestDto> create(@Valid @RequestBody ReturnRequestDto returnRequestDto) {
        return new ResponseEntity<>(returnService.create(returnRequestDto), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{returnId}")
    public ResponseEntity<ReturnRequestDto> update(
            @Valid @RequestBody ReturnRequestDto returnRequestDto,
            @PathVariable Long returnId) {
        return new ResponseEntity<>(returnService.update(returnRequestDto, returnId), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{returnId}")
    public ResponseEntity<ApiResponseMessage> delete(@PathVariable Long returnId) {
        returnService.delete(returnId);
        return new ResponseEntity<>(
                ApiResponseMessage.builder()
                    .message("Return request deleted successfully")
                    .success(true)
                    .status(HttpStatus.OK)
                    .build(),
                HttpStatus.OK);
    }

    @GetMapping("/{returnId}")
    public ResponseEntity<ReturnRequestDto> get(@PathVariable Long returnId) {
        return new ResponseEntity<>(returnService.get(returnId), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<PageableResponse<ReturnRequestDto>> getAll(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "requestDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        return new ResponseEntity<>(
                returnService.getAll(pageNumber, pageSize, sortBy, sortDir),
                HttpStatus.OK);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PageableResponse<ReturnRequestDto>> getByOrder(
            @PathVariable String orderId,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        return new ResponseEntity<>(returnService.getByOrder(orderId, pageNumber, pageSize), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{returnId}/process")
    public ResponseEntity<ReturnRequestDto> processReturn(
            @PathVariable Long returnId,
            @RequestParam String status,
            @RequestParam(required = false) String notes,
            @RequestParam String processedByUserId) {
        return new ResponseEntity<>(
                returnService.processReturn(returnId, status, notes, processedByUserId),
                HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{returnId}/refund")
    public ResponseEntity<ReturnRequestDto> issueRefund(
            @PathVariable Long returnId,
            @RequestParam double refundAmount) {
        return new ResponseEntity<>(returnService.issueRefund(returnId, refundAmount), HttpStatus.OK);
    }
}