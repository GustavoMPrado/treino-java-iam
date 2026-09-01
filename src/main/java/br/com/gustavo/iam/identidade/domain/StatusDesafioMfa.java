package br.com.gustavo.iam.identidade.domain;

// Define os possíveis estados de um desafio de MFA.
public enum StatusDesafioMfa {

    // Desafio criado e aguardando confirmação.
    PENDENTE,

    // Código validado com sucesso.
    CONFIRMADO,

    // Prazo de validade do desafio encerrado.
    EXPIRADO,

    // Limite de tentativas inválidas atingido.
    BLOQUEADO,

    // Desafio invalidado porque um novo desafio foi criado para o mesmo usuário.
    SUBSTITUIDO
}