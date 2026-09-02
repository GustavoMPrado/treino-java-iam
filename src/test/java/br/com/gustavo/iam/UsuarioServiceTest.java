package br.com.gustavo.iam;

import br.com.gustavo.iam.identidade.adapter.in.web.dto.CriarUsuarioRequest;
import br.com.gustavo.iam.identidade.adapter.in.web.dto.UsuarioResponse;
import br.com.gustavo.iam.identidade.application.UsuarioService;
import br.com.gustavo.iam.identidade.application.port.out.UsuarioRepositoryPort;
import br.com.gustavo.iam.identidade.domain.Role;
import br.com.gustavo.iam.identidade.domain.StatusUsuario;
import br.com.gustavo.iam.identidade.domain.Usuario;
import br.com.gustavo.iam.shared.exception.UsuarioJaExisteException;
import br.com.gustavo.iam.shared.exception.UsuarioNaoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

// Testes unitários das regras de negócio do UsuarioService.
// O repository é mockado para manter os testes isolados da infraestrutura.
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepository;

    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioService(usuarioRepository);
    }

    @Test
    void deveListarUsuarios() {
        Usuario gustavo = new Usuario(
                "Gustavo",
                "gustavo@email.com",
                Role.ADMIN,
                true,
                StatusUsuario.ATIVO
        );

        Usuario maria = new Usuario(
                "Maria",
                "maria@email.com",
                Role.GESTOR,
                true,
                StatusUsuario.ATIVO
        );

        Usuario joao = new Usuario(
                "João",
                "joao@email.com",
                Role.USER,
                false,
                StatusUsuario.ATIVO
        );

        when(usuarioRepository.listarTodos())
                .thenReturn(List.of(gustavo, maria, joao));

        Collection<UsuarioResponse> usuarios = usuarioService.listarTodos();

        assertEquals(3, usuarios.size());
    }

    @Test
    void deveBuscarUsuarioExistentePorEmail() {
        Usuario gustavo = criarGustavo();

        when(usuarioRepository.buscarPorEmail("gustavo@email.com"))
                .thenReturn(Optional.of(gustavo));

        UsuarioResponse usuario =
                usuarioService.buscarResponsePorEmail("gustavo@email.com");

        assertEquals("Gustavo", usuario.getNome());
        assertEquals("gustavo@email.com", usuario.getEmail());
        assertEquals(Role.ADMIN, usuario.getRole());
        assertTrue(usuario.isMfaAtivo());
        assertEquals(StatusUsuario.ATIVO, usuario.getStatus());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExiste() {
        when(usuarioRepository.buscarPorEmail("naoexiste@email.com"))
                .thenReturn(Optional.empty());

        UsuarioNaoEncontradoException exception = assertThrows(
                UsuarioNaoEncontradoException.class,
                () -> usuarioService.buscarResponsePorEmail("naoexiste@email.com")
        );

        assertEquals(
                "Usuário não encontrado com o e-mail: naoexiste@email.com",
                exception.getMessage()
        );
    }

    @Test
    void deveCadastrarNovoUsuario() {
        CriarUsuarioRequest request = new CriarUsuarioRequest();
        request.setNome("Carlos");
        request.setEmail("carlos@email.com");
        request.setRole(Role.USER);
        request.setMfaAtivo(true);
        request.setStatus(StatusUsuario.ATIVO);

        when(usuarioRepository.existePorEmail("carlos@email.com"))
                .thenReturn(false);

        when(usuarioRepository.salvar(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioResponse usuario = usuarioService.cadastrar(request);

        assertEquals("Carlos", usuario.getNome());
        assertEquals("carlos@email.com", usuario.getEmail());
        assertEquals(Role.USER, usuario.getRole());
        assertTrue(usuario.isMfaAtivo());
        assertEquals(StatusUsuario.ATIVO, usuario.getStatus());
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaExiste() {
        CriarUsuarioRequest request = new CriarUsuarioRequest();
        request.setNome("Outro Gustavo");
        request.setEmail("gustavo@email.com");
        request.setRole(Role.USER);
        request.setMfaAtivo(true);
        request.setStatus(StatusUsuario.ATIVO);

        when(usuarioRepository.existePorEmail("gustavo@email.com"))
                .thenReturn(true);

        UsuarioJaExisteException exception = assertThrows(
                UsuarioJaExisteException.class,
                () -> usuarioService.cadastrar(request)
        );

        assertEquals(
                "Já existe um usuário cadastrado com o e-mail: gustavo@email.com",
                exception.getMessage()
        );
    }

    @Test
    void deveBuscarUsuarioInternoPorEmail() {
        Usuario gustavo = criarGustavo();

        when(usuarioRepository.buscarPorEmail("gustavo@email.com"))
                .thenReturn(Optional.of(gustavo));

        Usuario usuario = usuarioService.buscarPorEmail("gustavo@email.com");

        assertNotNull(usuario);
        assertEquals("Gustavo", usuario.getNome());
        assertEquals("gustavo@email.com", usuario.getEmail());
        assertEquals(Role.ADMIN, usuario.getRole());
        assertTrue(usuario.isMfaAtivo());
        assertEquals(StatusUsuario.ATIVO, usuario.getStatus());
    }

    @Test
    void deveRetornarNullQuandoBuscarUsuarioInternoNaoEncontrarEmail() {
        when(usuarioRepository.buscarPorEmail("naoexiste@email.com"))
                .thenReturn(Optional.empty());

        Usuario usuario =
                usuarioService.buscarPorEmail("naoexiste@email.com");

        assertNull(usuario);
    }

    @Test
    void deveBloquearUsuario() {
        Usuario gustavo = criarGustavo();

        when(usuarioRepository.buscarPorEmail("gustavo@email.com"))
                .thenReturn(Optional.of(gustavo));

        when(usuarioRepository.salvar(gustavo))
                .thenReturn(gustavo);

        UsuarioResponse usuario =
                usuarioService.bloquearUsuario("gustavo@email.com");

        assertEquals("Gustavo", usuario.getNome());
        assertEquals("gustavo@email.com", usuario.getEmail());
        assertEquals(StatusUsuario.BLOQUEADO, usuario.getStatus());
    }

    @Test
    void deveAtivarUsuario() {
        Usuario gustavo = new Usuario(
                "Gustavo",
                "gustavo@email.com",
                Role.ADMIN,
                true,
                StatusUsuario.BLOQUEADO
        );

        when(usuarioRepository.buscarPorEmail("gustavo@email.com"))
                .thenReturn(Optional.of(gustavo));

        when(usuarioRepository.salvar(gustavo))
                .thenReturn(gustavo);

        UsuarioResponse usuario =
                usuarioService.ativarUsuario("gustavo@email.com");

        assertEquals("Gustavo", usuario.getNome());
        assertEquals("gustavo@email.com", usuario.getEmail());
        assertEquals(StatusUsuario.ATIVO, usuario.getStatus());
    }

    @Test
    void deveMarcarUsuarioComoPendente() {
        Usuario gustavo = criarGustavo();

        when(usuarioRepository.buscarPorEmail("gustavo@email.com"))
                .thenReturn(Optional.of(gustavo));

        when(usuarioRepository.salvar(gustavo))
                .thenReturn(gustavo);

        UsuarioResponse usuario =
                usuarioService.marcarUsuarioComoPendente("gustavo@email.com");

        assertEquals("Gustavo", usuario.getNome());
        assertEquals("gustavo@email.com", usuario.getEmail());
        assertEquals(StatusUsuario.PENDENTE, usuario.getStatus());
    }

    @Test
    void deveLancarExcecaoAoBloquearUsuarioInexistente() {
        when(usuarioRepository.buscarPorEmail("naoexiste@email.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsuarioNaoEncontradoException.class,
                () -> usuarioService.bloquearUsuario("naoexiste@email.com")
        );
    }

    @Test
    void deveLancarExcecaoAoAtivarUsuarioInexistente() {
        when(usuarioRepository.buscarPorEmail("naoexiste@email.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsuarioNaoEncontradoException.class,
                () -> usuarioService.ativarUsuario("naoexiste@email.com")
        );
    }

    @Test
    void deveLancarExcecaoAoMarcarUsuarioInexistenteComoPendente() {
        when(usuarioRepository.buscarPorEmail("naoexiste@email.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsuarioNaoEncontradoException.class,
                () -> usuarioService.marcarUsuarioComoPendente("naoexiste@email.com")
        );
    }

    @Test
    void deveDesativarMfa() {
        Usuario gustavo = criarGustavo();

        when(usuarioRepository.buscarPorEmail("gustavo@email.com"))
                .thenReturn(Optional.of(gustavo));

        when(usuarioRepository.salvar(gustavo))
                .thenReturn(gustavo);

        UsuarioResponse usuario =
                usuarioService.desativarMfa("gustavo@email.com");

        assertEquals("Gustavo", usuario.getNome());
        assertEquals("gustavo@email.com", usuario.getEmail());
        assertFalse(usuario.isMfaAtivo());
    }

    @Test
    void deveLancarExcecaoAoDesativarMfaParaUsuarioInexistente() {
        when(usuarioRepository.buscarPorEmail("naoexiste@email.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsuarioNaoEncontradoException.class,
                () -> usuarioService.desativarMfa("naoexiste@email.com")
        );
    }

    private Usuario criarGustavo() {
        return new Usuario(
                "Gustavo",
                "gustavo@email.com",
                Role.ADMIN,
                true,
                StatusUsuario.ATIVO
        );
    }
}