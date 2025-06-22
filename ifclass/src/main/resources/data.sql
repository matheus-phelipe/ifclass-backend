-- Script de inicialização para criar usuário admin
-- Este script é executado automaticamente pelo Spring Boot na inicialização

-- Inserir usuário admin (senha: hello)
-- Senha criptografada com BCrypt: $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM5lIaU0VPDmBdKYn9Gm
INSERT INTO usuario (id, nome, email, senha, prontuario) VALUES
(1, 'Administrador', 'admin@ifclass.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM5lIaU0VPDmBdKYn9Gm', 'ADM001')
ON CONFLICT (id) DO NOTHING;

-- Inserir permissão de admin
INSERT INTO usuario_authorities (usuario_id, authority) VALUES
(1, 'ROLE_ADMIN')
ON CONFLICT DO NOTHING;

-- Inserir usuário coordenador (senha: hello)
-- Senha criptografada com BCrypt: $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM5lIaU0VPDmBdKYn9Gm
INSERT INTO usuario (id, nome, email, senha, prontuario) VALUES
(2, 'Coordenador Teste', 'coordenador@ifclass.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM5lIaU0VPDmBdKYn9Gm', 'COORD001')
ON CONFLICT (id) DO NOTHING;

-- Inserir permissões de coordenador
INSERT INTO usuario_authorities (usuario_id, authority) VALUES
(2, 'ROLE_COORDENADOR'),
(2, 'ROLE_PROFESSOR')
ON CONFLICT DO NOTHING;

-- Inserir usuário professor (senha: hello)
-- Senha criptografada com BCrypt: $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM5lIaU0VPDmBdKYn9Gm
INSERT INTO usuario (id, nome, email, senha, prontuario) VALUES
(3, 'Professor Teste', 'professor@ifclass.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM5lIaU0VPDmBdKYn9Gm', 'PROF001')
ON CONFLICT (id) DO NOTHING;

-- Inserir permissão de professor
INSERT INTO usuario_authorities (usuario_id, authority) VALUES
(3, 'ROLE_PROFESSOR')
ON CONFLICT DO NOTHING;
