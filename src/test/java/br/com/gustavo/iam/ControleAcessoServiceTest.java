package br.com.gustavo.iam;

import br.com.gustavo.iam.acesso.adapter.in.web.dto.VerificarAcessoRequest;
import br.com.gustavo.iam.acesso.adapter.in.web.dto.VerificarAcessoResponse;
import br.com.gustavo.iam.acesso.application.ControleAcessoService;
import br.com.gustavo.iam.auditoria.application.AuditoriaService;
import br.com.gustavo.iam.auditoria.domain.TentativaAcesso;
import br.com.gustavo.iam.identidade.application.UsuarioService;
import br.com.gustavo.iam.identidade.application.port.out.UsuarioRepositoryPort;
import br.com.gustavo.iam.identidade.domain.Permissao;
import br.com.gustavo.iam.identidade.domain.Role;
import br.com.gustavo.iam.identidade.domain.StatusUsuario;
import br.com.gustavo.iam.identidade.domain.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

// Testes unitários das regras de controle de acesso.
// O repository é mockado para manter os testes isolados da infraestrutura.
@ExtendWith(MockitoExtension.class)
class ControleAcessoServiceTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepository;

    private UsuarioService usuarioService;
    private AuditoriaService auditoriaService;
    private ControleAcessoService controleAcessoService;

    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioService(usuarioRepository);
        auditoriaService = new AuditoriaService();
        controleAcessoService = new ControleAcessoService(
                usuarioService,
                auditoriaService
        );
    }

    @Test
    void devePermitirAcessoQuandoUsuarioTemPermissaoStatusAtivoEMfaAtivo() {
        Usuario gustavo = new Usuario(
                "Gustavo",
                "gustavo@email.com",
                Role.ADMIN,
                true,
                StatusUsuario.ATIVO
        );

        when(usuarioRepository.buscarPorEmail("gustavo@email.com"))
                .thenReturn(Optional.of(gustavo));

        VerificarAcessoRequest request = new VerificarAcessoRequest(
                "gustavo@email.com",
                Permissao.DELETAR_USUARIO
        );

        VerificarAcessoResponse response =
                controleAcessoService.verificarAcesso(request);

        assertTrue(response.isAcessoPermitido());
        assertEquals("Gustavo", response.getUsuario());
        assertEquals(Permissao.DELETAR_USUARIO, response.getPermissao());
        assertEquals(
                "Usuário possui permissão, status ativo e MFA ativo",
                response.getMotivo()
        );
    }

    @Test
    void deveNegarAcessoQuandoUsuarioNaoTemPermissao() {
        Usuario maria = new Usuario(
                "Maria",
                "maria@email.com",
                Role.GESTOR,
                true,
                StatusUsuario.ATIVO
        );

        when(usuarioRepository.buscarPorEmail("maria@email.com"))
                .thenReturn(Optional.of(maria));

        VerificarAcessoRequest request = new VerificarAcessoRequest(
                "maria@email.com",
                Permissao.DELETAR_USUARIO
        );

        VerificarAcessoResponse response =
                controleAcessoService.verificarAcesso(request);

        assertFalse(response.isAcessoPermitido());
        assertEquals("Maria", response.getUsuario());
        assertEquals(Permissao.DELETAR_USUARIO, response.getPermissao());
        assertEquals(
                "Usuário não possui a permissão solicitada",
                response.getMotivo()
        );
    }

    @Test
    void deveNegarAcessoQuandoMfaNaoEstaAtivo() {
        Usuario joao = new Usuario(
                "João",
                "joao@email.com",
                Role.USER,
                false,
                StatusUsuario.ATIVO
        );

        when(usuarioRepository.buscarPorEmail("joao@email.com"))
                .thenReturn(Optional.of(joao));

        VerificarAcessoRequest request = new VerificarAcessoRequest(
                "joao@email.com",
                Permissao.VER_PERFIL
        );

        VerificarAcessoResponse response =
                controleAcessoService.verificarAcesso(request);

        assertFalse(response.isAcessoPermitido());
        assertEquals("João", response.getUsuario());
        assertEquals(Permissao.VER_PERFIL, response.getPermissao());
        assertEquals(
                "Usuário não pode acessar. MFA não está ativo",
                response.getMotivo()
        );
    }

    @Test
    void deveNegarAcessoQuandoUsuarioEstaBloqueado() {
        Usuario bruno = new Usuario(
                "Bruno",
                "bruno@email.com",
                Role.ADMIN,
                true,
                StatusUsuario.BLOQUEADO
        );

        when(usuarioRepository.buscarPorEmail("bruno@email.com"))
                .thenReturn(Optional.of(bruno));

        VerificarAcessoRequest request = new VerificarAcessoRequest(
                "bruno@email.com",
                Permissao.DELETAR_USUARIO
        );

        VerificarAcessoResponse response =
                controleAcessoService.verificarAcesso(request);

        assertFalse(response.isAcessoPermitido());
        assertEquals("Bruno", response.getUsuario());
        assertEquals(Permissao.DELETAR_USUARIO, response.getPermissao());
        assertEquals("Usuário bloqueado", response.getMotivo());
    }

    @Test
    void deveNegarAcessoQuandoUsuarioEstaPendente() {
        Usuario paula = new Usuario(
                "Paula",
                "paula@email.com",
                Role.ADMIN,
                true,
                StatusUsuario.PENDENTE
        );

        when(usuarioRepository.buscarPorEmail("paula@email.com"))
                .thenReturn(Optional.of(paula));

        VerificarAcessoRequest request = new VerificarAcessoRequest(
                "paula@email.com",
                Permissao.DELETAR_USUARIO
        );

        VerificarAcessoResponse response =
                controleAcessoService.verificarAcesso(request);

        assertFalse(response.isAcessoPermitido());
        assertEquals("Paula", response.getUsuario());
        assertEquals(Permissao.DELETAR_USUARIO, response.getPermissao());
        assertEquals(
                "Usuário pendente de ativação",
                response.getMotivo()
        );
    }

    @Test
    void deveNegarAcessoQuandoUsuarioNaoExiste() {
        when(usuarioRepository.buscarPorEmail("naoexiste@email.com"))
                .thenReturn(Optional.empty());

        VerificarAcessoRequest request = new VerificarAcessoRequest(
                "naoexiste@email.com",
                Permissao.DELETAR_USUARIO
        );

        VerificarAcessoResponse response =
                controleAcessoService.verificarAcesso(request);

        assertFalse(response.isAcessoPermitido());
        assertNull(response.getUsuario());
        assertEquals(Permissao.DELETAR_USUARIO, response.getPermissao());
        assertEquals("Usuário não encontrado", response.getMotivo());
    }

    @Test
    void deveRegistrarTentativaNaAuditoriaAoVerificarAcesso() {
        Usuario gustavo = new Usuario(
                "Gustavo",
                "gustavo@email.com",
                Role.ADMIN,
                true,
                StatusUsuario.ATIVO
        );

        when(usuarioRepository.buscarPorEmail("gustavo@email.com"))
                .thenReturn(Optional.of(gustavo));

        VerificarAcessoRequest request = new VerificarAcessoRequest(
                "gustavo@email.com",
                Permissao.DELETAR_USUARIO
        );

        controleAcessoService.verificarAcesso(request);

        Collection<TentativaAcesso> tentativas =
                auditoriaService.listarTentativas();

        assertEquals(1, tentativas.size());
    }
}
