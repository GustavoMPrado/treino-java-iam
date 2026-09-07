package br.com.gustavo.iam;

import br.com.gustavo.iam.identidade.adapter.out.persistence.DesafioMfaRepositoryAdapter;
import br.com.gustavo.iam.identidade.adapter.out.persistence.UsuarioRepositoryAdapter;
import br.com.gustavo.iam.identidade.application.port.out.DesafioMfaRepositoryPort;
import br.com.gustavo.iam.identidade.application.port.out.UsuarioRepositoryPort;
import br.com.gustavo.iam.identidade.domain.DesafioMfa;
import br.com.gustavo.iam.identidade.domain.Role;
import br.com.gustavo.iam.identidade.domain.StatusDesafioMfa;
import br.com.gustavo.iam.identidade.domain.StatusUsuario;
import br.com.gustavo.iam.identidade.domain.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({
        TestcontainersConfiguration.class,
        UsuarioRepositoryAdapter.class,
        DesafioMfaRepositoryAdapter.class
})
class DesafioMfaRepositoryTest {

    @Autowired
    private UsuarioRepositoryPort usuarioRepository;

    @Autowired
    private DesafioMfaRepositoryPort desafioMfaRepository;

    @Test
    void deveSalvarEBuscarDesafioPendentePorUsuario() {
        Usuario usuario = new Usuario(
                "Carlos",
                "carlos@email.com",
                Role.USER,
                false,
                StatusUsuario.ATIVO);

        usuarioRepository.salvar(usuario);

        DesafioMfa desafio = new DesafioMfa(
                usuario,
                "hash-teste",
                LocalDateTime.now().plusMinutes(5));

        desafioMfaRepository.salvar(desafio);

        Optional<DesafioMfa> desafioEncontrado =
                desafioMfaRepository.buscarPendentePorUsuario(usuario.getId());

        assertTrue(desafioEncontrado.isPresent());
        assertEquals(
                StatusDesafioMfa.PENDENTE,
                desafioEncontrado.get().getStatus());
    }

    @Test
    void deveDeixarDeBuscarDesafioDepoisDeSubstituido() {
        Usuario usuario = new Usuario(
                "Maria",
                "maria@email.com",
                Role.GESTOR,
                false,
                StatusUsuario.ATIVO);

        usuarioRepository.salvar(usuario);

        DesafioMfa desafio = new DesafioMfa(
                usuario,
                "hash-teste",
                LocalDateTime.now().plusMinutes(5));

        desafioMfaRepository.salvar(desafio);

        desafio.substituir();
        desafioMfaRepository.salvar(desafio);

        Optional<DesafioMfa> desafioPendente =
                desafioMfaRepository.buscarPendentePorUsuario(usuario.getId());

        assertTrue(desafioPendente.isEmpty());
        assertEquals(StatusDesafioMfa.SUBSTITUIDO, desafio.getStatus());
    }
}