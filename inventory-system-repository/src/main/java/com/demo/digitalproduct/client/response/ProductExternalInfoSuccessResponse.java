package com.demo.digitalproduct.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductExternalInfoSuccessResponse {

    private String id;
    private double price;
    private double stock;
}
