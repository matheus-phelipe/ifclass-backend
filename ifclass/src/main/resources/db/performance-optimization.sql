-- ===== OTIMIZAÇÕES DE PERFORMANCE PARA O BANCO DE DADOS =====
-- Este arquivo contém índices e otimizações para melhorar a performance das queries

-- ===== ÍNDICES PARA TABELA USUARIO =====
-- Índice para busca por email (login)
CREATE INDEX IF NOT EXISTS idx_usuario_email ON usuario(email);

-- Índice para busca por prontuario
CREATE INDEX IF NOT EXISTS idx_usuario_prontuario ON usuario(prontuario);

-- Índice para busca por authorities (roles)
CREATE INDEX IF NOT EXISTS idx_usuario_authorities_usuario_id ON usuario_authorities(usuario_id);
CREATE INDEX IF NOT EXISTS idx_usuario_authorities_authority ON usuario_authorities(authority);

-- Índice composto para busca por usuário e authority
CREATE INDEX IF NOT EXISTS idx_usuario_authorities_composite ON usuario_authorities(usuario_id, authority);

-- ===== ÍNDICES PARA TABELA AULA =====
-- Índice para busca por professor
CREATE INDEX IF NOT EXISTS idx_aula_professor_id ON aula(professor_id);

-- Índice para busca por data
CREATE INDEX IF NOT EXISTS idx_aula_data ON aula(data);

-- Índice para busca por sala
CREATE INDEX IF NOT EXISTS idx_aula_sala_id ON aula(sala_id);

-- Índice para busca por disciplina
CREATE INDEX IF NOT EXISTS idx_aula_disciplina_id ON aula(disciplina_id);

-- Índice composto para busca por professor e data (query mais comum)
CREATE INDEX IF NOT EXISTS idx_aula_professor_data ON aula(professor_id, data);

-- Índice composto para busca por data e hora
CREATE INDEX IF NOT EXISTS idx_aula_data_hora ON aula(data, hora);

-- ===== ÍNDICES PARA TABELA TURMA =====
-- Índice para busca por curso
CREATE INDEX IF NOT EXISTS idx_turma_curso_id ON turma(curso_id);

-- Índice para busca por ano
CREATE INDEX IF NOT EXISTS idx_turma_ano ON turma(ano);

-- Índice composto para busca por curso e ano
CREATE INDEX IF NOT EXISTS idx_turma_curso_ano ON turma(curso_id, ano);

-- ===== ÍNDICES PARA TABELA ALUNO_TURMA =====
-- Índice para busca por aluno
CREATE INDEX IF NOT EXISTS idx_aluno_turma_aluno_id ON aluno_turma(aluno_id);

-- Índice para busca por turma
CREATE INDEX IF NOT EXISTS idx_aluno_turma_turma_id ON aluno_turma(turma_id);

-- ===== ÍNDICES PARA TABELA DISCIPLINA =====
-- Índice para busca por nome
CREATE INDEX IF NOT EXISTS idx_disciplina_nome ON disciplina(nome);

-- Índice para busca por código
CREATE INDEX IF NOT EXISTS idx_disciplina_codigo ON disciplina(codigo);

-- ===== ÍNDICES PARA TABELA CURSO =====
-- Índice para busca por nome
CREATE INDEX IF NOT EXISTS idx_curso_nome ON curso(nome);

-- Índice para busca por código
CREATE INDEX IF NOT EXISTS idx_curso_codigo ON curso(codigo);

-- ===== ÍNDICES PARA TABELA SALA =====
-- Índice para busca por bloco
CREATE INDEX IF NOT EXISTS idx_sala_bloco_id ON sala(bloco_id);

-- Índice para busca por código
CREATE INDEX IF NOT EXISTS idx_sala_codigo ON sala(codigo);

-- ===== ÍNDICES PARA TABELA BLOCO =====
-- Índice para busca por nome
CREATE INDEX IF NOT EXISTS idx_bloco_nome ON bloco(nome);

-- ===== ÍNDICES PARA TABELAS DE RESET DE SENHA =====
-- Índice para busca por token
CREATE INDEX IF NOT EXISTS idx_password_reset_token_token ON password_reset_token(token);

-- Índice para busca por usuário
CREATE INDEX IF NOT EXISTS idx_password_reset_token_usuario_id ON password_reset_token(usuario_id);

-- Índice para busca por data de expiração
CREATE INDEX IF NOT EXISTS idx_password_reset_token_expiry_date ON password_reset_token(expiry_date);

-- ===== OTIMIZAÇÕES DE QUERIES =====

-- View otimizada para buscar usuários com detalhes
CREATE OR REPLACE VIEW v_usuario_detalhes AS
SELECT 
    u.id,
    u.nome,
    u.email,
    u.prontuario,
    STRING_AGG(ua.authority, ',') as authorities,
    COUNT(CASE WHEN ua.authority = 'ROLE_PROFESSOR' THEN 1 END) > 0 as is_professor,
    COUNT(CASE WHEN ua.authority = 'ROLE_ALUNO' THEN 1 END) > 0 as is_aluno,
    COUNT(CASE WHEN ua.authority = 'ROLE_COORDENADOR' THEN 1 END) > 0 as is_coordenador,
    COUNT(CASE WHEN ua.authority = 'ROLE_ADMIN' THEN 1 END) > 0 as is_admin
