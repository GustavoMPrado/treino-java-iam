package br.com.gustavo.iam.auditoria.application;

import br.com.gustavo.iam.auditoria.application.port.out.AuditoriaRepositoryPort;
import br.com.gustavo.iam.auditoria.domain.TentativaAcesso;
import br.com.gustavo.iam.identidade.domain.Permissao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

// Service responsável por registrar e consultar tentativas de acesso.
@Service
public class AuditoriaService {

    private final AuditoriaRepositoryPort auditoriaRepository;

    public AuditoriaService(AuditoriaRepositoryPort auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    public void registrarTentativa(
            String email,
            Permissao permissao,
            boolean acessoPermitido,
            String motivo) {
        TentativaAcesso tentativa = new TentativaAcesso(
                email,
                permissao,
                acessoPermitido,
                motivo,
                LocalDateTime.now());

        auditoriaRepository.salvar(tentativa);
    }

    public Page<TentativaAcesso> listarTentativas(Pageable pageable) {
        return auditoriaRepository.listarTodas(pageable);
    }

    public Page<TentativaAcesso> listarTentativasPorEmail(
            String email,
            Pageable pageable) {
        return auditoriaRepository.buscarPorEmail(email, pageable);
    }

    public Page<TentativaAcesso> listarTentativasPorResultado(
            boolean acessoPermitido,
            Pageable pageable) {
        return auditoriaRepository.buscarPorResultado(
                acessoPermitido,
                pageable);
    }

    public Page<TentativaAcesso> listarTentativasPorEmailEResultado(
            String email,
            boolean acessoPermitido,
            Pageable pageable) {
        return auditoriaRepository.buscarPorEmailEResultado(
                email,
                acessoPermitido,
                pageable);
    }
}