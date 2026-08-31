package br.com.gustavo.iam.identidade.adapter.out.persistence;

import br.com.gustavo.iam.identidade.application.port.out.UsuarioRepositoryPort;
import br.com.gustavo.iam.identidade.domain.Usuario;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

// Adaptador responsavel pela persistência de usuários com Spring Data JPA.
// Implementa a porta usada pela camada de aplicação.
@Repository
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final UsuarioJpaRepository usuarioJpaRepository;

    public UsuarioRepositoryAdapter(UsuarioJpaRepository usuarioJpaRepository) {
        this.usuarioJpaRepository = usuarioJpaRepository;
    }

    // Retorna todos os usuários persistidos.
    @Override
    public Collection<Usuario> listarTodos() {
        return usuarioJpaRepository.findAll();
    }

    // Busca um usuário pelo e-mail.
    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioJpaRepository.findByEmail(email);
    }

    // Verifica se já existe um usuário com o e-mail informado.
    @Override
    public boolean existePorEmail(String email) {
        return usuarioJpaRepository.existsByEmail(email);
    }

    // Salva ou atualiza um usuário no banco.
    @Override
    public Usuario salvar(Usuario usuario) {
        return usuarioJpaRepository.save(usuario);
    }
}