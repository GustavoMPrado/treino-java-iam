package br.com.gustavo.iam;

import br.com.gustavo.iam.shared.security.KeycloakRealmRoleConverter;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class KeycloakRealmRoleConverterTest {

    private final KeycloakRealmRoleConverter converter =
            new KeycloakRealmRoleConverter();

    @Test
    void deveConverterRealmRoleUserParaAuthority() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("realm_access", Map.of(
                        "roles", List.of("USER")
                ))
                .build();

        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        assertTrue(authorities.contains(
                new SimpleGrantedAuthority("ROLE_USER")
        ));
    }

    @Test
    void deveRetornarVazioQuandoJwtNaoPossuirRealmRoles() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", "teste.mfa")
                .build();

        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        assertTrue(authorities.isEmpty());
    }
}