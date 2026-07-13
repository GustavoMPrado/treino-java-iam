package br.com.gustavo.iam;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Testes do controller de auditoria.
// Cada teste reinicia o contexto para evitar interferência dos dados em memória.
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AuditoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveListarTentativasDeAcesso() throws Exception {
        String body = """
                {
                  "email": "gustavo@email.com",
                  "permissao": "DELETAR_USUARIO" }""";

        mockMvc.perform(post("/acessos/verificar")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isOk());

        mockMvc.perform(get("/auditoria/acessos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email").value("gustavo@email.com"))
                .andExpect(jsonPath("$[0].permissao").value("DELETAR_USUARIO"))
                .andExpect(jsonPath("$[0].acessoPermitido").value(true))
                .andExpect(jsonPath("$[0].motivo").value("Usuário possui permissão, status ativo e MFA ativo"));
    }

    @Test
    void deveListarTentativasPorEmail() throws Exception {
        String bodyGustavo = """
            {
              "email": "gustavo@email.com",
              "permissao": "DELETAR_USUARIO"}""";

        String bodyMaria = """
            {
              "email": "maria@email.com",
              "permissao": "DELETAR_USUARIO"}""";

        mockMvc.perform(post("/acessos/verificar")
                        .contentType("application/json")
                        .content(bodyGustavo))
                .andExpect(status().isOk());

        mockMvc.perform(post("/acessos/verificar")
                        .contentType("application/json")
                        .content(bodyMaria))
                .andExpect(status().isOk());

        mockMvc.perform(get("/auditoria/acessos")
                        .param("email", "maria@email.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email").value("maria@email.com"))
                .andExpect(jsonPath("$[0].permissao").value("DELETAR_USUARIO"))
                .andExpect(jsonPath("$[0].acessoPermitido").value(false))
                .andExpect(jsonPath("$[0].motivo").value("Usuário não possui a permissão solicitada"));
    }

    @Test
    void deveListarTentativasPermitidas() throws Exception {
        String bodyGustavo = """
            {
              "email": "gustavo@email.com",
              "permissao": "DELETAR_USUARIO"}""";

        String bodyMaria = """
            {
              "email": "maria@email.com",
              "permissao": "DELETAR_USUARIO"}""";

        mockMvc.perform(post("/acessos/verificar")
                        .contentType("application/json")
                        .content(bodyGustavo))
                .andExpect(status().isOk());

        mockMvc.perform(post("/acessos/verificar")
                        .contentType("application/json")
                        .content(bodyMaria))
                .andExpect(status().isOk());

        mockMvc.perform(get("/auditoria/acessos")
                        .param("permitido", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email").value("gustavo@email.com"))
                .andExpect(jsonPath("$[0].permissao").value("DELETAR_USUARIO"))
                .andExpect(jsonPath("$[0].acessoPermitido").value(true))
                .andExpect(jsonPath("$[0].motivo").value("Usuário possui permissão, status ativo e MFA ativo"));
    }

    @Test
    void deveListarTentativasNegadas() throws Exception {
        String bodyGustavo = """
            {
              "email": "gustavo@email.com",
              "permissao": "DELETAR_USUARIO"
            }
            """;

        String bodyMaria = """
            {
              "email": "maria@email.com",
              "permissao": "DELETAR_USUARIO"}""";

        mockMvc.perform(post("/acessos/verificar")
                        .contentType("application/json")
                        .content(bodyGustavo))
                .andExpect(status().isOk());

        mockMvc.perform(post("/acessos/verificar")
                        .contentType("application/json")
                        .content(bodyMaria))
                .andExpect(status().isOk());

        mockMvc.perform(get("/auditoria/acessos")
                        .param("permitido", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email").value("maria@email.com"))
                .andExpect(jsonPath("$[0].permissao").value("DELETAR_USUARIO"))
                .andExpect(jsonPath("$[0].acessoPermitido").value(false))
                .andExpect(jsonPath("$[0].motivo").value("Usuário não possui a permissão solicitada"));
    }

    @Test
    void deveListarTentativasPorEmailEResultado() throws Exception {
        String bodyMariaNegado = """
            {
              "email": "maria@email.com",
              "permissao": "DELETAR_USUARIO"}""";

        String bodyMariaPermitido = """
            {
              "email": "maria@email.com",
              "permissao": "VER_RELATORIO"}""";

        mockMvc.perform(post("/acessos/verificar")
                        .contentType("application/json")
                        .content(bodyMariaNegado))
                .andExpect(status().isOk());

        mockMvc.perform(post("/acessos/verificar")
                        .contentType("application/json")
                        .content(bodyMariaPermitido))
                .andExpect(status().isOk());

        mockMvc.perform(get("/auditoria/acessos")
                        .param("email", "maria@email.com")
                        .param("permitido", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email").value("maria@email.com"))
                .andExpect(jsonPath("$[0].permissao").value("DELETAR_USUARIO"))
                .andExpect(jsonPath("$[0].acessoPermitido").value(false))
                .andExpect(jsonPath("$[0].motivo").value("Usuário não possui a permissão solicitada"));
    }

}