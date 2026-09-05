package br.com.gustavo.iam;

import br.com.gustavo.iam.auditoria.adapter.in.web.AuditoriaController;
import br.com.gustavo.iam.auditoria.application.AuditoriaService;
import br.com.gustavo.iam.auditoria.domain.TentativaAcesso;
import br.com.gustavo.iam.identidade.domain.Permissao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Testes da camada web do controller de auditoria.
@WebMvcTest(AuditoriaController.class)
class AuditoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuditoriaService auditoriaService;

    @Test
    void deveListarTentativasDeAcesso() throws Exception {
        TentativaAcesso tentativa = new TentativaAcesso(
                "gustavo@email.com",
                Permissao.DELETAR_USUARIO,
                true,
                "Usuário possui permissão, status ativo e MFA ativo",
                LocalDateTime.now());

        Page<TentativaAcesso> pagina = criarPagina(tentativa);

        when(auditoriaService.listarTentativas(any(Pageable.class)))
                .thenReturn(pagina);

        mockMvc.perform(get("/auditoria/acessos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].email").value("gustavo@email.com"))
                .andExpect(jsonPath("$.content[0].permissao").value("DELETAR_USUARIO"))
                .andExpect(jsonPath("$.content[0].acessoPermitido").value(true))
                .andExpect(jsonPath("$.content[0].motivo")
                        .value("Usuário possui permissão, status ativo e MFA ativo"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    void deveListarTentativasPorEmail() throws Exception {
        TentativaAcesso tentativa = new TentativaAcesso(
                "maria@email.com",
                Permissao.DELETAR_USUARIO,
                false,
                "Usuário não possui a permissão solicitada",
                LocalDateTime.now());

        Page<TentativaAcesso> pagina = criarPagina(tentativa);

        when(auditoriaService.listarTentativasPorEmail(
                eq("maria@email.com"),
                any(Pageable.class)
        )).thenReturn(pagina);

        mockMvc.perform(get("/auditoria/acessos")
                        .param("email", "maria@email.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].email").value("maria@email.com"))
                .andExpect(jsonPath("$.content[0].permissao").value("DELETAR_USUARIO"))
                .andExpect(jsonPath("$.content[0].acessoPermitido").value(false))
                .andExpect(jsonPath("$.content[0].motivo")
                        .value("Usuário não possui a permissão solicitada"));
    }

    @Test
    void deveListarTentativasPermitidas() throws Exception {
        TentativaAcesso tentativa = new TentativaAcesso(
                "gustavo@email.com",
                Permissao.DELETAR_USUARIO,
                true,
                "Usuário possui permissão, status ativo e MFA ativo",
                LocalDateTime.now());

        Page<TentativaAcesso> pagina = criarPagina(tentativa);

        when(auditoriaService.listarTentativasPorResultado(
                eq(true),
                any(Pageable.class)
        )).thenReturn(pagina);

        mockMvc.perform(get("/auditoria/acessos")
                        .param("permitido", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].email").value("gustavo@email.com"))
                .andExpect(jsonPath("$.content[0].permissao").value("DELETAR_USUARIO"))
                .andExpect(jsonPath("$.content[0].acessoPermitido").value(true))
                .andExpect(jsonPath("$.content[0].motivo")
                        .value("Usuário possui permissão, status ativo e MFA ativo"));
    }

    @Test
    void deveListarTentativasNegadas() throws Exception {
        TentativaAcesso tentativa = new TentativaAcesso(
                "maria@email.com",
                Permissao.DELETAR_USUARIO,
                false,
                "Usuário não possui a permissão solicitada",
                LocalDateTime.now());

        Page<TentativaAcesso> pagina = criarPagina(tentativa);

        when(auditoriaService.listarTentativasPorResultado(
                eq(false),
                any(Pageable.class)
        )).thenReturn(pagina);

        mockMvc.perform(get("/auditoria/acessos")
                        .param("permitido", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].email").value("maria@email.com"))
                .andExpect(jsonPath("$.content[0].permissao").value("DELETAR_USUARIO"))
                .andExpect(jsonPath("$.content[0].acessoPermitido").value(false))
                .andExpect(jsonPath("$.content[0].motivo")
                        .value("Usuário não possui a permissão solicitada"));
    }

    @Test
    void deveListarTentativasPorEmailEResultado() throws Exception {
        TentativaAcesso tentativa = new TentativaAcesso(
                "maria@email.com",
                Permissao.DELETAR_USUARIO,
                false,
                "Usuário não possui a permissão solicitada",
                LocalDateTime.now());

        Page<TentativaAcesso> pagina = criarPagina(tentativa);

        when(auditoriaService.listarTentativasPorEmailEResultado(
                eq("maria@email.com"),
                eq(false),
                any(Pageable.class)
        )).thenReturn(pagina);

        mockMvc.perform(get("/auditoria/acessos")
                        .param("email", "maria@email.com")
                        .param("permitido", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].email").value("maria@email.com"))
                .andExpect(jsonPath("$.content[0].permissao").value("DELETAR_USUARIO"))
                .andExpect(jsonPath("$.content[0].acessoPermitido").value(false))
                .andExpect(jsonPath("$.content[0].motivo")
                        .value("Usuário não possui a permissão solicitada"));
    }

    private Page<TentativaAcesso> criarPagina(TentativaAcesso tentativa) {
        Pageable pageable = PageRequest.of(
                0,
                20,
                Sort.by(Sort.Direction.DESC, "dataHora"));

        return new PageImpl<>(
                List.of(tentativa),
                pageable,
                1);
    }
}