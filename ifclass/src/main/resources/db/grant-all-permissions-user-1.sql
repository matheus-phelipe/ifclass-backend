-- Script para dar todas as permissões ao usuário com ID 1
-- Execute este script no banco PostgreSQL

-- Primeiro, remover todas as autoridades existentes do usuário ID 1
DELETE FROM usuario_authorities WHERE usuario_id = 1;

-- Inserir todas as autoridades para o usuário ID 1
INSERT INTO usuario_authorities (usuario_id, authorities) VALUES 
(1, 'ROLE_ADMIN'),
(1, 'ROLE_PROFESSOR'),
(1, 'ROLE_ALUNO'),
(1, 'ROLE_COORDENADOR');

-- Verificar se as permissões foram inseridas corretamente
SELECT u.id, u.nome, u.email, ua.authorities 
FROM usuarios u 
LEFT JOIN usuario_authorities ua ON u.id = ua.usuario_id 
WHERE u.id = 1;

-- Comentário: Este script garante que o usuário ID 1 tenha acesso total ao sistema
-- incluindo funcionalidades de administração, coordenação, ensino e aprendizado
