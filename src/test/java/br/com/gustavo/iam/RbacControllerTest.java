package br.com.gustavo.iam;

import br.com.gustavo.iam.rbac.adapter.in.web.RbacController;
import br.com.gustavo.iam.shared.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Testes da camada web do controller de RBAC.
// A SecurityConfig real é carregada para validar o comportamento HTTP com segurança.
@WebMvcTest(RbacController.class)
@Import(SecurityConfig.class)
class RbacControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void deveListarRoles() throws Exception {
        mockMvc.perform(get("/rbac/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].nome", containsInAnyOrder(
                        "ADMIN",
                        "GESTOR",
                        "USER")));
    }

    @Test
    void deveListarPermissoes() throws Exception {
        mockMvc.perform(get("/rbac/permissoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[*]", containsInAnyOrder(
                        "CRIAR_USUARIO",
                        "DELETAR_USUARIO",
                        "VER_RELATORIO",
                        "APROVAR_ACESSO",
                        "VER_PERFIL")));
    }

    @Test
    void deveListarRolesComSuasPermissoes() throws Exception {
        mockMvc.perform(get("/rbac/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("ADMIN"))
                .andExpect(jsonPath("$[0].permissoes", hasSize(5)))
                .andExpect(jsonPath("$[0].permissoes", containsInAnyOrder(
                        "CRIAR_USUARIO",
                        "DELETAR_USUARIO",
                        "VER_RELATORIO",
                        "APROVAR_ACESSO",
                        "VER_PERFIL")))
                .andExpect(jsonPath("$[2].nome").value("USER"))
                .andExpect(jsonPath("$[2].permissoes", hasSize(1)))
                .andExpect(jsonPath("$[2].permissoes", containsInAnyOrder(
                        "VER_PERFIL")));
    }
}
