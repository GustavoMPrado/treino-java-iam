package br.com.gustavo.iam;

import br.com.gustavo.iam.auditoria.application.AuditoriaService;
import br.com.gustavo.iam.auditoria.application.port.out.AuditoriaRepositoryPort;
import br.com.gustavo.iam.auditoria.domain.TentativaAcesso;
import br.com.gustavo.iam.identidade.domain.Permissao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Testes unitários do AuditoriaService.
// O repository é mockado para manter os testes isolados da infraestrutura.
@ExtendWith(MockitoExtension.class)
class AuditoriaServiceTest {

    @Mock
    private AuditoriaRepositoryPort auditoriaRepository;

    private AuditoriaService auditoriaService;

    @BeforeEach
    void setUp() {
        auditoriaService = new AuditoriaService(auditoriaRepository);
    }

    @Test
    void deveRegistrarTentativaDeAcesso() {
        auditoriaService.registrarTentativa(
                "gustavo@email.com",
                Permissao.DELETAR_USUARIO,
                true,
                "Usuário possui permissão");

        verify(auditoriaRepository).salvar(any(TentativaAcesso.class));
    }

    @Test
    void deveListarTentativasPorEmail() {
        TentativaAcesso tentativa = new TentativaAcesso(
                "maria@email.com",
                Permissao.DELETAR_USUARIO,
                false,
                "Usuário não possui a permissão solicitada",
                LocalDateTime.now());

        when(auditoriaRepository.buscarPorEmail("maria@email.com"))
                .thenReturn(List.of(tentativa));

        Collection<TentativaAcesso> tentativas =
                auditoriaService.listarTentativasPorEmail("maria@email.com");

        assertEquals(1, tentativas.size());
    }

    @Test
    void deveListarTentativasPermitidas() {
        TentativaAcesso tentativa = new TentativaAcesso(
                "gustavo@email.com",
                Permissao.DELETAR_USUARIO,
                true,
                "Usuário possui permissão",
                LocalDateTime.now());

        when(auditoriaRepository.buscarPorResultado(true))
                .thenReturn(List.of(tentativa));

        Collection<TentativaAcesso> tentativas =
                auditoriaService.listarTentativasPorResultado(true);

        assertEquals(1, tentativas.size());
    }

    @Test
    void deveListarTentativasNegadas() {
        TentativaAcesso tentativa = new TentativaAcesso(
                "maria@email.com",
                Permissao.DELETAR_USUARIO,
                false,
                "Usuário não possui a permissão solicitada",
                LocalDateTime.now());

        when(auditoriaRepository.buscarPorResultado(false))
                .thenReturn(List.of(tentativa));

        Collection<TentativaAcesso> tentativas =
                auditoriaService.listarTentativasPorResultado(false);

        assertEquals(1, tentativas.size());
    }

    @Test
    void deveListarTentativasPorEmailEResultado() {
        TentativaAcesso tentativa = new TentativaAcesso(
                "maria@email.com",
                Permissao.DELETAR_USUARIO,
                false,
                "Usuário não possui a permissão solicitada",
                LocalDateTime.now());

        when(auditoriaRepository.buscarPorEmailEResultado(
                "maria@email.com",
                false
        )).thenReturn(List.of(tentativa));

        Collection<TentativaAcesso> tentativas =
                auditoriaService.listarTentativasPorEmailEResultado(
                        "maria@email.com",
                        false);

        assertEquals(1, tentativas.size());
    }
}
