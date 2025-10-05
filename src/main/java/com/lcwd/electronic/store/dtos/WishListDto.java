package com.lcwd.electronic.store.dtos;

import lombok.*;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WishListDto {
    private Long id;
    private String productId;
    private String userId;
    private Long sectionId;
    private String sectionName;
    private Date addedDate;
}