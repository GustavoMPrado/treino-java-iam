package br.com.gustavo.iam.auditoria.adapter.out.persistence;

import br.com.gustavo.iam.auditoria.domain.TentativaAcesso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

// Repository JPA para acesso aos registros de auditoria no PostgreSQL.
public interface AuditoriaJpaRepository extends JpaRepository<TentativaAcesso, UUID> {

    Page<TentativaAcesso> findByEmailIgnoreCase(
            String email, Pageable pageable);

    Page<TentativaAcesso> findByAcessoPermitido(
            boolean acessoPermitido, Pageable pageable);

    Page<TentativaAcesso> findByEmailIgnoreCaseAndAcessoPermitido(
            String email, boolean acessoPermitido, Pageable pageable);
}