package com.ifclass.ifclass.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.ifclass.ifclass.util.security.SecurityLogger;

import java.io.IOException;

@Component
@Order(2) // Executa após SecurityHeadersFilter
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private SecurityLogger securityLogger;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        // Cabeçalhos de CORS obrigatórios para respostas
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        // Libera requisição preflight (OPTIONS) do navegador
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String header = request.getHeader("Authorization");

        String clientIP = securityLogger.getClientIP(request);

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.replace("Bearer ", "");
            try {
                String email = jwtUtil.validateToken(token);
                // Token válido - continua processamento
            } catch (Exception e) {
                // Log de token inválido
                securityLogger.logInvalidToken(clientIP, token);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } else {
            // Libera login ou cadastro, bloqueia o resto
            if (!request.getRequestURI().contains("/auth") &&
                    !request.getRequestURI().equals("/api/usuarios") &&
                    !request.getRequestURI().contains("/usuarios/login") &&
                    !request.getRequestURI().contains("/usuarios/request-password-reset") &&
                    !request.getRequestURI().contains("/usuarios/reset-password") &&
                    !request.getRequestURI().contains("/relatorios") ) {

                // Log de tentativa de acesso não autorizado
                securityLogger.logUnauthorizedAccess(
                    request.getRequestURI(),
                    clientIP,
                    request.getHeader("User-Agent")
                );
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
