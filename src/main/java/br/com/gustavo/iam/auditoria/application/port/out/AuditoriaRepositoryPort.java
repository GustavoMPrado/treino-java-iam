package br.com.gustavo.iam.auditoria.application.port.out;

import br.com.gustavo.iam.auditoria.domain.TentativaAcesso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// Porta de saída para persistência das tentativas de acesso.
public interface AuditoriaRepositoryPort {

    TentativaAcesso salvar(TentativaAcesso tentativa);

    Page<TentativaAcesso> listarTodas(Pageable pageable);

    Page<TentativaAcesso> buscarPorEmail(
            String email, Pageable pageable);

    Page<TentativaAcesso> buscarPorResultado(
            boolean acessoPermitido, Pageable pageable);

    Page<TentativaAcesso> buscarPorEmailEResultado(
            String email, boolean acessoPermitido, Pageable pageable);
}
