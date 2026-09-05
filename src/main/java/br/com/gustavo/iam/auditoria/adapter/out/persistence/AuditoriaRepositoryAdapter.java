package br.com.gustavo.iam.auditoria.adapter.out.persistence;

import br.com.gustavo.iam.auditoria.application.port.out.AuditoriaRepositoryPort;
import br.com.gustavo.iam.auditoria.domain.TentativaAcesso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

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
    public Page<TentativaAcesso> listarTodas(Pageable pageable) {
        return auditoriaJpaRepository.findAll(pageable);
    }

    @Override
    public Page<TentativaAcesso> buscarPorEmail(
            String email,
            Pageable pageable) {
        return auditoriaJpaRepository.findByEmailIgnoreCase(
                email,
                pageable);
    }

    @Override
    public Page<TentativaAcesso> buscarPorResultado(
            boolean acessoPermitido,
            Pageable pageable) {
        return auditoriaJpaRepository.findByAcessoPermitido(
                acessoPermitido,
                pageable);
    }

    @Override
    public Page<TentativaAcesso> buscarPorEmailEResultado(
            String email,
            boolean acessoPermitido,
            Pageable pageable) {
        return auditoriaJpaRepository
                .findByEmailIgnoreCaseAndAcessoPermitido(
                        email,
                        acessoPermitido,
                        pageable);
    }
}