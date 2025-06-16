package com.ifclass.ifclass.util.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration // Indica que esta é uma classe de configuração do Spring
public class SecurityConfig {

    @Bean // Define este método como um produtor de um bean do Spring
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Retorna uma nova instância de BCryptPasswordEncoder
    }

    // Você pode ter outras configurações de segurança aqui, se houver
}
