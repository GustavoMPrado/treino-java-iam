package br.com.gustavo.iam.identidade.adapter.in.security;

import br.com.gustavo.iam.identidade.application.IdentidadeAutenticadaService;
import br.com.gustavo.iam.identidade.domain.Usuario;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class JwtIdentidadeAutenticadaAdapter {

    private final IdentidadeAutenticadaService identidadeAutenticadaService;

    public JwtIdentidadeAutenticadaAdapter(
            IdentidadeAutenticadaService identidadeAutenticadaService
    ) {
        this.identidadeAutenticadaService = identidadeAutenticadaService;
    }

    public Usuario resolverUsuario(Jwt jwt) {
        String subject = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        Boolean emailVerificado = jwt.getClaimAsBoolean("email_verified");

        return identidadeAutenticadaService.resolverUsuario(
                subject,
                email,
                Boolean.TRUE.equals(emailVerificado)
        );
    }
}