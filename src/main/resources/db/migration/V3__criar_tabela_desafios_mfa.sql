CREATE TABLE desafios_mfa (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL,
    codigo_hash VARCHAR(255) NOT NULL,
    criado_em TIMESTAMP NOT NULL,
    expira_em TIMESTAMP NOT NULL,
    tentativas INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL,

    CONSTRAINT fk_desafios_mfa_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id)
        ON DELETE RESTRICT);