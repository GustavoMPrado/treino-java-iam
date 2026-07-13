package br.com.gustavo.iam;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Testes de controller com contexto completo do Spring.
// Cada teste reinicia o contexto para evitar interferência dos dados em memória.
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
                .andExpect(jsonPath("$.mensagem").value("Usuário não encontrado com o e-mail: naoexiste@email.com"));
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
                .andExpect(jsonPath("$.mensagem").value("Já existe um usuário cadastrado com o e-mail: gustavo@email.com"));
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
