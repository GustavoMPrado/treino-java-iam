package br.com.gustavo.iam.auditoria.adapter.out.persistence;

import br.com.gustavo.iam.auditoria.domain.TentativaAcesso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

// Repository JPA para acesso aos registros de auditoria no PostgreSQL.
public interface AuditoriaJpaRepository extends JpaRepository<TentativaAcesso, UUID> {

    List<TentativaAcesso> findByEmailIgnoreCase(String email);

    List<TentativaAcesso> findByAcessoPermitido(boolean acessoPermitido);

    List<TentativaAcesso> findByEmailIgnoreCaseAndAcessoPermitido(
            String email,
            boolean acessoPermitido
    );
}