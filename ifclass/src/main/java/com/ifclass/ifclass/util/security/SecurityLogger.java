package com.ifclass.ifclass.util.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Logger especializado para eventos de segurança
 */
@Component
public class SecurityLogger {
    
    private static final Logger logger = LoggerFactory.getLogger("SECURITY");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Log de tentativa de login
     */
    public void logLoginAttempt(String email, String ip, boolean success) {
        String status = success ? "SUCCESS" : "FAILED";
        String message = String.format("[%s] LOGIN_%s - Email: %s, IP: %s", 
            LocalDateTime.now().format(formatter), status, email, ip);
        
        if (success) {
            logger.info(message);
        } else {
            logger.warn(message);
        }
    }
    
    /**
     * Log de logout
     */
    public void logLogout(String email, String ip) {
        String message = String.format("[%s] LOGOUT - Email: %s, IP: %s", 
            LocalDateTime.now().format(formatter), email, ip);
        logger.info(message);
    }
    
    /**
     * Log de tentativa de acesso não autorizado
     */
    public void logUnauthorizedAccess(String uri, String ip, String userAgent) {
        String message = String.format("[%s] UNAUTHORIZED_ACCESS - URI: %s, IP: %s, UserAgent: %s", 
            LocalDateTime.now().format(formatter), uri, ip, userAgent);
        logger.warn(message);
    }
    
    /**
     * Log de token JWT inválido
     */
    public void logInvalidToken(String ip, String token) {
        String tokenPreview = token != null && token.length() > 10 ? 
            token.substring(0, 10) + "..." : "null";
        String message = String.format("[%s] INVALID_TOKEN - IP: %s, Token: %s", 
            LocalDateTime.now().format(formatter), ip, tokenPreview);
        logger.warn(message);
    }
    
    /**
     * Log de tentativa de XSS
     */
    public void logXSSAttempt(String input, String ip, String uri) {
        String message = String.format("[%s] XSS_ATTEMPT - URI: %s, IP: %s, Input: %s", 
            LocalDateTime.now().format(formatter), uri, ip, 
            input.length() > 100 ? input.substring(0, 100) + "..." : input);
        logger.error(message);
    }
    
    /**
     * Log de tentativa de SQL Injection
     */
    public void logSQLInjectionAttempt(String input, String ip, String uri) {
        String message = String.format("[%s] SQL_INJECTION_ATTEMPT - URI: %s, IP: %s, Input: %s", 
            LocalDateTime.now().format(formatter), uri, ip, 
            input.length() > 100 ? input.substring(0, 100) + "..." : input);
        logger.error(message);
    }
    
    /**
     * Log de mudança de senha
     */
    public void logPasswordChange(String email, String ip) {
        String message = String.format("[%s] PASSWORD_CHANGE - Email: %s, IP: %s", 
            LocalDateTime.now().format(formatter), email, ip);
        logger.info(message);
    }
    
    /**
     * Log de criação de usuário
     */
    public void logUserCreation(String email, String createdBy, String ip) {
        String message = String.format("[%s] USER_CREATED - Email: %s, CreatedBy: %s, IP: %s", 
            LocalDateTime.now().format(formatter), email, createdBy, ip);
        logger.info(message);
    }
    
    /**
     * Log de erro de sistema
     */
    public void logSystemError(String operation, String error, String ip) {
        String message = String.format("[%s] SYSTEM_ERROR - Operation: %s, Error: %s, IP: %s", 
            LocalDateTime.now().format(formatter), operation, error, ip);
        logger.error(message);
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
