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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
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
        Pageable pageable = PageRequest.of(0, 20);

        TentativaAcesso tentativa = new TentativaAcesso(
                "maria@email.com",
                Permissao.DELETAR_USUARIO,
                false,
                "Usuário não possui a permissão solicitada",
                LocalDateTime.now());

        Page<TentativaAcesso> pagina =
                new PageImpl<>(List.of(tentativa), pageable, 1);

        when(auditoriaRepository.buscarPorEmail(
                "maria@email.com",
                pageable
        )).thenReturn(pagina);

        Page<TentativaAcesso> tentativas =
                auditoriaService.listarTentativasPorEmail(
                        "maria@email.com",
                        pageable);

        assertEquals(1, tentativas.getTotalElements());
    }

    @Test
    void deveListarTentativasPermitidas() {
        Pageable pageable = PageRequest.of(0, 20);

        TentativaAcesso tentativa = new TentativaAcesso(
                "gustavo@email.com",
                Permissao.DELETAR_USUARIO,
                true,
                "Usuário possui permissão",
                LocalDateTime.now());

        Page<TentativaAcesso> pagina =
                new PageImpl<>(List.of(tentativa), pageable, 1);

        when(auditoriaRepository.buscarPorResultado(
                true,
                pageable
        )).thenReturn(pagina);

        Page<TentativaAcesso> tentativas =
                auditoriaService.listarTentativasPorResultado(
                        true,
                        pageable);

        assertEquals(1, tentativas.getTotalElements());
    }

    @Test
    void deveListarTentativasNegadas() {
        Pageable pageable = PageRequest.of(0, 20);

        TentativaAcesso tentativa = new TentativaAcesso(
                "maria@email.com",
                Permissao.DELETAR_USUARIO,
                false,
                "Usuário não possui a permissão solicitada",
                LocalDateTime.now());

        Page<TentativaAcesso> pagina =
                new PageImpl<>(List.of(tentativa), pageable, 1);

        when(auditoriaRepository.buscarPorResultado(
                false,
                pageable
        )).thenReturn(pagina);

        Page<TentativaAcesso> tentativas =
                auditoriaService.listarTentativasPorResultado(
                        false,
                        pageable);

        assertEquals(1, tentativas.getTotalElements());
    }

    @Test
    void deveListarTentativasPorEmailEResultado() {
        Pageable pageable = PageRequest.of(0, 20);

        TentativaAcesso tentativa = new TentativaAcesso(
                "maria@email.com",
                Permissao.DELETAR_USUARIO,
                false,
                "Usuário não possui a permissão solicitada",
                LocalDateTime.now());

        Page<TentativaAcesso> pagina =
                new PageImpl<>(List.of(tentativa), pageable, 1);

        when(auditoriaRepository.buscarPorEmailEResultado(
                "maria@email.com",
                false,
                pageable
        )).thenReturn(pagina);

        Page<TentativaAcesso> tentativas =
                auditoriaService.listarTentativasPorEmailEResultado(
                        "maria@email.com",
                        false,
                        pageable);

        assertEquals(1, tentativas.getTotalElements());
    }
}
