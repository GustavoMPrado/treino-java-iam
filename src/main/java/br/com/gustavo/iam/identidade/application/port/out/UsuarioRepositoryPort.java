package br.com.gustavo.iam.identidade.application.port.out;

import br.com.gustavo.iam.identidade.domain.Usuario;

import java.util.Collection;
import java.util.Optional;

public interface UsuarioRepositoryPort {

    Collection<Usuario> listarTodos();

    Optional<Usuario> buscarPorEmail(String email);

    boolean existePorEmail(String email);

    Usuario salvar(Usuario usuario);
}