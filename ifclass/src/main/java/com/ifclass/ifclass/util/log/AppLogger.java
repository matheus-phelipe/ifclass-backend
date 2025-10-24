package com.ifclass.ifclass.util.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AppLogger {

    private static final Logger logger = LoggerFactory.getLogger(AppLogger.class);

    // Registra uma operação de CRUD bem-sucedida (Criação, Leitura, Atualização, Deleção).
    public void logCrudSuccess(String entity, String operation, String details) {
        logger.info("CRUD_SUCCESS | Entidade: {} | Operação: {} | Detalhes: {}", entity, operation, details);
    }

    // Registra um aviso ou falha esperada em uma operação de CRUD.
    public void logCrudWarning(String entity, String operation, String reason) {
        logger.warn("CRUD_WARNING | Entidade: {} | Operação: {} | Motivo: {}", entity, operation, reason);
    }

    // Registra um erro inesperado em um serviço da aplicação.
    public void logServiceError(String serviceName, String methodName, String errorMessage) {
        logger.error("SERVICE_ERROR | Serviço: {} | Método: {} | Erro: {}", serviceName, methodName, errorMessage);
    }
    
    // Versão do log de erro que inclui a exceção completa para depuração.
    public void logServiceError(String serviceName, String methodName, String errorMessage, Exception exception) {
        logger.error("SERVICE_ERROR | Serviço: {} | Método: {} | Erro: {}", serviceName, methodName, errorMessage, exception);
    }
}