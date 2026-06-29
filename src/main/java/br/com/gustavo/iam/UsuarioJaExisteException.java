package br.com.gustavo.iam;

// Exceção usada quando alguém tenta cadastrar um usuário com e-mail já existente.
public class UsuarioJaExisteException extends RuntimeException {

    public UsuarioJaExisteException(String email) {
        super("Já existe um usuário cadastrado com o e-mail: " + email);
    }
}