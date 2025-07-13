-- Tabela de logs (caso queira usar no futuro)
CREATE TABLE IF NOT EXISTS logs (
    id BINARY(16) PRIMARY KEY,
    servico VARCHAR(100) NOT NULL,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('SUCESSO', 'ERRO')),
    mensagem TEXT NOT NULL,
    data_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de pedidos
CREATE TABLE IF NOT EXISTS pedidos (
    id BINARY(16) PRIMARY KEY,
    cliente_nome VARCHAR(255) NOT NULL,
    cliente_email VARCHAR(255) NOT NULL,
    cliente_cpf VARCHAR(14) NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    descricao TEXT,
    status VARCHAR(50) DEFAULT 'CRIADO',
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices
CREATE INDEX idx_logs_servico ON logs(servico);
CREATE INDEX idx_logs_tipo ON logs(tipo);
CREATE INDEX idx_logs_data_hora ON logs(data_hora);

CREATE INDEX idx_pedidos_status ON pedidos(status);
CREATE INDEX idx_pedidos_data_criacao ON pedidos(data_criacao);
