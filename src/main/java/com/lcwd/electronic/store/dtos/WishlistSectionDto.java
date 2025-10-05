package com.lcwd.electronic.store.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WishlistSectionDto {
    private Long id;
    private String name;
    private String userId;
    private String description;
}