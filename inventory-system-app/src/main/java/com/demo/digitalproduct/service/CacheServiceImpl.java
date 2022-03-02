package com.demo.digitalproduct.service;

import com.demo.digitalproduct.config.AppConfig;
import com.demo.digitalproduct.entity.Product;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
public class CacheServiceImpl implements CacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final AppConfig appConfig;

    public CacheServiceImpl(RedisTemplate<String, Object> redisTemplate, AppConfig appConfig) {
        this.redisTemplate = redisTemplate;
        this.appConfig = appConfig;
    }

    @Override
    public Product getFromCache(String productId) {
        Product redisRecord = (Product) redisTemplate.opsForValue().get(productId);
        if(redisRecord == null) {
            log.info("Product with id = <{}> wasn't found in cache", productId);
            return null;
        }

        log.info("Product with id = <{}> retrieved from cache: <{}>",
                productId,
                StringUtils.normalizeSpace(redisRecord.toString()));
        return redisRecord;
    }

    @Override
    public void saveToCache(Product product) {
        String redisKey = product.getId().toString();
        log.info("Saving product with id = <{}> to cache", product.getId().toString());
        redisTemplate.opsForValue().set(redisKey, product, Duration.ofMinutes(appConfig.getCache().getDuration()));
    }
}
