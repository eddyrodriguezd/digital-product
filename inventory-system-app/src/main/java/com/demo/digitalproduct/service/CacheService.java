package com.demo.digitalproduct.service;

import com.demo.digitalproduct.entity.Product;

public interface CacheService {

    Product getFromCache(String productId);

    void saveToCache(Product product);
}
