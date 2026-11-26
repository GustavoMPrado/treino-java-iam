package br.com.gustavo.iam.shared.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MfaValidatorTest {

    private final OAuth2TokenValidator<Jwt> validator =
            new SecurityConfig().mfaValidator();

    @Test
    void deveAceitarTokenComSenhaEOtp() {
        Jwt jwt = criarToken(List.of("pwd", "otp"));

        assertFalse(validator.validate(jwt).hasErrors());
    }

    @Test
    void deveRejeitarTokenComApenasSenha() {
        Jwt jwt = criarToken(List.of("pwd"));

        assertTrue(validator.validate(jwt).hasErrors());
    }

    @Test
    void deveRejeitarTokenComApenasOtp() {
        Jwt jwt = criarToken(List.of("otp"));

        assertTrue(validator.validate(jwt).hasErrors());
    }

    @Test
    void deveRejeitarTokenComAmrVazio() {
        Jwt jwt = criarToken(List.of());

        assertTrue(validator.validate(jwt).hasErrors());
    }

    @Test
    void deveRejeitarTokenSemAmr() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .claim("sub", "usuario-teste")
                .build();

        assertTrue(validator.validate(jwt).hasErrors());
    }

    @Test
    void deveRejeitarTokenComAmrInvalido() {
        Jwt jwt = criarToken("pwd otp");

        assertTrue(validator.validate(jwt).hasErrors());
    }

    private Jwt criarToken(Object amr) {
        return Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .claim("sub", "usuario-teste")
                .claim("amr", amr)
                .build();
    }
}
