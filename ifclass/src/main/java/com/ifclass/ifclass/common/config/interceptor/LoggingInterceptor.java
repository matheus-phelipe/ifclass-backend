package com.ifclass.ifclass.common.config.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.ifclass.ifclass.admin.dto.LogSistemaDTO;
import com.ifclass.ifclass.util.log.LogWebSocketService;

@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Autowired
    private LogWebSocketService logWebSocketService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Este método é executado ANTES da requisição chegar ao controller
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);

        // Tenta pegar o IP real, mesmo se houver um proxy (load balancer) na frente
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        String logMessage = String.format("HTTP_REQUEST | Method: %s | URI: %s | IP: %s", 
                                      request.getMethod(), request.getRequestURI(), ipAddress);
        logger.info(logMessage);

        // Cria o DTO e envia via WebSocket
        LogSistemaDTO logDTO = new LogSistemaDTO(
            null, // ID não é necessário para o WebSocket
            LocalDateTime.now(),
            "INFO",
            "Network", // Categoria para logs de requisição
            logMessage,
            "system", // Usuário (poderia ser extraído do token de segurança)
            ipAddress,
            "" // Detalhes
        );
        logWebSocketService.sendLog(logDTO);

        return true; 
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        
    long startTime = (Long) request.getAttribute("startTime");
    long duration = System.currentTimeMillis() - startTime;
    int status = response.getStatus();

    String logLevel = "INFO"; 
    String logCategory = "Network";

    if (status >= 400 || ex != null) {
        logLevel = "ERROR";
        logCategory = "Application"; 
    }
    

    String logMessage = String.format("HTTP_RESPONSE | Status: %s | Duration: %dms | URI: %s",
    status, duration, request.getRequestURI());

    if ("ERROR".equals(logLevel)) {
        logger.error(logMessage + (ex != null ? " | Exception: " + ex.getMessage() : ""));
    } else {
        logger.info(logMessage);
    }

    LogSistemaDTO logDTO = new LogSistemaDTO(
        null,
        LocalDateTime.now(),
        logLevel,
        logCategory,
        logMessage,
        "system",
        request.getRemoteAddr(),
        ex != null ? ex.getMessage() : ""
    );

    logWebSocketService.sendLog(logDTO);    

    if (ex != null) {
        String errorLogMessage = String.format("HTTP_ERROR | URI: %s | Error: %s", 
                                               request.getRequestURI(), ex.getMessage());
        logger.error(errorLogMessage);

        LogSistemaDTO errorLogDTO = new LogSistemaDTO(
            null, LocalDateTime.now(), "ERROR", "Application",
            errorLogMessage, "system", request.getRemoteAddr(), ""
        );
        logWebSocketService.sendLog(errorLogDTO);
    }
    }
}