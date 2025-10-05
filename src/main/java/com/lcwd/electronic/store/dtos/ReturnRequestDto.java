package com.lcwd.electronic.store.dtos;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnRequestDto {
    private Long id;
    
    @NotNull(message = "Order ID is required")
    private String orderId;
    
    @NotBlank(message = "Return reason is required")
    private String reason;
    
    private String status;
    private LocalDateTime requestDate;
    private LocalDateTime processedDate;
    private String processingNotes;
    private boolean refundIssued;
    private double refundAmount;
    private String processedByUserId;
}