package br.com.gustavo.iam;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

// Configuração de teste responsável por criar um PostgreSQL descartável.
// O container será usado apenas pelos testes de integração.
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    // Cria uma instância isolada do PostgreSQL para os testes.
    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>("postgres:16-alpine");
    }
}