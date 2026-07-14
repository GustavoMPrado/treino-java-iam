package br.com.gustavo.iam;

import br.com.gustavo.iam.identidade.adapter.in.web.dto.ConfirmarMfaRequest;
import br.com.gustavo.iam.identidade.adapter.in.web.dto.CriarUsuarioRequest;
import br.com.gustavo.iam.identidade.adapter.in.web.dto.IniciarMfaResponse;
import br.com.gustavo.iam.identidade.adapter.in.web.dto.UsuarioResponse;
import br.com.gustavo.iam.identidade.application.UsuarioService;
import br.com.gustavo.iam.identidade.domain.Role;
import br.com.gustavo.iam.identidade.domain.StatusUsuario;
import br.com.gustavo.iam.identidade.domain.Usuario;
import br.com.gustavo.iam.shared.exception.MfaInvalidoException;
import br.com.gustavo.iam.shared.exception.UsuarioJaExisteException;
import br.com.gustavo.iam.shared.exception.UsuarioNaoEncontradoException;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Testes unitários do UsuarioService.
// Eles validam o cadastro, a busca e as regras dos usuários em memória.
class UsuarioServiceTest {

    @Test
    void deveListarUsuariosIniciais() {
        UsuarioService usuarioService = new UsuarioService();

        Collection<UsuarioResponse> usuarios = usuarioService.listarTodos();

        assertEquals(3, usuarios.size());
    }

    @Test
    void deveBuscarUsuarioExistentePorEmail() {
        UsuarioService usuarioService = new UsuarioService();

        UsuarioResponse usuario = usuarioService.buscarResponsePorEmail("gustavo@email.com");

        assertEquals("Gustavo", usuario.getNome());
        assertEquals("gustavo@email.com", usuario.getEmail());
        assertEquals(Role.ADMIN, usuario.getRole());
        assertTrue(usuario.isMfaAtivo());
        assertEquals(StatusUsuario.ATIVO, usuario.getStatus());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExiste() {
        UsuarioService usuarioService = new UsuarioService();

        UsuarioNaoEncontradoException exception = assertThrows(
                UsuarioNaoEncontradoException.class,
                () -> usuarioService.buscarResponsePorEmail("naoexiste@email.com")
        );

        assertEquals("Usuário não encontrado com o e-mail: naoexiste@email.com", exception.getMessage());
    }

    @Test
    void deveCadastrarNovoUsuario() {
        UsuarioService usuarioService = new UsuarioService();

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
        UsuarioService usuarioService = new UsuarioService();

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
        UsuarioService usuarioService = new UsuarioService();

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
        UsuarioService usuarioService = new UsuarioService();

        Usuario usuario = usuarioService.buscarPorEmail("naoexiste@email.com");

        assertNull(usuario);
    }

    @Test
    void deveBloquearUsuario() {
        UsuarioService usuarioService = new UsuarioService();

        UsuarioResponse usuario = usuarioService.bloquearUsuario("gustavo@email.com");

        assertEquals("Gustavo", usuario.getNome());
        assertEquals("gustavo@email.com", usuario.getEmail());
        assertEquals(StatusUsuario.BLOQUEADO, usuario.getStatus());
    }

    @Test
    void deveAtivarUsuario() {
        UsuarioService usuarioService = new UsuarioService();

        usuarioService.bloquearUsuario("gustavo@email.com");

        UsuarioResponse usuario = usuarioService.ativarUsuario("gustavo@email.com");

        assertEquals("Gustavo", usuario.getNome());
        assertEquals("gustavo@email.com", usuario.getEmail());
        assertEquals(StatusUsuario.ATIVO, usuario.getStatus());
    }

    @Test
    void deveMarcarUsuarioComoPendente() {
        UsuarioService usuarioService = new UsuarioService();

        UsuarioResponse usuario = usuarioService.marcarUsuarioComoPendente("gustavo@email.com");

        assertEquals("Gustavo", usuario.getNome());
        assertEquals("gustavo@email.com", usuario.getEmail());
        assertEquals(StatusUsuario.PENDENTE, usuario.getStatus());
    }

    @Test
    void deveLancarExcecaoAoBloquearUsuarioInexistente() {
        UsuarioService usuarioService = new UsuarioService();

        assertThrows(
                UsuarioNaoEncontradoException.class,
                () -> usuarioService.bloquearUsuario("naoexiste@email.com"));
    }

    @Test
    void deveLancarExcecaoAoAtivarUsuarioInexistente() {
        UsuarioService usuarioService = new UsuarioService();

        assertThrows(
                UsuarioNaoEncontradoException.class,
                () -> usuarioService.ativarUsuario("naoexiste@email.com"));
    }

    @Test
    void deveLancarExcecaoAoMarcarUsuarioInexistenteComoPendente() {
        UsuarioService usuarioService = new UsuarioService();

        assertThrows(
                UsuarioNaoEncontradoException.class,
                () -> usuarioService.marcarUsuarioComoPendente("naoexiste@email.com"));
    }

    @Test
    void deveIniciarFluxoDeMfa() {
        UsuarioService usuarioService = new UsuarioService();

        IniciarMfaResponse response = usuarioService.iniciarMfa("joao@email.com");

        assertEquals("joao@email.com", response.getEmail());
        assertTrue(response.isMfaPendente());
        assertEquals("123456", response.getCodigoSimulado());
    }

    @Test
    void deveConfirmarMfaComCodigoCorreto() {
        UsuarioService usuarioService = new UsuarioService();

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
        UsuarioService usuarioService = new UsuarioService();

        usuarioService.iniciarMfa("joao@email.com");

        ConfirmarMfaRequest request = new ConfirmarMfaRequest();
        request.setCodigo("000000");

        assertThrows(
                MfaInvalidoException.class,
                () -> usuarioService.confirmarMfa("joao@email.com", request));
    }

    @Test
    void deveLancarExcecaoQuandoConfirmarMfaSemFluxoIniciado() {
        UsuarioService usuarioService = new UsuarioService();

        ConfirmarMfaRequest request = new ConfirmarMfaRequest();
        request.setCodigo("123456");

        assertThrows(
                MfaInvalidoException.class,
                () -> usuarioService.confirmarMfa("joao@email.com", request));
    }

    @Test
    void deveDesativarMfa() {
        UsuarioService usuarioService = new UsuarioService();

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
        UsuarioService usuarioService = new UsuarioService();

        assertThrows(
                UsuarioNaoEncontradoException.class,
                () -> usuarioService.iniciarMfa("naoexiste@email.com"));
    }

    @Test
    void deveLancarExcecaoAoConfirmarMfaParaUsuarioInexistente() {
        UsuarioService usuarioService = new UsuarioService();

        ConfirmarMfaRequest request = new ConfirmarMfaRequest();
        request.setCodigo("123456");

        assertThrows(
                UsuarioNaoEncontradoException.class,
                () -> usuarioService.confirmarMfa("naoexiste@email.com", request));
    }

    @Test
    void deveLancarExcecaoAoDesativarMfaParaUsuarioInexistente() {
        UsuarioService usuarioService = new UsuarioService();

        assertThrows(
                UsuarioNaoEncontradoException.class,
                () -> usuarioService.desativarMfa("naoexiste@email.com"));
    }
}