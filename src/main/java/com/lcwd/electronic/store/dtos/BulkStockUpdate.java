package com.lcwd.electronic.store.dtos;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkStockUpdate {
    private List<String> productIds;
    private List<Integer> quantities;
}