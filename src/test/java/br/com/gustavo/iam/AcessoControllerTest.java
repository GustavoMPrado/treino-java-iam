package br.com.gustavo.iam;

import br.com.gustavo.iam.auditoria.application.port.out.AuditoriaRepositoryPort;
import br.com.gustavo.iam.acesso.adapter.in.web.AcessoController;
import br.com.gustavo.iam.acesso.application.ControleAcessoService;
import br.com.gustavo.iam.auditoria.application.AuditoriaService;
import br.com.gustavo.iam.identidade.application.UsuarioService;
import br.com.gustavo.iam.identidade.application.port.out.UsuarioRepositoryPort;
import br.com.gustavo.iam.identidade.domain.Role;
import br.com.gustavo.iam.identidade.domain.StatusUsuario;
import br.com.gustavo.iam.identidade.domain.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Testes do controller de verificação de acesso.
// A requisição passa pelo controller e pelas regras reais de acesso.
@WebMvcTest(AcessoController.class)
@Import({
        ControleAcessoService.class,
        UsuarioService.class,
        AuditoriaService.class
})
class AcessoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioRepositoryPort usuarioRepository;

    @MockitoBean
    private AuditoriaRepositoryPort auditoriaRepository;

    @BeforeEach
    void setUp() {
        Usuario gustavo = new Usuario(
                "Gustavo",
                "gustavo@email.com",
                Role.ADMIN,
                true,
                StatusUsuario.ATIVO);

        Usuario maria = new Usuario(
                "Maria",
                "maria@email.com",
                Role.GESTOR,
                true,
                StatusUsuario.ATIVO);

        Usuario joao = new Usuario(
                "João",
                "joao@email.com",
                Role.USER,
                false,
                StatusUsuario.ATIVO);

        Usuario bruno = new Usuario(
                "Bruno",
                "bruno@email.com",
                Role.ADMIN,
                true,
                StatusUsuario.BLOQUEADO);

        Usuario paula = new Usuario(
                "Paula",
                "paula@email.com",
                Role.ADMIN,
                true,
                StatusUsuario.PENDENTE);

        when(usuarioRepository.buscarPorEmail("gustavo@email.com"))
                .thenReturn(Optional.of(gustavo));

        when(usuarioRepository.buscarPorEmail("maria@email.com"))
                .thenReturn(Optional.of(maria));

        when(usuarioRepository.buscarPorEmail("joao@email.com"))
                .thenReturn(Optional.of(joao));

        when(usuarioRepository.buscarPorEmail("bruno@email.com"))
                .thenReturn(Optional.of(bruno));

        when(usuarioRepository.buscarPorEmail("paula@email.com"))
                .thenReturn(Optional.of(paula));

        when(usuarioRepository.buscarPorEmail("naoexiste@email.com"))
                .thenReturn(Optional.empty());
    }

    @Test
    void devePermitirAcessoQuandoUsuarioTemPermissaoStatusAtivoEMfaAtivo() throws Exception {
        String body = """
                {
                  "email": "gustavo@email.com",
                  "permissao": "DELETAR_USUARIO"
                }
                """;

        mockMvc.perform(post("/acessos/verificar")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuario").value("Gustavo"))
                .andExpect(jsonPath("$.permissao").value("DELETAR_USUARIO"))
                .andExpect(jsonPath("$.acessoPermitido").value(true))
                .andExpect(jsonPath("$.motivo")
                        .value("Usuário possui permissão, status ativo e MFA ativo"));
    }

    @Test
    void deveNegarAcessoQuandoUsuarioNaoTemPermissao() throws Exception {
        String body = """
                {
                  "email": "maria@email.com",
                  "permissao": "DELETAR_USUARIO"
                }
                """;

        mockMvc.perform(post("/acessos/verificar")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuario").value("Maria"))
                .andExpect(jsonPath("$.permissao").value("DELETAR_USUARIO"))
                .andExpect(jsonPath("$.acessoPermitido").value(false))
                .andExpect(jsonPath("$.motivo")
                        .value("Usuário não possui a permissão solicitada"));
    }

    @Test
    void deveNegarAcessoQuandoMfaNaoEstaAtivo() throws Exception {
        String body = """
                {
                  "email": "joao@email.com",
                  "permissao": "VER_PERFIL"
                }
                """;

        mockMvc.perform(post("/acessos/verificar")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuario").value("João"))
                .andExpect(jsonPath("$.permissao").value("VER_PERFIL"))
                .andExpect(jsonPath("$.acessoPermitido").value(false))
                .andExpect(jsonPath("$.motivo")
                        .value("Usuário não pode acessar. MFA não está ativo"));
    }

    @Test
    void deveNegarAcessoQuandoUsuarioNaoExiste() throws Exception {
        String body = """
                {
                  "email": "naoexiste@email.com",
                  "permissao": "DELETAR_USUARIO"
                }
                """;

        mockMvc.perform(post("/acessos/verificar")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.permissao").value("DELETAR_USUARIO"))
                .andExpect(jsonPath("$.acessoPermitido").value(false))
                .andExpect(jsonPath("$.motivo").value("Usuário não encontrado"));
    }

    @Test
    void deveNegarAcessoQuandoUsuarioEstaBloqueado() throws Exception {
        String body = """
                {
                  "email": "bruno@email.com",
                  "permissao": "DELETAR_USUARIO"
                }
                """;

        mockMvc.perform(post("/acessos/verificar")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuario").value("Bruno"))
                .andExpect(jsonPath("$.permissao").value("DELETAR_USUARIO"))
                .andExpect(jsonPath("$.acessoPermitido").value(false))
                .andExpect(jsonPath("$.motivo").value("Usuário bloqueado"));
    }

    @Test
    void deveNegarAcessoQuandoUsuarioEstaPendente() throws Exception {
        String body = """
                {
                  "email": "paula@email.com",
                  "permissao": "DELETAR_USUARIO"
                }
                """;

        mockMvc.perform(post("/acessos/verificar")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuario").value("Paula"))
                .andExpect(jsonPath("$.permissao").value("DELETAR_USUARIO"))
                .andExpect(jsonPath("$.acessoPermitido").value(false))
                .andExpect(jsonPath("$.motivo")
                        .value("Usuário pendente de ativação"));
    }

    @Test
    void deveRetornar400QuandoEmailForInvalido() throws Exception {
        String body = """
                {
                  "email": "email-invalido",
                  "permissao": "DELETAR_USUARIO"
                }
                """;

        mockMvc.perform(post("/acessos/verificar")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar400QuandoPermissaoNaoForInformada() throws Exception {
        String body = """
                {
                  "email": "gustavo@email.com"
                }
                """;

        mockMvc.perform(post("/acessos/verificar")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar400QuandoPermissaoForInvalida() throws Exception {
        String body = """
                {
                  "email": "gustavo@email.com",
                  "permissao": "INVALIDA"}""";

        mockMvc.perform(post("/acessos/verificar")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem")
                        .value("Valor inválido no JSON enviado."));
    }
}