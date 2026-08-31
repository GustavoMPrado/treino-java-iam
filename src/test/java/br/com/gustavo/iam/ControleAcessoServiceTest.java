package br.com.gustavo.iam;

import br.com.gustavo.iam.acesso.adapter.in.web.dto.VerificarAcessoRequest;
import br.com.gustavo.iam.acesso.adapter.in.web.dto.VerificarAcessoResponse;
import br.com.gustavo.iam.acesso.application.ControleAcessoService;
import br.com.gustavo.iam.auditoria.application.AuditoriaService;
import br.com.gustavo.iam.auditoria.domain.TentativaAcesso;
import br.com.gustavo.iam.identidade.adapter.in.web.dto.CriarUsuarioRequest;
import br.com.gustavo.iam.identidade.application.UsuarioService;
import br.com.gustavo.iam.identidade.application.port.out.UsuarioRepositoryPort;
import br.com.gustavo.iam.identidade.domain.Permissao;
import br.com.gustavo.iam.identidade.domain.Role;
import br.com.gustavo.iam.identidade.domain.StatusUsuario;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

// Testes unitários das regras de controle de acesso.
// O UsuarioRepositoryPort é mockado porque a persistência ainda não participa destes cenários.
class ControleAcessoServiceTest {

    @Test
    void devePermitirAcessoQuandoUsuarioTemPermissaoStatusAtivoEMfaAtivo() {
        UsuarioService usuarioService = new UsuarioService(mock(UsuarioRepositoryPort.class));
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
        UsuarioService usuarioService = new UsuarioService(mock(UsuarioRepositoryPort.class));
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
        UsuarioService usuarioService = new UsuarioService(mock(UsuarioRepositoryPort.class));
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
        UsuarioService usuarioService = new UsuarioService(mock(UsuarioRepositoryPort.class));
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
        UsuarioService usuarioService = new UsuarioService(mock(UsuarioRepositoryPort.class));
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
        UsuarioService usuarioService = new UsuarioService(mock(UsuarioRepositoryPort.class));
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
        UsuarioService usuarioService = new UsuarioService(mock(UsuarioRepositoryPort.class));
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
