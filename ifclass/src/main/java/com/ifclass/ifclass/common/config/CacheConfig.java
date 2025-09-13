package com.ifclass.ifclass.common.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    @Primary
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
        // Configuração padrão para caches
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .maximumSize(1000)
                .recordStats()); // Habilita estatísticas de cache
        
        // Definir nomes de cache explicitamente para melhor controle
        cacheManager.setCacheNames(Arrays.asList(
            "usuarios",           // Cache de usuários (15 min)
            "cursos",            // Cache de cursos (30 min)
            "disciplinas",       // Cache de disciplinas (30 min)
            "turmas",            // Cache de turmas (20 min)
            "salas",             // Cache de salas (60 min - dados estáticos)
            "blocos",            // Cache de blocos (60 min - dados estáticos)
            "aulas",             // Cache de aulas (10 min)
            "estatisticas",      // Cache de estatísticas (5 min)
            "dashboard"          // Cache de dashboard (10 min)
        ));
        
        return cacheManager;
    }
    
    @Bean("longTermCacheManager")
    public CacheManager longTermCacheManager() {
        CaffeineCacheManager longTermCache = new CaffeineCacheManager();
        
        // Cache de longo prazo para dados que mudam raramente
        longTermCache.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(2, TimeUnit.HOURS)
                .maximumSize(500)
                .recordStats());
        
        longTermCache.setCacheNames(Arrays.asList(
            "static-data",       // Dados estáticos do sistema
            "system-config",     // Configurações do sistema
            "user-permissions"   // Permissões de usuário
        ));
        
        return longTermCache;
    }
    
    @Bean("shortTermCacheManager")
    public CacheManager shortTermCacheManager() {
        CaffeineCacheManager shortTermCache = new CaffeineCacheManager();
        
        // Cache de curto prazo para dados que mudam frequentemente
        shortTermCache.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(2, TimeUnit.MINUTES)
                .maximumSize(200)
                .recordStats());
        
        shortTermCache.setCacheNames(Arrays.asList(
            "real-time-data",    // Dados em tempo real
            "session-data",      // Dados de sessão
            "temp-calculations"  // Cálculos temporários
        ));
        
        return shortTermCache;
    }
}
