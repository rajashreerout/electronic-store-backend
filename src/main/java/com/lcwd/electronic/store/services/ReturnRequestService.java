package com.lcwd.electronic.store.services;

import com.lcwd.electronic.store.dtos.ReturnRequestDto;
import com.lcwd.electronic.store.dtos.PageableResponse;

public interface ReturnRequestService {
    ReturnRequestDto create(ReturnRequestDto returnRequestDto);
    ReturnRequestDto update(ReturnRequestDto returnRequestDto, Long returnId);
    void delete(Long returnId);
    ReturnRequestDto get(Long returnId);
    PageableResponse<ReturnRequestDto> getAll(int pageNumber, int pageSize, String sortBy, String sortDir);
    PageableResponse<ReturnRequestDto> getByOrder(String orderId, int pageNumber, int pageSize);
    PageableResponse<ReturnRequestDto> getByStatus(String status, int pageNumber, int pageSize);
    ReturnRequestDto processReturn(Long returnId, String status, String notes, String processedByUserId);
    ReturnRequestDto issueRefund(Long returnId, double refundAmount);
}