package br.com.gustavo.iam;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ControleAcessoServiceTest {

    @Test
    void devePermitirAcessoQuandoUsuarioTemPermissaoStatusAtivoEMfaAtivo() {
        UsuarioService usuarioService = new UsuarioService();
        AuditoriaService auditoriaService = new AuditoriaService();
        ControleAcessoService controleAcessoService = new ControleAcessoService(usuarioService, auditoriaService);

        VerificarAcessoRequest request = new VerificarAcessoRequest(
                "gustavo@email.com",
                Permissao.DELETAR_USUARIO);

        VerificarAcessoResponse response = controleAcessoService.verificarAcesso(request);

        assertTrue(response.isAcessoPermitido());
        assertEquals("Gustavo", response.getUsuario());
        assertEquals(Permissao.DELETAR_USUARIO, response.getPermissao());
        assertEquals("Usuário possui permissão, status ativo e MFA ativo", response.getMotivo());
    }

    @Test
    void deveNegarAcessoQuandoUsuarioNaoTemPermissao() {
        UsuarioService usuarioService = new UsuarioService();
        AuditoriaService auditoriaService = new AuditoriaService();
        ControleAcessoService controleAcessoService = new ControleAcessoService(usuarioService, auditoriaService);

        VerificarAcessoRequest request = new VerificarAcessoRequest(
                "maria@email.com",
                Permissao.DELETAR_USUARIO);

        VerificarAcessoResponse response = controleAcessoService.verificarAcesso(request);

        assertFalse(response.isAcessoPermitido());
        assertEquals("Maria", response.getUsuario());
        assertEquals(Permissao.DELETAR_USUARIO, response.getPermissao());
        assertEquals("Usuário não possui a permissão solicitada", response.getMotivo());
    }

    @Test
    void deveNegarAcessoQuandoMfaNaoEstaAtivo() {
        UsuarioService usuarioService = new UsuarioService();
        AuditoriaService auditoriaService = new AuditoriaService();
        ControleAcessoService controleAcessoService = new ControleAcessoService(usuarioService, auditoriaService);

        VerificarAcessoRequest request = new VerificarAcessoRequest(
                "joao@email.com",
                Permissao.VER_PERFIL
        );

        VerificarAcessoResponse response = controleAcessoService.verificarAcesso(request);

        assertFalse(response.isAcessoPermitido());
        assertEquals("João", response.getUsuario());
        assertEquals(Permissao.VER_PERFIL, response.getPermissao());
        assertEquals("Usuário não pode acessar. MFA não está ativo", response.getMotivo());
    }

    @Test
    void deveNegarAcessoQuandoUsuarioEstaBloqueado() {
        UsuarioService usuarioService = new UsuarioService();
        AuditoriaService auditoriaService = new AuditoriaService();
        ControleAcessoService controleAcessoService = new ControleAcessoService(usuarioService, auditoriaService);

        CriarUsuarioRequest criarUsuarioRequest = new CriarUsuarioRequest();
        criarUsuarioRequest.setNome("Bruno");
        criarUsuarioRequest.setEmail("bruno@email.com");
        criarUsuarioRequest.setRole(Role.ADMIN);
        criarUsuarioRequest.setMfaAtivo(true);
        criarUsuarioRequest.setStatus(StatusUsuario.BLOQUEADO);

        usuarioService.cadastrar(criarUsuarioRequest);

        VerificarAcessoRequest request = new VerificarAcessoRequest(
                "bruno@email.com",
                Permissao.DELETAR_USUARIO
        );

        VerificarAcessoResponse response = controleAcessoService.verificarAcesso(request);

        assertFalse(response.isAcessoPermitido());
        assertEquals("Bruno", response.getUsuario());
        assertEquals(Permissao.DELETAR_USUARIO, response.getPermissao());
        assertEquals("Usuário bloqueado", response.getMotivo());
    }

    @Test
    void deveNegarAcessoQuandoUsuarioEstaPendente() {
        UsuarioService usuarioService = new UsuarioService();
        AuditoriaService auditoriaService = new AuditoriaService();
        ControleAcessoService controleAcessoService = new ControleAcessoService(usuarioService, auditoriaService);

        CriarUsuarioRequest criarUsuarioRequest = new CriarUsuarioRequest();
        criarUsuarioRequest.setNome("Paula");
        criarUsuarioRequest.setEmail("paula@email.com");
        criarUsuarioRequest.setRole(Role.ADMIN);
        criarUsuarioRequest.setMfaAtivo(true);
        criarUsuarioRequest.setStatus(StatusUsuario.PENDENTE);

        usuarioService.cadastrar(criarUsuarioRequest);

        VerificarAcessoRequest request = new VerificarAcessoRequest(
                "paula@email.com",
                Permissao.DELETAR_USUARIO
        );

        VerificarAcessoResponse response = controleAcessoService.verificarAcesso(request);

        assertFalse(response.isAcessoPermitido());
        assertEquals("Paula", response.getUsuario());
        assertEquals(Permissao.DELETAR_USUARIO, response.getPermissao());
        assertEquals("Usuário pendente de ativação", response.getMotivo());
    }

    @Test
    void deveNegarAcessoQuandoUsuarioNaoExiste() {
        UsuarioService usuarioService = new UsuarioService();
        AuditoriaService auditoriaService = new AuditoriaService();
        ControleAcessoService controleAcessoService = new ControleAcessoService(usuarioService, auditoriaService);

        VerificarAcessoRequest request = new VerificarAcessoRequest(
                "naoexiste@email.com",
                Permissao.DELETAR_USUARIO
        );

        VerificarAcessoResponse response = controleAcessoService.verificarAcesso(request);

        assertFalse(response.isAcessoPermitido());
        assertNull(response.getUsuario());
        assertEquals(Permissao.DELETAR_USUARIO, response.getPermissao());
        assertEquals("Usuário não encontrado", response.getMotivo());
    }

    @Test
    void deveRegistrarTentativaNaAuditoriaAoVerificarAcesso() {
        UsuarioService usuarioService = new UsuarioService();
        AuditoriaService auditoriaService = new AuditoriaService();
        ControleAcessoService controleAcessoService = new ControleAcessoService(usuarioService, auditoriaService);

        VerificarAcessoRequest request = new VerificarAcessoRequest(
                "gustavo@email.com",
                Permissao.DELETAR_USUARIO
        );

        controleAcessoService.verificarAcesso(request);

        Collection<TentativaAcesso> tentativas = auditoriaService.listarTentativas();

        assertEquals(1, tentativas.size());
    }
}
