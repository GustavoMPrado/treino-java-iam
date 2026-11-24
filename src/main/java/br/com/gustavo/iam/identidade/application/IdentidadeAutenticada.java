package br.com.gustavo.iam.identidade.application;

public record IdentidadeAutenticada(
        String subject,
        String email,
        boolean emailVerificado
) {
}