package br.com.gustavo.iam.auditoria.application;

import br.com.gustavo.iam.auditoria.application.port.out.AuditoriaRepositoryPort;
import br.com.gustavo.iam.auditoria.domain.TentativaAcesso;
import br.com.gustavo.iam.identidade.domain.Permissao;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;

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
            String motivo
    ) {
        TentativaAcesso tentativa = new TentativaAcesso(
                email,
                permissao,
                acessoPermitido,
                motivo,
                LocalDateTime.now());

        auditoriaRepository.salvar(tentativa);
    }

    public Collection<TentativaAcesso> listarTentativas() {
        return auditoriaRepository.listarTodas();
    }

    public Collection<TentativaAcesso> listarTentativasPorEmail(String email) {
        return auditoriaRepository.buscarPorEmail(email);
    }

    public Collection<TentativaAcesso> listarTentativasPorResultado(boolean acessoPermitido) {
        return auditoriaRepository.buscarPorResultado(acessoPermitido);
    }

    public Collection<TentativaAcesso> listarTentativasPorEmailEResultado(
            String email,
            boolean acessoPermitido
    ) {
        return auditoriaRepository.buscarPorEmailEResultado(
                email,
                acessoPermitido);
    }
}
