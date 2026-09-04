package br.com.gustavo.iam.auditoria.adapter.out.persistence;

import br.com.gustavo.iam.auditoria.application.port.out.AuditoriaRepositoryPort;
import br.com.gustavo.iam.auditoria.domain.TentativaAcesso;
import org.springframework.stereotype.Repository;

import java.util.Collection;

// Adapter responsável por implementar a porta de persistência da auditoria.
@Repository
public class AuditoriaRepositoryAdapter implements AuditoriaRepositoryPort {

    private final AuditoriaJpaRepository auditoriaJpaRepository;

    public AuditoriaRepositoryAdapter(AuditoriaJpaRepository auditoriaJpaRepository) {
        this.auditoriaJpaRepository = auditoriaJpaRepository;
    }

    @Override
    public TentativaAcesso salvar(TentativaAcesso tentativa) {
        return auditoriaJpaRepository.save(tentativa);
    }

    @Override
    public Collection<TentativaAcesso> listarTodas() {
        return auditoriaJpaRepository.findAll();
    }

    @Override
    public Collection<TentativaAcesso> buscarPorEmail(String email) {
        return auditoriaJpaRepository.findByEmailIgnoreCase(email);
    }

    @Override
    public Collection<TentativaAcesso> buscarPorResultado(boolean acessoPermitido) {
        return auditoriaJpaRepository.findByAcessoPermitido(acessoPermitido);
    }

    @Override
    public Collection<TentativaAcesso> buscarPorEmailEResultado(
            String email,
            boolean acessoPermitido
    ) {
        return auditoriaJpaRepository
                .findByEmailIgnoreCaseAndAcessoPermitido(email, acessoPermitido);
    }
}