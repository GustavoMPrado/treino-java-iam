package br.com.gustavo.iam.identidade.application.port.out;

import br.com.gustavo.iam.identidade.domain.DesafioMfa;

import java.util.Optional;
import java.util.UUID;

// Porta de saída responsável pela persistência dos desafios de MFA.
// Mantém a camada de aplicação desacoplada da implementação do banco.
public interface DesafioMfaRepositoryPort {

    // Salva ou atualiza um desafio de MFA.
    DesafioMfa salvar(DesafioMfa desafio);

    // Busca o desafio pendente de um usuário.
    Optional<DesafioMfa> buscarPendentePorUsuario(UUID usuarioId);
}
