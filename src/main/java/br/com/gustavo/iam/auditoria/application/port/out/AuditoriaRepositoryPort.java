package br.com.gustavo.iam.auditoria.application.port.out;

import br.com.gustavo.iam.auditoria.domain.TentativaAcesso;

import java.util.Collection;

// Porta de saída para persistência das tentativas de acesso.
public interface AuditoriaRepositoryPort {

    TentativaAcesso salvar(TentativaAcesso tentativa);

    Collection<TentativaAcesso> listarTodas();

    Collection<TentativaAcesso> buscarPorEmail(String email);

    Collection<TentativaAcesso> buscarPorResultado(boolean acessoPermitido);

    Collection<TentativaAcesso> buscarPorEmailEResultado(
            String email,
            boolean acessoPermitido);
}
