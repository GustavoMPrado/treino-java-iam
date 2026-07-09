package br.com.gustavo.iam;

import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuditoriaServiceTest {

    @Test
    void deveRegistrarTentativaDeAcesso() {
        AuditoriaService auditoriaService = new AuditoriaService();

        auditoriaService.registrarTentativa(
                "gustavo@email.com",
                Permissao.DELETAR_USUARIO,
                true,
                "Usuário possui permissão");
        Collection<TentativaAcesso> tentativas = auditoriaService.listarTentativas();

        assertEquals(1, tentativas.size());
    }

    @Test
    void deveListarTentativasPorEmail() {
        AuditoriaService auditoriaService = new AuditoriaService();

        auditoriaService.registrarTentativa(
                "gustavo@email.com",
                Permissao.DELETAR_USUARIO,
                true,
                "Usuário possui permissão"
        );

        auditoriaService.registrarTentativa(
                "maria@email.com",
                Permissao.DELETAR_USUARIO,
                false,
                "Usuário não possui a permissão solicitada"
        );

        Collection<TentativaAcesso> tentativas = auditoriaService.listarTentativasPorEmail("maria@email.com");

        assertEquals(1, tentativas.size());
    }

    @Test
    void deveListarTentativasPermitidas() {
        AuditoriaService auditoriaService = new AuditoriaService();

        auditoriaService.registrarTentativa(
                "gustavo@email.com",
                Permissao.DELETAR_USUARIO,
                true,
                "Usuário possui permissão"
        );

        auditoriaService.registrarTentativa(
                "maria@email.com",
                Permissao.DELETAR_USUARIO,
                false,
                "Usuário não possui a permissão solicitada"
        );

        Collection<TentativaAcesso> tentativas = auditoriaService.listarTentativasPorResultado(true);

        assertEquals(1, tentativas.size());
    }

    @Test
    void deveListarTentativasNegadas() {
        AuditoriaService auditoriaService = new AuditoriaService();

        auditoriaService.registrarTentativa(
                "gustavo@email.com",
                Permissao.DELETAR_USUARIO,
                true,
                "Usuário possui permissão"
        );

        auditoriaService.registrarTentativa(
                "maria@email.com",
                Permissao.DELETAR_USUARIO,
                false,
                "Usuário não possui a permissão solicitada"
        );

        Collection<TentativaAcesso> tentativas = auditoriaService.listarTentativasPorResultado(false);

        assertEquals(1, tentativas.size());
    }

    @Test
    void deveListarTentativasPorEmailEResultado() {
        AuditoriaService auditoriaService = new AuditoriaService();

        auditoriaService.registrarTentativa(
                "gustavo@email.com",
                Permissao.DELETAR_USUARIO,
                true,
                "Usuário possui permissão"
        );

        auditoriaService.registrarTentativa(
                "maria@email.com",
                Permissao.DELETAR_USUARIO,
                false,
                "Usuário não possui a permissão solicitada"
        );

        auditoriaService.registrarTentativa(
                "maria@email.com",
                Permissao.VER_RELATORIO,
                true,
                "Usuário possui permissão"
        );

        Collection<TentativaAcesso> tentativas =
                auditoriaService.listarTentativasPorEmailEResultado("maria@email.com", false);

        assertEquals(1, tentativas.size());
    }
}
