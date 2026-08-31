package br.com.gustavo.iam;

import br.com.gustavo.iam.identidade.application.MfaHashService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Testes unitários da proteção dos códigos de MFA.
// Validam a geração e a comparação de hashes usando HMAC-SHA256.
class MfaHashServiceTest {

    private static final String SEGREDO_TESTE = "segredo-apenas-para-testes";

    @Test
    void deveReconhecerCodigoCorrespondenteAoHash() {
        MfaHashService mfaHashService = new MfaHashService(SEGREDO_TESTE);

        String hash = mfaHashService.gerarHash("123456");

        assertTrue(mfaHashService.corresponde("123456", hash));
    }

    @Test
    void naoDeveAceitarCodigoDiferenteDoHash() {
        MfaHashService mfaHashService = new MfaHashService(SEGREDO_TESTE);

        String hash = mfaHashService.gerarHash("123456");

        assertFalse(mfaHashService.corresponde("000000", hash));
    }

    @Test
    void deveRetornarFalsoQuandoHashEsperadoForNulo() {
        MfaHashService mfaHashService = new MfaHashService(SEGREDO_TESTE);

        assertFalse(mfaHashService.corresponde("123456", null));
    }
}