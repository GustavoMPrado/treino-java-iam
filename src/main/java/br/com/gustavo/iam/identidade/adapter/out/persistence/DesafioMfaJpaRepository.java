package br.com.gustavo.iam.identidade.adapter.out.persistence;

import br.com.gustavo.iam.identidade.domain.DesafioMfa;
import br.com.gustavo.iam.identidade.domain.StatusDesafioMfa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

// Repository JPA responsável pelo acesso à tabela de desafios de MFA.
public interface DesafioMfaJpaRepository extends JpaRepository<DesafioMfa, UUID> {

    Optional<DesafioMfa> findFirstByUsuarioIdAndStatusOrderByCriadoEmDesc(
            UUID usuarioId,
            StatusDesafioMfa status);
}