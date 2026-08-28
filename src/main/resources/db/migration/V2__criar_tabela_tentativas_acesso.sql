CREATE TABLE tentativas_acesso (
    id UUID PRIMARY KEY,
    usuario_id UUID,
    email VARCHAR(255) NOT NULL,
    permissao VARCHAR(50) NOT NULL,
    acesso_permitido BOOLEAN NOT NULL,
    motivo VARCHAR(255) NOT NULL,
    data_hora TIMESTAMP NOT NULL,

    CONSTRAINT fk_tentativas_acesso_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id)
        ON DELETE SET NULL
);