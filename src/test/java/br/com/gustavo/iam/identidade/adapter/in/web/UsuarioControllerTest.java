package br.com.gustavo.iam.identidade.adapter.in.web;

import br.com.gustavo.iam.identidade.adapter.in.web.dto.ConfirmarMfaRequest;
import br.com.gustavo.iam.identidade.adapter.in.web.dto.IniciarMfaResponse;
import br.com.gustavo.iam.identidade.adapter.in.web.dto.UsuarioResponse;
import br.com.gustavo.iam.identidade.application.MfaService;
import br.com.gustavo.iam.identidade.application.UsuarioService;
import br.com.gustavo.iam.identidade.application.port.in.UsuarioAutenticadoUseCase;
import br.com.gustavo.iam.identidade.application.port.out.UsuarioRepositoryPort;
import br.com.gustavo.iam.identidade.domain.Role;
import br.com.gustavo.iam.identidade.domain.StatusUsuario;
import br.com.gustavo.iam.identidade.domain.Usuario;
import br.com.gustavo.iam.shared.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Testes da camada web do controller de usuários.
// A SecurityConfig real é carregada para validar o comportamento HTTP com segurança.
@WebMvcTest(UsuarioController.class)
@Import({UsuarioService.class, SecurityConfig.class})
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioRepositoryPort usuarioRepository;

    @MockitoBean
    private MfaService mfaService;

    @MockitoBean
    private UsuarioAutenticadoUseCase usuarioAutenticadoUseCase;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
        Usuario gustavo = new Usuario(
                "Gustavo",
                "gustavo@email.com",
                Role.ADMIN,
                true,
                StatusUsuario.ATIVO
        );

        when(usuarioAutenticadoUseCase.resolverUsuarioAutenticado())
                .thenReturn(gustavo);

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
        mockMvc.perform(get("/usuarios")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_GESTOR")
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void deveBuscarUsuarioPorEmail() throws Exception {
        mockMvc.perform(get("/usuarios/gustavo@email.com")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_GESTOR")
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Gustavo"))
                .andExpect(jsonPath("$.email").value("gustavo@email.com"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.mfaAtivo").value(true))
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

    @Test
    void deveRetornar404QuandoUsuarioNaoExiste() throws Exception {
        mockMvc.perform(get("/usuarios/naoexiste@email.com")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_GESTOR")
                        )))
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
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        ))
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
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        ))
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
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        ))
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
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        ))
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
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        ))
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem")
                        .value("Valor inválido no JSON enviado."));
    }

    @Test
    void deveBuscarUsuarioAutenticado() throws Exception {
        mockMvc.perform(get("/usuarios/me")
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Gustavo"))
                .andExpect(jsonPath("$.email").value("gustavo@email.com"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.mfaAtivo").value(true))
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

    @Test
    void deveRetornar401AoBuscarPerfilSemAutenticacao() throws Exception {
        mockMvc.perform(get("/usuarios/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar403QuandoUserTentarListarUsuarios() throws Exception {
        mockMvc.perform(get("/usuarios")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_USER")
                        )))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRetornar403QuandoGestorTentarCadastrarUsuario() throws Exception {
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
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_GESTOR")
                        ))
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void devePermitirAdminListarUsuarios() throws Exception {
        mockMvc.perform(get("/usuarios")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        )))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @ParameterizedTest
    @CsvSource({
            "PATCH, /usuarios/gustavo@email.com/bloquear, USER",
            "PATCH, /usuarios/gustavo@email.com/bloquear, GESTOR",
            "PATCH, /usuarios/gustavo@email.com/ativar, USER",
            "PATCH, /usuarios/gustavo@email.com/ativar, GESTOR",
            "PATCH, /usuarios/gustavo@email.com/marcar-pendente, USER",
            "PATCH, /usuarios/gustavo@email.com/marcar-pendente, GESTOR",
            "POST, /usuarios/gustavo@email.com/mfa/iniciar, USER",
            "POST, /usuarios/gustavo@email.com/mfa/iniciar, GESTOR",
            "POST, /usuarios/gustavo@email.com/mfa/confirmar, USER",
            "POST, /usuarios/gustavo@email.com/mfa/confirmar, GESTOR",
            "PATCH, /usuarios/gustavo@email.com/mfa/desativar, USER",
            "PATCH, /usuarios/gustavo@email.com/mfa/desativar, GESTOR"
    })
    void deveNegarOperacoesAdministrativasParaRolesNaoAutorizadas(
            String metodo,
            String endpoint,
            String role
    ) throws Exception {
        mockMvc.perform(request(HttpMethod.valueOf(metodo), endpoint)
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_" + role)
                        )))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @CsvSource({
            "bloquear, ATIVO, BLOQUEADO",
            "ativar, BLOQUEADO, ATIVO",
            "marcar-pendente, ATIVO, PENDENTE"
    })
    void devePermitirAdminAlterarStatusUsuario(
            String operacao,
            StatusUsuario statusInicial,
            StatusUsuario statusEsperado
    ) throws Exception {
        Usuario usuario = new Usuario(
                "Gustavo",
                "gustavo@email.com",
                Role.USER,
                true,
                statusInicial
        );

        when(usuarioRepository.buscarPorEmail("gustavo@email.com"))
                .thenReturn(Optional.of(usuario));

        mockMvc.perform(request(
                        HttpMethod.PATCH,
                        "/usuarios/gustavo@email.com/" + operacao
                )
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(statusEsperado.name()));
    }

    @Test
    void devePermitirAdminIniciarMfa() throws Exception {
        when(mfaService.iniciarMfa("gustavo@email.com"))
                .thenReturn(new IniciarMfaResponse(
                        "gustavo@email.com",
                        true,
                        "123456"
                ));

        mockMvc.perform(post("/usuarios/gustavo@email.com/mfa/iniciar")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        )))
                .andExpect(status().isOk());

        verify(mfaService).iniciarMfa("gustavo@email.com");
    }

    @Test
    void devePermitirAdminDesativarMfa() throws Exception {
        mockMvc.perform(request(
                        HttpMethod.PATCH,
                        "/usuarios/gustavo@email.com/mfa/desativar"
                )
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mfaAtivo").value(false));
    }

    @Test
    void devePermitirAdminConfirmarMfa() throws Exception {
        when(mfaService.confirmarMfa(
                eq("gustavo@email.com"),
                any(ConfirmarMfaRequest.class)
        )).thenReturn(new UsuarioResponse(
                "Gustavo",
                "gustavo@email.com",
                Role.ADMIN,
                true,
                StatusUsuario.ATIVO
        ));

        String body = """
            {
              "codigo": "123456"
            }
            """;

        mockMvc.perform(post("/usuarios/gustavo@email.com/mfa/confirmar")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        ))
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mfaAtivo").value(true));

        verify(mfaService).confirmarMfa(
                eq("gustavo@email.com"),
                any(ConfirmarMfaRequest.class)
        );
    }
}