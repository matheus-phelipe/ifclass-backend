package com.ifclass.ifclass.util.security;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Utilitário para validação e sanitização de entrada
 */
@Component
public class InputValidator {
    
    // Padrões de validação
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    private static final Pattern PRONTUARIO_PATTERN = Pattern.compile(
        "^[A-Z0-9]{3,10}$"
    );
    
    private static final Pattern NOME_PATTERN = Pattern.compile(
        "^[a-zA-ZÀ-ÿ\\s.'-]{2,100}$"
    );
    
    // Caracteres perigosos para XSS
    private static final Pattern XSS_PATTERN = Pattern.compile(
        ".*[<>\"'&].*", Pattern.CASE_INSENSITIVE
    );
    
    // SQL Injection patterns básicos
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        ".*(union|select|insert|update|delete|drop|create|alter|exec|execute).*", 
        Pattern.CASE_INSENSITIVE
    );
    
    /**
     * Valida formato de email
     */
    public boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Valida formato de prontuário
     */
    public boolean isValidProntuario(String prontuario) {
        return prontuario != null && PRONTUARIO_PATTERN.matcher(prontuario).matches();
    }
    
    /**
     * Valida formato de nome
     */
    public boolean isValidNome(String nome) {
        return nome != null && NOME_PATTERN.matcher(nome).matches();
    }
    
    /**
     * Verifica se a string contém caracteres perigosos para XSS
     */
    public boolean containsXSS(String input) {
        return input != null && XSS_PATTERN.matcher(input).find();
    }
    
    /**
     * Verifica se a string contém padrões de SQL Injection
     */
    public boolean containsSQLInjection(String input) {
        return input != null && SQL_INJECTION_PATTERN.matcher(input).find();
    }
    
    /**
     * Sanitiza string removendo caracteres perigosos
     */
    public String sanitize(String input) {
        if (input == null) return null;
        
        return input
            .replaceAll("[<>\"'&]", "") // Remove caracteres XSS básicos
            .trim()
            .substring(0, Math.min(input.length(), 255)); // Limita tamanho
    }
    
    /**
     * Valida se a entrada é segura (sem XSS nem SQL Injection)
     */
    public boolean isSafeInput(String input) {
        return !containsXSS(input) && !containsSQLInjection(input);
    }
    
    /**
     * Valida senha forte
     */
    public boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(ch -> "!@#$%^&*()_+-=[]{}|;:,.<>?".indexOf(ch) >= 0);
        
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
}
