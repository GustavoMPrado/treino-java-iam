package br.com.gustavo.iam.identidade.adapter.out.security;

import br.com.gustavo.iam.identidade.application.IdentidadeAutenticada;
import br.com.gustavo.iam.identidade.application.port.out.IdentidadeAutenticadaPort;
import br.com.gustavo.iam.shared.exception.IdentidadeAutenticadaInvalidaException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class JwtIdentidadeAutenticadaAdapter implements IdentidadeAutenticadaPort {

    @Override
    public IdentidadeAutenticada obterIdentidadeAutenticada() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (!(authentication instanceof JwtAuthenticationToken jwtAuthenticationToken)) {
            throw new IdentidadeAutenticadaInvalidaException(
                    "Não existe identidade JWT autenticada."
            );
        }

        Jwt jwt = jwtAuthenticationToken.getToken();

        String subject = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        Boolean emailVerificado = jwt.getClaimAsBoolean("email_verified");

        return new IdentidadeAutenticada(
                subject,
                email,
                Boolean.TRUE.equals(emailVerificado)
        );
    }
}