package com.lcwd.electronic.store.dtos;

import lombok.*;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductReviewDto {
    private Long id;
    private String productId;
    private String userId;
    private int rating;
    private String review;  // changed from comment to match frontend
    private Date reviewDate;
    private UserDto user;
}