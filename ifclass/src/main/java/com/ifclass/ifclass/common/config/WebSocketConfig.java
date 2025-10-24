package com.ifclass.ifclass.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Define um "broker" (agente de mensagens) para os tópicos.
        // O frontend vai se inscrever em "tópicos" que começam com "/topic".
        registry.enableSimpleBroker("/topic");

        // Define o prefixo para mensagens que vêm do cliente para o servidor ("app").
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Registra o endpoint "/ws-logs" que o frontend usará para se conectar.
        // .setAllowedOriginPatterns("*") permite a conexão de qualquer origem (útil para desenvolvimento).
        // .withSockJS() é usado como um fallback para navegadores que não suportam WebSockets nativamente.
        registry.addEndpoint("/ws-logs").setAllowedOriginPatterns("*").withSockJS();
    }
}