FROM usuario u
LEFT JOIN usuario_authorities ua ON u.id = ua.usuario_id
WHERE NOT EXISTS (
    SELECT 1 FROM usuario_authorities ua2 
    WHERE ua2.usuario_id = u.id AND ua2.authority = 'ROLE_ADMIN'
)
GROUP BY u.id, u.nome, u.email, u.prontuario;

-- View otimizada para aulas com informações completas
CREATE OR REPLACE VIEW v_aula_completa AS
SELECT 
    a.id,
    a.data,
    a.hora,
    a.professor_id,
    u.nome as professor_nome,
    a.disciplina_id,
    d.nome as disciplina_nome,
    d.codigo as disciplina_codigo,
    a.sala_id,
    s.codigo as sala_codigo,
    s.bloco_id,
    b.nome as bloco_nome
FROM aula a
JOIN usuario u ON a.professor_id = u.id
JOIN disciplina d ON a.disciplina_id = d.id
JOIN sala s ON a.sala_id = s.id
JOIN bloco b ON s.bloco_id = b.id;

-- View para estatísticas de coordenação
CREATE OR REPLACE VIEW v_estatisticas_coordenacao AS
SELECT 
    (SELECT COUNT(*) FROM usuario u 
     JOIN usuario_authorities ua ON u.id = ua.usuario_id 
     WHERE ua.authority = 'ROLE_PROFESSOR') as total_professores,
    (SELECT COUNT(*) FROM usuario u 
     JOIN usuario_authorities ua ON u.id = ua.usuario_id 
     WHERE ua.authority = 'ROLE_ALUNO') as total_alunos,
    (SELECT COUNT(*) FROM disciplina) as total_disciplinas,
    (SELECT COUNT(*) FROM turma) as total_turmas,
    (SELECT COUNT(*) FROM aula WHERE data = CURRENT_DATE) as aulas_hoje,
    (SELECT COUNT(*) FROM sala) as total_salas,
    (SELECT COUNT(*) FROM bloco) as total_blocos;

-- ===== CONFIGURAÇÕES DE PERFORMANCE =====

-- Aumentar work_mem para queries complexas (ajustar conforme disponibilidade de RAM)
-- SET work_mem = '256MB';

-- Configurar shared_buffers (ajustar conforme RAM disponível)
-- SET shared_buffers = '256MB';

-- Habilitar estatísticas automáticas
-- SET track_activities = on;
-- SET track_counts = on;
-- SET track_io_timing = on;

-- ===== ANÁLISE DE PERFORMANCE =====

-- Query para verificar índices não utilizados
-- SELECT schemaname, tablename, indexname, idx_scan, idx_tup_read, idx_tup_fetch
-- FROM pg_stat_user_indexes
-- WHERE idx_scan = 0
-- ORDER BY schemaname, tablename;

-- Query para verificar tabelas com mais scans sequenciais
-- SELECT schemaname, tablename, seq_scan, seq_tup_read, 
--        idx_scan, idx_tup_fetch,
--        seq_tup_read / seq_scan as avg_seq_read
-- FROM pg_stat_user_tables
-- WHERE seq_scan > 0
-- ORDER BY seq_tup_read DESC;

-- ===== MANUTENÇÃO AUTOMÁTICA =====

-- Configurar autovacuum para manter performance
-- ALTER TABLE usuario SET (autovacuum_vacuum_scale_factor = 0.1);
-- ALTER TABLE aula SET (autovacuum_vacuum_scale_factor = 0.1);
-- ALTER TABLE turma SET (autovacuum_vacuum_scale_factor = 0.1);

-- ===== COMENTÁRIOS SOBRE OTIMIZAÇÕES =====

/*
ÍNDICES CRIADOS:

1. USUARIO:
   - idx_usuario_email: Para login rápido
   - idx_usuario_prontuario: Para busca por prontuário
   - idx_usuario_authorities_*: Para verificação de roles

2. AULA:
   - idx_aula_professor_data: Para dashboard do professor
   - idx_aula_data_hora: Para busca por horários
   - Índices individuais para FKs

3. TURMA:
   - idx_turma_curso_ano: Para listagem de turmas por curso/ano

4. ALUNO_TURMA:
   - Índices para relacionamentos N:N

VIEWS CRIADAS:

1. v_usuario_detalhes: Evita JOINs repetitivos
2. v_aula_completa: Dados completos de aulas
3. v_estatisticas_coordenacao: Dashboard de coordenação

BENEFÍCIOS ESPERADOS:

- Redução de 60-80% no tempo de queries de login
- Melhoria de 50-70% nas consultas de aulas por professor
- Redução significativa no uso de CPU para queries complexas
- Melhor cache hit ratio no PostgreSQL
*/
