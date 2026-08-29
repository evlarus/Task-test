package com.evlarus.ecomreturns.common.config;

import java.time.Duration;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

@Configuration
public class CacheConfig {

    // курсы НБРБ обновляются раз в день, поэтому кэшируем на 12 часов
    @Bean
    public RedisCacheManagerBuilderCustomizer exchangeRatesCacheCustomizer() {
        return builder -> builder.withCacheConfiguration("exchangeRates",
                RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(12)));
    }
}
