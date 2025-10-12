package com.ifclass.ifclass.common.config;

import com.ifclass.ifclass.common.config.interceptor.LoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoggingInterceptor loggingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Registra nosso interceptor para ser aplicado a TODAS as rotas da aplicação ("/**")
        registry.addInterceptor(loggingInterceptor).addPathPatterns("/**").excludePathPatterns(
                "/api/admin/sistema/logs/**", 
                "/actuator/**",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/error"
            );;
    }
}