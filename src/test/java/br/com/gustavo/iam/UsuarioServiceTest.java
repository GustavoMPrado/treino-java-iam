package br.com.gustavo.iam;

import br.com.gustavo.iam.identidade.adapter.in.web.dto.ConfirmarMfaRequest;
import br.com.gustavo.iam.identidade.adapter.in.web.dto.CriarUsuarioRequest;
import br.com.gustavo.iam.identidade.adapter.in.web.dto.IniciarMfaResponse;
import br.com.gustavo.iam.identidade.adapter.in.web.dto.UsuarioResponse;
import br.com.gustavo.iam.identidade.application.UsuarioService;
import br.com.gustavo.iam.identidade.application.port.out.UsuarioRepositoryPort;
import br.com.gustavo.iam.identidade.domain.Role;
import br.com.gustavo.iam.identidade.domain.StatusUsuario;
import br.com.gustavo.iam.identidade.domain.Usuario;
import br.com.gustavo.iam.shared.exception.MfaInvalidoException;
import br.com.gustavo.iam.shared.exception.UsuarioJaExisteException;
import br.com.gustavo.iam.shared.exception.UsuarioNaoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;


// Testes unitários das regras de negócio do UsuarioService.
// O repository é mockado para manter os testes isolados da infraestrutura.

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    // Mock da porta de persistência usada pelo UsuarioService.
    @Mock
    private UsuarioRepositoryPort usuarioRepository;

    private UsuarioService usuarioService;

    // Cria uma nova instância do service antes de cada teste.
    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioService(usuarioRepository);
    }

    @Test
    void deveListarUsuariosIniciais() {

        Collection<UsuarioResponse> usuarios = usuarioService.listarTodos();

        assertEquals(3, usuarios.size());
    }

    @Test
    void deveBuscarUsuarioExistentePorEmail() {

        UsuarioResponse usuario = usuarioService.buscarResponsePorEmail("gustavo@email.com");

        assertEquals("Gustavo", usuario.getNome());
        assertEquals("gustavo@email.com", usuario.getEmail());
        assertEquals(Role.ADMIN, usuario.getRole());
        assertTrue(usuario.isMfaAtivo());
        assertEquals(StatusUsuario.ATIVO, usuario.getStatus());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExiste() {

        UsuarioNaoEncontradoException exception = assertThrows(
                UsuarioNaoEncontradoException.class,
                () -> usuarioService.buscarResponsePorEmail("naoexiste@email.com")
        );

        assertEquals("Usuário não encontrado com o e-mail: naoexiste@email.com", exception.getMessage());
    }

    @Test
    void deveCadastrarNovoUsuario() {

        CriarUsuarioRequest request = new CriarUsuarioRequest();
        request.setNome("Carlos");
        request.setEmail("carlos@email.com");
        request.setRole(Role.USER);
        request.setMfaAtivo(true);
        request.setStatus(StatusUsuario.ATIVO);

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

        UsuarioJaExisteException exception = assertThrows(
                UsuarioJaExisteException.class,
                () -> usuarioService.cadastrar(request)
        );

        assertEquals("Já existe um usuário cadastrado com o e-mail: gustavo@email.com", exception.getMessage());
    }

    @Test
    void deveBuscarUsuarioInternoPorEmail() {

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

        Usuario usuario = usuarioService.buscarPorEmail("naoexiste@email.com");

        assertNull(usuario);
    }

    @Test
    void deveBloquearUsuario() {

        UsuarioResponse usuario = usuarioService.bloquearUsuario("gustavo@email.com");

        assertEquals("Gustavo", usuario.getNome());
        assertEquals("gustavo@email.com", usuario.getEmail());
        assertEquals(StatusUsuario.BLOQUEADO, usuario.getStatus());
    }

    @Test
    void deveAtivarUsuario() {

        usuarioService.bloquearUsuario("gustavo@email.com");

        UsuarioResponse usuario = usuarioService.ativarUsuario("gustavo@email.com");

        assertEquals("Gustavo", usuario.getNome());
        assertEquals("gustavo@email.com", usuario.getEmail());
        assertEquals(StatusUsuario.ATIVO, usuario.getStatus());
    }

    @Test
    void deveMarcarUsuarioComoPendente() {

        UsuarioResponse usuario = usuarioService.marcarUsuarioComoPendente("gustavo@email.com");

        assertEquals("Gustavo", usuario.getNome());
        assertEquals("gustavo@email.com", usuario.getEmail());
        assertEquals(StatusUsuario.PENDENTE, usuario.getStatus());
    }

    @Test
    void deveLancarExcecaoAoBloquearUsuarioInexistente() {

        assertThrows(
                UsuarioNaoEncontradoException.class,
                () -> usuarioService.bloquearUsuario("naoexiste@email.com"));
    }

    @Test
    void deveLancarExcecaoAoAtivarUsuarioInexistente() {

        assertThrows(
                UsuarioNaoEncontradoException.class,
                () -> usuarioService.ativarUsuario("naoexiste@email.com"));
    }

    @Test
    void deveLancarExcecaoAoMarcarUsuarioInexistenteComoPendente() {

        assertThrows(
                UsuarioNaoEncontradoException.class,
                () -> usuarioService.marcarUsuarioComoPendente("naoexiste@email.com"));
    }

    @Test
    void deveIniciarFluxoDeMfa() {

        IniciarMfaResponse response = usuarioService.iniciarMfa("joao@email.com");

        assertEquals("joao@email.com", response.getEmail());
        assertTrue(response.isMfaPendente());
        assertEquals("123456", response.getCodigoSimulado());
    }

    @Test
    void deveConfirmarMfaComCodigoCorreto() {

        usuarioService.iniciarMfa("joao@email.com");

        ConfirmarMfaRequest request = new ConfirmarMfaRequest();
        request.setCodigo("123456");

        UsuarioResponse usuario = usuarioService.confirmarMfa("joao@email.com", request);

        assertEquals("João", usuario.getNome());
        assertEquals("joao@email.com", usuario.getEmail());
        assertTrue(usuario.isMfaAtivo());
    }

    @Test
    void deveLancarExcecaoQuandoCodigoMfaForInvalido() {

        usuarioService.iniciarMfa("joao@email.com");

        ConfirmarMfaRequest request = new ConfirmarMfaRequest();
        request.setCodigo("000000");

        assertThrows(
                MfaInvalidoException.class,
                () -> usuarioService.confirmarMfa("joao@email.com", request));
    }

    @Test
    void deveLancarExcecaoQuandoConfirmarMfaSemFluxoIniciado() {

        ConfirmarMfaRequest request = new ConfirmarMfaRequest();
        request.setCodigo("123456");

        assertThrows(
                MfaInvalidoException.class,
                () -> usuarioService.confirmarMfa("joao@email.com", request));
    }

    @Test
    void deveDesativarMfa() {

        usuarioService.iniciarMfa("joao@email.com");

        ConfirmarMfaRequest request = new ConfirmarMfaRequest();
        request.setCodigo("123456");

        usuarioService.confirmarMfa("joao@email.com", request);

        UsuarioResponse usuario = usuarioService.desativarMfa("joao@email.com");

        assertEquals("João", usuario.getNome());
        assertEquals("joao@email.com", usuario.getEmail());
        assertFalse(usuario.isMfaAtivo());
    }

    @Test
    void deveLancarExcecaoAoIniciarMfaParaUsuarioInexistente() {

        assertThrows(
                UsuarioNaoEncontradoException.class,
                () -> usuarioService.iniciarMfa("naoexiste@email.com"));
    }

    @Test
    void deveLancarExcecaoAoConfirmarMfaParaUsuarioInexistente() {

        ConfirmarMfaRequest request = new ConfirmarMfaRequest();
        request.setCodigo("123456");

        assertThrows(
                UsuarioNaoEncontradoException.class,
                () -> usuarioService.confirmarMfa("naoexiste@email.com", request));
    }

    @Test
    void deveLancarExcecaoAoDesativarMfaParaUsuarioInexistente() {

        assertThrows(
                UsuarioNaoEncontradoException.class,
                () -> usuarioService.desativarMfa("naoexiste@email.com"));
    }
}