package br.com.gustavo.iam.identidade.adapter.out.security;

import br.com.gustavo.iam.identidade.application.IdentidadeAutenticada;
import br.com.gustavo.iam.shared.exception.IdentidadeAutenticadaInvalidaException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtIdentidadeAutenticadaAdapterTest {

    private JwtIdentidadeAutenticadaAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new JwtIdentidadeAutenticadaAdapter();
    }

    @Test
    void deveObterIdentidadeAutenticadaDoJwt() {
        Jwt jwt = new Jwt(
                "token",
                Instant.now(),
                Instant.now().plusSeconds(300),
                Map.of("alg", "none"),
                Map.of(
                        "sub", "sub-123",
                        "email", "gustavo@email.com",
                        "email_verified", true
                )
        );

        JwtAuthenticationToken authentication =
                new JwtAuthenticationToken(jwt);

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        IdentidadeAutenticada identidade =
                adapter.obterIdentidadeAutenticada();

        assertEquals("sub-123", identidade.subject());
        assertEquals("gustavo@email.com", identidade.email());
        assertTrue(identidade.emailVerificado());
    }

    @Test
    void deveLancarExcecaoQuandoNaoExistirJwtAutenticado() {
        SecurityContextHolder.clearContext();

        assertThrows(
                IdentidadeAutenticadaInvalidaException.class,
                () -> adapter.obterIdentidadeAutenticada()
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }
}