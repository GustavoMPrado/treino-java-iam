package br.com.gustavo.iam.identidade.adapter.in.security;

import br.com.gustavo.iam.identidade.application.IdentidadeAutenticadaService;
import br.com.gustavo.iam.identidade.domain.Role;
import br.com.gustavo.iam.identidade.domain.StatusUsuario;
import br.com.gustavo.iam.identidade.domain.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtIdentidadeAutenticadaAdapterTest {

    @Mock
    private IdentidadeAutenticadaService identidadeAutenticadaService;

    private JwtIdentidadeAutenticadaAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new JwtIdentidadeAutenticadaAdapter(
                identidadeAutenticadaService
        );
    }

    @Test
    void deveExtrairClaimsDoJwtEDelegarParaService() {
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

        Usuario usuario = new Usuario(
                "Gustavo",
                "gustavo@email.com",
                Role.USER,
                true,
                StatusUsuario.ATIVO
        );

        when(identidadeAutenticadaService.resolverUsuario(
                "sub-123",
                "gustavo@email.com",
                true
        )).thenReturn(usuario);

        Usuario resultado = adapter.resolverUsuario(jwt);

        assertSame(usuario, resultado);

        verify(identidadeAutenticadaService).resolverUsuario(
                "sub-123",
                "gustavo@email.com",
                true
        );
    }

    @Test
    void deveConsiderarEmailNaoVerificadoQuandoClaimNaoExistir() {
        Jwt jwt = new Jwt(
                "token",
                Instant.now(),
                Instant.now().plusSeconds(300),
                Map.of("alg", "none"),
                Map.of(
                        "sub", "sub-123",
                        "email", "gustavo@email.com"
                )
        );

        Usuario usuario = new Usuario(
                "Gustavo",
                "gustavo@email.com",
                Role.USER,
                true,
                StatusUsuario.ATIVO
        );

        when(identidadeAutenticadaService.resolverUsuario(
                "sub-123",
                "gustavo@email.com",
                false
        )).thenReturn(usuario);

        Usuario resultado = adapter.resolverUsuario(jwt);

        assertSame(usuario, resultado);

        verify(identidadeAutenticadaService).resolverUsuario(
                "sub-123",
                "gustavo@email.com",
                false
        );
    }
}