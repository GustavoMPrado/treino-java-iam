package br.com.gustavo.iam.identidade.adapter.out.persistence;

import br.com.gustavo.iam.identidade.domain.DesafioMfa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

// Repository JPA responsável pelo acesso à tabela de desafios de MFA.
public interface DesafioMfaJpaRepository extends JpaRepository<DesafioMfa, UUID> {
}