package br.com.gustavo.iam.identidade.adapter.out.persistence;

import br.com.gustavo.iam.identidade.application.port.out.DesafioMfaRepositoryPort;
import br.com.gustavo.iam.identidade.domain.DesafioMfa;
import org.springframework.stereotype.Repository;

// Adaptador responsável pela persistência dos desafios de MFA com Spring Data JPA.
// Implementa a porta usada pela camada de aplicação.
@Repository
public class DesafioMfaRepositoryAdapter implements DesafioMfaRepositoryPort {

    private final DesafioMfaJpaRepository desafioMfaJpaRepository;

    public DesafioMfaRepositoryAdapter(DesafioMfaJpaRepository desafioMfaJpaRepository) {
        this.desafioMfaJpaRepository = desafioMfaJpaRepository;
    }

    // Salva ou atualiza um desafio de MFA no banco.
    @Override
    public DesafioMfa salvar(DesafioMfa desafio) {
        return desafioMfaJpaRepository.save(desafio);
    }
}