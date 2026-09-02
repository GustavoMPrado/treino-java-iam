package br.com.gustavo.iam;

import br.com.gustavo.iam.identidade.adapter.in.web.UsuarioController;
import br.com.gustavo.iam.identidade.application.MfaService;
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

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Testes da camada web de usuários.
// O banco não é carregado neste teste.
@WebMvcTest(UsuarioController.class)
@Import(UsuarioService.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioRepositoryPort usuarioRepository;

    @MockitoBean
    private MfaService mfaService;

    @BeforeEach
    void setUp() {
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

        when(usuarioRepository.buscarPorEmail("gustavo@email.com"))
                .thenReturn(Optional.of(gustavo));

        when(usuarioRepository.buscarPorEmail("naoexiste@email.com"))
                .thenReturn(Optional.empty());

        when(usuarioRepository.existePorEmail("gustavo@email.com"))
                .thenReturn(true);

        when(usuarioRepository.existePorEmail("carlos@email.com"))
                .thenReturn(false);

        when(usuarioRepository.salvar(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void deveListarUsuarios() throws Exception {
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void deveBuscarUsuarioPorEmail() throws Exception {
        mockMvc.perform(get("/usuarios/gustavo@email.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Gustavo"))
                .andExpect(jsonPath("$.email").value("gustavo@email.com"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.mfaAtivo").value(true))
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

    @Test
    void deveRetornar404QuandoUsuarioNaoExiste() throws Exception {
        mockMvc.perform(get("/usuarios/naoexiste@email.com"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem")
                        .value("Usuário não encontrado com o e-mail: naoexiste@email.com"));
    }

    @Test
    void deveCadastrarUsuario() throws Exception {
        String body = """
                {
                  "nome": "Carlos",
                  "email": "carlos@email.com",
                  "role": "USER",
                  "mfaAtivo": true,
                  "status": "ATIVO"
                }
                """;

        mockMvc.perform(post("/usuarios")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Carlos"))
                .andExpect(jsonPath("$.email").value("carlos@email.com"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.mfaAtivo").value(true))
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

    @Test
    void deveRetornar409QuandoUsuarioJaExiste() throws Exception {
        String body = """
                {
                  "nome": "Outro Gustavo",
                  "email": "gustavo@email.com",
                  "role": "USER",
                  "mfaAtivo": true,
                  "status": "ATIVO"
                }
                """;

        mockMvc.perform(post("/usuarios")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem")
                        .value("Já existe um usuário cadastrado com o e-mail: gustavo@email.com"));
    }

    @Test
    void deveRetornar400QuandoNomeEstiverVazio() throws Exception {
        String body = """
                {
                  "nome": "",
                  "email": "carlos@email.com",
                  "role": "USER",
                  "mfaAtivo": true,
                  "status": "ATIVO"
                }
                """;

        mockMvc.perform(post("/usuarios")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar400QuandoEmailForInvalido() throws Exception {
        String body = """
                {
                  "nome": "Carlos",
                  "email": "email-invalido",
                  "role": "USER",
                  "mfaAtivo": true,
                  "status": "ATIVO"
                }
                """;

        mockMvc.perform(post("/usuarios")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar400QuandoRoleForInvalida() throws Exception {
        String body = """
                {
                  "nome": "Carlos",
                  "email": "carlos@email.com",
                  "role": "INVALIDA",
                  "mfaAtivo": true,
                  "status": "ATIVO"
                }
                """;

        mockMvc.perform(post("/usuarios")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Valor inválido no JSON enviado."));
    }
}