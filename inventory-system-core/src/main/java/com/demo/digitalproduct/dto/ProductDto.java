package com.demo.digitalproduct.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
public class ProductDto implements Serializable {

    private UUID id;

    private String sku;

    private String description;

    private ProductDetailDto detail;
}
