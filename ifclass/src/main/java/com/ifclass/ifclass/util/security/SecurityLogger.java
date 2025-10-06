package com.ifclass.ifclass.util.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Logger especializado para eventos de segurança
 */
@Component
public class SecurityLogger {
    
    private static final Logger logger = LoggerFactory.getLogger("SECURITY");
    
    /**
     * Log de tentativa de login
     */
    public void logLoginAttempt(String email, String ip, boolean success) {
        if (success) {
            logger.info("LOGIN_SUCCESS | Email: {} | IP: {}", email, ip);
        } else {
            logger.warn("LOGIN_FAILED | Email: {} | IP: {}", email, ip);
        }
    }
    
    /**
     * Log de logout
     */
    public void logLogout(String email, String ip) {
        logger.info("LOGOUT | Email: {} | IP: {}", email, ip);
    }
    
    /**
     * Log de tentativa de acesso não autorizado
     */
    public void logUnauthorizedAccess(String uri, String ip, String userAgent) {
        logger.warn("UNAUTHORIZED_ACCESS | URI: {} | IP: {} | User-Agent: {}", uri, ip, userAgent);
    }
    
    /**
     * Log de token JWT inválido
     */
    public void logInvalidToken(String ip, String token) {
        String tokenPreview = token != null && token.length() > 10 ? 
            token.substring(0, 10) + "..." : "null";
        logger.warn("INVALID_TOKEN | IP: {} | Token: {}", ip, tokenPreview);
    }
    
    /**
     * Log de tentativa de XSS
     */
    public void logXSSAttempt(String input, String ip, String uri) {
        String safeInput = input.length() > 100 ? input.substring(0, 100) + "..." : input;
        logger.error("XSS_ATTEMPT | URI: {} | IP: {} | Input: {}", uri, ip, safeInput);
    }
    
    /**
     * Log de tentativa de SQL Injection
     */
    public void logSQLInjectionAttempt(String input, String ip, String uri) {
        String safeInput = input.length() > 100 ? input.substring(0, 100) + "..." : input;
        logger.error("SQL_INJECTION_ATTEMPT | URI: {} | IP: {} | Input: {}", uri, ip, safeInput);
    }
    
    /**
     * Log de mudança de senha
     */
    public void logPasswordChange(String email, String ip) {
        logger.info("PASSWORD_CHANGE | Email: {} | IP: {}", email, ip);
    }
    
    /**
     * Log de criação de usuário
     */
    public void logUserCreation(String email, String createdBy, String ip) {
        logger.info("USER_CREATED | Email: {} | CreatedBy: {} | IP: {}", email, createdBy, ip);
    }
    
    /**
     * Log de erro de sistema
     */
    public void logSystemError(String operation, String error, String ip) {
        logger.error("SYSTEM_ERROR | Operation: {} | Error: {} | IP: {}", operation, error, ip);
    }
    
    /**
     * Extrai IP do request
     */
    public String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }
        
        return request.getRemoteAddr();
    }
}
