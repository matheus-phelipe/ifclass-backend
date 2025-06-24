package com.ifclass.ifclass.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PerformanceMonitoringService {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceMonitoringService.class);

    @Autowired(required = false)
    private CacheManager cacheManager;

    @Autowired
    private DataSource dataSource;

    // Contadores de performance
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong slowQueries = new AtomicLong(0);
    private final AtomicLong cacheHits = new AtomicLong(0);
    private final AtomicLong cacheMisses = new AtomicLong(0);
    private final Map<String, AtomicLong> endpointCounts = new HashMap<>();
    private final Map<String, AtomicLong> queryTimes = new HashMap<>();

    /**
     * Registra uma requisição HTTP
     */
    public void recordRequest(String endpoint, long responseTime) {
        totalRequests.incrementAndGet();
        
        endpointCounts.computeIfAbsent(endpoint, k -> new AtomicLong(0)).incrementAndGet();
        queryTimes.computeIfAbsent(endpoint, k -> new AtomicLong(0)).addAndGet(responseTime);
        
        // Considera query lenta se > 1000ms
        if (responseTime > 1000) {
            slowQueries.incrementAndGet();
            logger.warn("Slow request detected: {} took {}ms", endpoint, responseTime);
        }
    }

    /**
     * Registra hit de cache
     */
    public void recordCacheHit(String cacheName) {
        cacheHits.incrementAndGet();
        logger.debug("Cache HIT for: {}", cacheName);
    }

    /**
     * Registra miss de cache
     */
    public void recordCacheMiss(String cacheName) {
        cacheMisses.incrementAndGet();
        logger.debug("Cache MISS for: {}", cacheName);
    }

    /**
     * Obtém estatísticas de performance
     */
    public Map<String, Object> getPerformanceStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Estatísticas gerais
        stats.put("totalRequests", totalRequests.get());
        stats.put("slowQueries", slowQueries.get());
        stats.put("cacheHits", cacheHits.get());
        stats.put("cacheMisses", cacheMisses.get());
        
        // Taxa de hit do cache
        long totalCacheOperations = cacheHits.get() + cacheMisses.get();
        double cacheHitRate = totalCacheOperations > 0 ? 
            (double) cacheHits.get() / totalCacheOperations * 100 : 0;
        stats.put("cacheHitRate", String.format("%.2f%%", cacheHitRate));
        
        // Endpoints mais utilizados
        Map<String, Object> topEndpoints = new HashMap<>();
        endpointCounts.entrySet().stream()
            .sorted(Map.Entry.<String, AtomicLong>comparingByValue((a, b) -> 
                Long.compare(b.get(), a.get())))
            .limit(10)
            .forEach(entry -> topEndpoints.put(entry.getKey(), entry.getValue().get()));
        stats.put("topEndpoints", topEndpoints);
        
        // Tempos médios de resposta
        Map<String, Object> avgResponseTimes = new HashMap<>();
        queryTimes.forEach((endpoint, totalTime) -> {
            long count = endpointCounts.get(endpoint).get();
            if (count > 0) {
                avgResponseTimes.put(endpoint, totalTime.get() / count);
            }
        });
        stats.put("avgResponseTimes", avgResponseTimes);
        
        // Estatísticas do banco de dados
        stats.put("databaseStats", getDatabaseStats());
        
        // Estatísticas de memória
        stats.put("memoryStats", getMemoryStats());
        
        return stats;
    }

    /**
     * Obtém estatísticas do banco de dados
     */
    private Map<String, Object> getDatabaseStats() {
        Map<String, Object> dbStats = new HashMap<>();
        
        try (Connection conn = dataSource.getConnection()) {
            // Número de conexões ativas
            try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT count(*) as active_connections FROM pg_stat_activity WHERE state = 'active'")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    dbStats.put("activeConnections", rs.getInt("active_connections"));
                }
            }
            
            // Estatísticas de tabelas principais
            try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT schemaname, tablename, n_tup_ins, n_tup_upd, n_tup_del, seq_scan, idx_scan " +
                "FROM pg_stat_user_tables WHERE tablename IN ('usuario', 'aula', 'turma', 'disciplina')")) {
                ResultSet rs = stmt.executeQuery();
                Map<String, Object> tableStats = new HashMap<>();
                while (rs.next()) {
                    Map<String, Object> stats = new HashMap<>();
                    stats.put("inserts", rs.getLong("n_tup_ins"));
                    stats.put("updates", rs.getLong("n_tup_upd"));
                    stats.put("deletes", rs.getLong("n_tup_del"));
                    stats.put("seqScans", rs.getLong("seq_scan"));
                    stats.put("idxScans", rs.getLong("idx_scan"));
                    tableStats.put(rs.getString("tablename"), stats);
                }
                dbStats.put("tableStats", tableStats);
            }
            
        } catch (SQLException e) {
            logger.error("Error getting database stats", e);
            dbStats.put("error", "Unable to retrieve database statistics");
        }
        
        return dbStats;
    }

    /**
     * Obtém estatísticas de memória
     */
    private Map<String, Object> getMemoryStats() {
        Map<String, Object> memStats = new HashMap<>();
        
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        memStats.put("maxMemoryMB", maxMemory / 1024 / 1024);
        memStats.put("totalMemoryMB", totalMemory / 1024 / 1024);
        memStats.put("usedMemoryMB", usedMemory / 1024 / 1024);
        memStats.put("freeMemoryMB", freeMemory / 1024 / 1024);
        memStats.put("memoryUsagePercent", String.format("%.2f%%", 
            (double) usedMemory / maxMemory * 100));
        
        return memStats;
    }

    /**
     * Limpa estatísticas antigas (executado a cada hora)
     */
    @Scheduled(fixedRate = 3600000) // 1 hora
    public void cleanupOldStats() {
        logger.info("Cleaning up old performance statistics");
        
        // Reset contadores se ficaram muito grandes
        if (totalRequests.get() > 1000000) {
            totalRequests.set(0);
            slowQueries.set(0);
            cacheHits.set(0);
            cacheMisses.set(0);
            endpointCounts.clear();
            queryTimes.clear();
            logger.info("Performance counters reset due to high volume");
        }
    }

    /**
     * Log de estatísticas de performance (executado a cada 15 minutos)
     */
    @Scheduled(fixedRate = 900000) // 15 minutos
    public void logPerformanceStats() {
        Map<String, Object> stats = getPerformanceStats();
        
        logger.info("=== PERFORMANCE STATS ===");
        logger.info("Total Requests: {}", stats.get("totalRequests"));
        logger.info("Slow Queries: {}", stats.get("slowQueries"));
        logger.info("Cache Hit Rate: {}", stats.get("cacheHitRate"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> memStats = (Map<String, Object>) stats.get("memoryStats");
        logger.info("Memory Usage: {}MB / {}MB ({})", 
            memStats.get("usedMemoryMB"), 
            memStats.get("maxMemoryMB"),
            memStats.get("memoryUsagePercent"));
        
        logger.info("========================");
    }

    /**
     * Força limpeza de cache se a memória estiver alta
     */
    @Scheduled(fixedRate = 300000) // 5 minutos
    public void checkMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        double memoryUsagePercent = (double) usedMemory / maxMemory * 100;
        
        // Se uso de memória > 85%, limpa caches
        if (memoryUsagePercent > 85) {
            logger.warn("High memory usage detected ({}%), clearing caches",
                String.format("%.2f%%", memoryUsagePercent));

            if (cacheManager != null) {
                cacheManager.getCacheNames().forEach(cacheName -> {
                    var cache = cacheManager.getCache(cacheName);
                    if (cache != null) {
                        cache.clear();
                    }
                });
            }

            // Força garbage collection
            System.gc();
        }
    }

    /**
     * Obtém estatísticas específicas de cache
     */
    public Map<String, Object> getCacheStats() {
        Map<String, Object> cacheStats = new HashMap<>();

        if (cacheManager != null) {
            cacheManager.getCacheNames().forEach(cacheName -> {
                var cache = cacheManager.getCache(cacheName);
                if (cache != null) {
                    Map<String, Object> stats = new HashMap<>();
                    stats.put("name", cacheName);
                    // Adicionar mais estatísticas específicas do Caffeine se necessário
                    cacheStats.put(cacheName, stats);
                }
            });
        } else {
            cacheStats.put("error", "Cache manager not available");
        }

        return cacheStats;
    }
}
