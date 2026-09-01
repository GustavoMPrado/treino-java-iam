package br.com.gustavo.iam;

import br.com.gustavo.iam.identidade.domain.DesafioMfa;
import br.com.gustavo.iam.identidade.domain.Role;
import br.com.gustavo.iam.identidade.domain.StatusDesafioMfa;
import br.com.gustavo.iam.identidade.domain.StatusUsuario;
import br.com.gustavo.iam.identidade.domain.Usuario;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Testes unitários das regras de domínio do desafio de MFA.
// Validam expiração, tentativas inválidas, bloqueio e processamento da confirmação.
class DesafioMfaTest {

    @Test
    void deveIdentificarDesafioExpirado() {
        Usuario usuario = criarUsuario();

        DesafioMfa desafio = new DesafioMfa(
                usuario,
                "hash-simulado",
                LocalDateTime.now().minusMinutes(1));

        assertTrue(desafio.estaExpirado());
    }

    @Test
    void deveManterDesafioValidoAntesDaExpiracao() {
        Usuario usuario = criarUsuario();

        DesafioMfa desafio = new DesafioMfa(
                usuario,
                "hash-simulado",
                LocalDateTime.now().plusMinutes(5));

        assertFalse(desafio.estaExpirado());
    }

    @Test
    void deveAlterarStatusParaExpiradoQuandoPrazoTerminou() {
        Usuario usuario = criarUsuario();

        DesafioMfa desafio = new DesafioMfa(
                usuario,
                "hash-simulado",
                LocalDateTime.now().minusMinutes(1));

        desafio.expirar();

        assertEquals(StatusDesafioMfa.EXPIRADO, desafio.getStatus());
    }

    @Test
    void deveRegistrarTentativaInvalida() {
        Usuario usuario = criarUsuario();

        DesafioMfa desafio = new DesafioMfa(
                usuario,
                "hash-simulado",
                LocalDateTime.now().plusMinutes(5));

        desafio.registrarTentativaInvalida();

        assertEquals(1, desafio.getTentativas());
        assertEquals(StatusDesafioMfa.PENDENTE, desafio.getStatus());
    }

    @Test
    void deveBloquearDesafioAposTresTentativasInvalidas() {
        Usuario usuario = criarUsuario();

        DesafioMfa desafio = new DesafioMfa(
                usuario,
                "hash-simulado",
                LocalDateTime.now().plusMinutes(5));

        desafio.registrarTentativaInvalida();
        desafio.registrarTentativaInvalida();
        desafio.registrarTentativaInvalida();

        assertEquals(3, desafio.getTentativas());
        assertEquals(StatusDesafioMfa.BLOQUEADO, desafio.getStatus());
    }

    @Test
    void deveConfirmarDesafioValido() {
        Usuario usuario = criarUsuario();

        DesafioMfa desafio = new DesafioMfa(
                usuario,
                "hash-simulado",
                LocalDateTime.now().plusMinutes(5));

        desafio.confirmar();

        assertEquals(StatusDesafioMfa.CONFIRMADO, desafio.getStatus());
    }

    @Test
    void naoDeveConfirmarDesafioExpirado() {
        Usuario usuario = criarUsuario();

        DesafioMfa desafio = new DesafioMfa(
                usuario,
                "hash-simulado",
                LocalDateTime.now().minusMinutes(1));

        desafio.confirmar();

        assertEquals(StatusDesafioMfa.EXPIRADO, desafio.getStatus());
    }

    @Test
    void deveConfirmarDesafioQuandoCodigoForValido() {
        Usuario usuario = criarUsuario();

        DesafioMfa desafio = new DesafioMfa(
                usuario,
                "hash-simulado",
                LocalDateTime.now().plusMinutes(5));

        desafio.processarTentativa(true);

        assertEquals(StatusDesafioMfa.CONFIRMADO, desafio.getStatus());
        assertEquals(0, desafio.getTentativas());
    }

    @Test
    void deveRegistrarTentativaQuandoCodigoForInvalido() {
        Usuario usuario = criarUsuario();

        DesafioMfa desafio = new DesafioMfa(
                usuario,
                "hash-simulado",
                LocalDateTime.now().plusMinutes(5));

        desafio.processarTentativa(false);

        assertEquals(StatusDesafioMfa.PENDENTE, desafio.getStatus());
        assertEquals(1, desafio.getTentativas());
    }

    @Test
    void naoDeveRegistrarTentativaQuandoDesafioEstiverExpirado() {
        Usuario usuario = criarUsuario();

        DesafioMfa desafio = new DesafioMfa(
                usuario,
                "hash-simulado",
                LocalDateTime.now().minusMinutes(1));

        desafio.registrarTentativaInvalida();

        assertEquals(0, desafio.getTentativas());
        assertEquals(StatusDesafioMfa.EXPIRADO, desafio.getStatus());
    }

    // Cria um usuário usado apenas nos cenários de teste do desafio MFA.
    private Usuario criarUsuario() {
        return new Usuario(
                "João",
                "joao@email.com",
                Role.USER,
                false,
                StatusUsuario.ATIVO);
    }
}