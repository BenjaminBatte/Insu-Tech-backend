package com.insurance.policy.insutech.controller;

import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cache")
public class CacheController {

    private final CacheManager cacheManager;

    public CacheController(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @GetMapping("/stats")
    public Map<String, Map<String, Object>> getCacheStats() {
        Map<String, Map<String, Object>> stats = new HashMap<>();

        cacheManager.getCacheNames().forEach(cacheName -> {
            CaffeineCache caffeineCache = (CaffeineCache) cacheManager.getCache(cacheName);
            if (caffeineCache != null) {
                CacheStats cacheStats = caffeineCache.getNativeCache().stats();
                Map<String, Object> cacheStatsMap = new HashMap<>();
                cacheStatsMap.put("hitCount", cacheStats.hitCount());
                cacheStatsMap.put("missCount", cacheStats.missCount());
                cacheStatsMap.put("loadSuccessCount", cacheStats.loadSuccessCount());
                cacheStatsMap.put("loadFailureCount", cacheStats.loadFailureCount());
                cacheStatsMap.put("totalLoadTime", cacheStats.totalLoadTime());
                cacheStatsMap.put("evictionCount", cacheStats.evictionCount());

                stats.put(cacheName, cacheStatsMap);
            }
        });

        return stats;
    }
}