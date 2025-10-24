package com.ifclass.ifclass.util.log;

import com.ifclass.ifclass.admin.dto.LogSistemaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class LogWebSocketService {

    // Ferramenta do Spring para enviar mensagens para tópicos WebSocket
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Envia um objeto de log para o tópico "/topic/logs".
     * @param log O DTO do log a ser enviado.
     */
    public void sendLog(LogSistemaDTO log) {
        // Converte o objeto de log para JSON e o envia para o tópico "/topic/logs".
        // Qualquer cliente (frontend) que estiver inscrito neste tópico receberá a mensagem.
        messagingTemplate.convertAndSend("/topic/logs", log);
    }
}