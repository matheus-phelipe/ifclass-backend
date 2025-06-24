package com.ifclass.ifclass.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Base64;

@Component
public class JwtUtil {
    private static Key key;

    // Configuração segura da chave JWT via application.properties
    @Value("${jwt.secret:}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}") // 24 horas por padrão
    private long jwtExpiration;

    private Key getSigningKey() {
        if (key == null) {
            if (jwtSecret != null && !jwtSecret.isEmpty()) {
                // Usar chave do application.properties se disponível
                byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
                key = Keys.hmacShaKeyFor(keyBytes);
            } else {
                // Fallback para chave gerada (apenas para desenvolvimento)
                key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
                System.err.println("⚠️  AVISO DE SEGURANÇA: Usando chave JWT gerada automaticamente. Configure jwt.secret no application.properties para produção!");
            }
        }
        return key;
    }

    public String generateToken(Long id, String email, List<String> permissao) {
        return Jwts.builder()
                .setSubject(email)
                .claim("id", id)
                .claim("authorities", permissao)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    public String validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
