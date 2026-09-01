package br.com.gustavo.iam.identidade.config;

import br.com.gustavo.iam.identidade.application.MfaHashService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Configuração responsavel por disponibilizar os componentes usados no fluxo de MFA.
@Configuration
public class MfaConfiguration {

    // Cria o serviço de hash usando o segredo fornecido pela configuração da aplicação.
    @Bean
    public MfaHashService mfaHashService(@Value("${mfa.secret}") String segredo) {
        return new MfaHashService(segredo);
    }
}