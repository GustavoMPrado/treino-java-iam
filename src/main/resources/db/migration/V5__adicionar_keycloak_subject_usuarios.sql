ALTER TABLE usuarios
    ADD COLUMN keycloak_subject VARCHAR(255);

ALTER TABLE usuarios
    ADD CONSTRAINT uq_usuarios_keycloak_subject UNIQUE (keycloak_subject);