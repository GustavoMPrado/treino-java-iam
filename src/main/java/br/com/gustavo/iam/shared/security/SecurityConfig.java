package br.com.gustavo.iam.shared.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

// Configuração central de segurança da aplicação.
@Configuration
public class SecurityConfig {

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(
                new KeycloakRealmRoleConverter());

        return converter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationConverter jwtAuthenticationConverter
    ) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/usuarios/me")
                        .authenticated()

                        .requestMatchers(HttpMethod.POST, "/acessos/verificar")
                        .authenticated()

                        .requestMatchers(HttpMethod.GET, "/usuarios")
                        .hasAnyRole("GESTOR", "ADMIN")

                        .requestMatchers(HttpMethod.GET, "/usuarios/{email}")
                        .hasAnyRole("GESTOR", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/usuarios")
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/usuarios/{email}/bloquear",
                                "/usuarios/{email}/ativar",
                                "/usuarios/{email}/marcar-pendente",
                                "/usuarios/{email}/mfa/desativar"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/usuarios/{email}/mfa/iniciar",
                                "/usuarios/{email}/mfa/confirmar"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/auditoria/acessos")
                        .hasAnyRole("GESTOR", "ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/rbac/roles",
                                "/rbac/permissoes"
                        )
                        .hasAnyRole("GESTOR", "ADMIN")

                        .anyRequest()
                        .denyAll()
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)));

        return http.build();
    }
}