CREATE UNIQUE INDEX uk_desafios_mfa_usuario_pendente
    ON desafios_mfa (usuario_id)
    WHERE status = 'PENDENTE';