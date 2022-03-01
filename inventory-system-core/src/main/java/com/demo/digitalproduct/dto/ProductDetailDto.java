package com.demo.digitalproduct.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class ProductDetailDto {

    private double currentPrice;
    private double currentStock;

    private boolean visible;
}
