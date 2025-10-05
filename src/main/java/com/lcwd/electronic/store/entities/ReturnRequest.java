package com.lcwd.electronic.store.entities;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "returns")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Order order;

    @Column(length = 1000)
    private String reason;
    private String status; // PENDING, APPROVED, REJECTED, REFUNDED
    private LocalDateTime requestDate;
    private LocalDateTime processedDate;
    private String processingNotes;
    private boolean refundIssued;
    private double refundAmount;
    
    @ManyToOne
    private User processedBy;
}