package br.com.gustavo.iam;

import br.com.gustavo.iam.identidade.adapter.out.persistence.UsuarioRepositoryAdapter;
import br.com.gustavo.iam.identidade.application.port.out.UsuarioRepositoryPort;
import br.com.gustavo.iam.identidade.domain.Role;
import br.com.gustavo.iam.identidade.domain.StatusUsuario;
import br.com.gustavo.iam.identidade.domain.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({
        TestcontainersConfiguration.class,
        UsuarioRepositoryAdapter.class
})
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepositoryPort usuarioRepository;

    @Test
    void deveSalvarEBuscarUsuarioPorEmail() {
        Usuario usuario = new Usuario(
                "Carlos",
                "carlos@email.com",
                Role.USER,
                false,
                StatusUsuario.ATIVO);

        usuarioRepository.salvar(usuario);

        Optional<Usuario> usuarioEncontrado =
                usuarioRepository.buscarPorEmail("carlos@email.com");

        assertTrue(usuarioEncontrado.isPresent());
        assertEquals("Carlos", usuarioEncontrado.get().getNome());
        assertEquals("carlos@email.com", usuarioEncontrado.get().getEmail());
        assertEquals(Role.USER, usuarioEncontrado.get().getRole());
        assertFalse(usuarioEncontrado.get().isMfaAtivo());
        assertEquals(StatusUsuario.ATIVO, usuarioEncontrado.get().getStatus());
    }

    @Test
    void deveVerificarSeEmailExiste() {
        Usuario usuario = new Usuario(
                "Maria",
                "maria@email.com",
                Role.GESTOR,
                true,
                StatusUsuario.ATIVO);

        usuarioRepository.salvar(usuario);

        assertTrue(usuarioRepository.existePorEmail("maria@email.com"));
        assertFalse(usuarioRepository.existePorEmail("naoexiste@email.com"));
    }
}