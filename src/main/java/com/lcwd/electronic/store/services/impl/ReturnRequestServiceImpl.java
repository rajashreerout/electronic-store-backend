package com.lcwd.electronic.store.services.impl;

import com.lcwd.electronic.store.dtos.ReturnRequestDto;
import com.lcwd.electronic.store.dtos.PageableResponse;
import com.lcwd.electronic.store.entities.Order;
import com.lcwd.electronic.store.entities.ReturnRequest;
import com.lcwd.electronic.store.entities.User;
import com.lcwd.electronic.store.exceptions.ResourceNotFoundException;
import com.lcwd.electronic.store.helper.Helper;
import com.lcwd.electronic.store.repositories.OrderRepository;
import com.lcwd.electronic.store.repositories.ReturnRequestRepository;
import com.lcwd.electronic.store.repositories.UserRepository;
import com.lcwd.electronic.store.services.ReturnRequestService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReturnRequestServiceImpl implements ReturnRequestService {

    @Autowired
    private ReturnRequestRepository returnRequestRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper mapper;

    @Override
    public ReturnRequestDto create(ReturnRequestDto returnRequestDto) {
        Order order = orderRepository.findById(returnRequestDto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + returnRequestDto.getOrderId()));

        ReturnRequest returnRequest = mapper.map(returnRequestDto, ReturnRequest.class);
        returnRequest.setOrder(order);
        returnRequest.setRequestDate(LocalDateTime.now());
        returnRequest.setStatus("PENDING");
        
        ReturnRequest savedReturn = returnRequestRepository.save(returnRequest);
        return mapper.map(savedReturn, ReturnRequestDto.class);
    }

    @Override
    public ReturnRequestDto update(ReturnRequestDto returnRequestDto, Long returnId) {
        ReturnRequest returnRequest = returnRequestRepository.findById(returnId)
                .orElseThrow(() -> new ResourceNotFoundException("Return request not found with ID: " + returnId));

        returnRequest.setReason(returnRequestDto.getReason());
        // Only update status if it's provided and the request is not already processed
        if (returnRequestDto.getStatus() != null && !returnRequest.getStatus().equals("REFUNDED")) {
            returnRequest.setStatus(returnRequestDto.getStatus());
        }
        
        ReturnRequest updatedReturn = returnRequestRepository.save(returnRequest);
        return mapper.map(updatedReturn, ReturnRequestDto.class);
    }

    @Override
    public void delete(Long returnId) {
        ReturnRequest returnRequest = returnRequestRepository.findById(returnId)
                .orElseThrow(() -> new ResourceNotFoundException("Return request not found with ID: " + returnId));
        returnRequestRepository.delete(returnRequest);
    }

    @Override
    public ReturnRequestDto get(Long returnId) {
        ReturnRequest returnRequest = returnRequestRepository.findById(returnId)
                .orElseThrow(() -> new ResourceNotFoundException("Return request not found with ID: " + returnId));
        return mapper.map(returnRequest, ReturnRequestDto.class);
    }

    @Override
    public PageableResponse<ReturnRequestDto> getAll(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("desc")) ? 
                Sort.by(sortBy).descending() : 
                Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<ReturnRequest> page = returnRequestRepository.findAll(pageable);
        return Helper.getPageableResponse(page, ReturnRequestDto.class);
    }

    @Override
    public PageableResponse<ReturnRequestDto> getByOrder(String orderId, int pageNumber, int pageSize) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<ReturnRequest> page = returnRequestRepository.findByOrder(order, pageable);
        return Helper.getPageableResponse(page, ReturnRequestDto.class);
    }

    @Override
    public PageableResponse<ReturnRequestDto> getByStatus(String status, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<ReturnRequest> page = returnRequestRepository.findByStatus(status, pageable);
        return Helper.getPageableResponse(page, ReturnRequestDto.class);
    }

    @Override
    public ReturnRequestDto processReturn(Long returnId, String status, String notes, String processedByUserId) {
        ReturnRequest returnRequest = returnRequestRepository.findById(returnId)
                .orElseThrow(() -> new ResourceNotFoundException("Return request not found with ID: " + returnId));
        
        User processedBy = userRepository.findById(processedByUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + processedByUserId));

        returnRequest.setStatus(status);
        returnRequest.setProcessingNotes(notes);
        returnRequest.setProcessedBy(processedBy);
        returnRequest.setProcessedDate(LocalDateTime.now());
        
        ReturnRequest processedReturn = returnRequestRepository.save(returnRequest);
        return mapper.map(processedReturn, ReturnRequestDto.class);
    }

    @Override
    public ReturnRequestDto issueRefund(Long returnId, double refundAmount) {
        ReturnRequest returnRequest = returnRequestRepository.findById(returnId)
                .orElseThrow(() -> new ResourceNotFoundException("Return request not found with ID: " + returnId));

        returnRequest.setRefundAmount(refundAmount);
        returnRequest.setRefundIssued(true);
        returnRequest.setStatus("REFUNDED");
        returnRequest.setProcessedDate(LocalDateTime.now());
        
        ReturnRequest refundedReturn = returnRequestRepository.save(returnRequest);
        return mapper.map(refundedReturn, ReturnRequestDto.class);
    }
}