package br.com.gustavo.iam;

// Define os status possíveis de um usuário no sistema.
public enum StatusUsuario {

    // Usuário ativo pode seguir para as validações de acesso.
    ATIVO,

    // Usuário bloqueado não deve conseguir acessar o sistema.
    BLOQUEADO,

    // Usuário pendente ainda não está liberado para acessar o sistema.
    PENDENTE
}