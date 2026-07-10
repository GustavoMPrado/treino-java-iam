package br.com.gustavo.iam;

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
}