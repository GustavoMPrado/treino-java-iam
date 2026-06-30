package br.com.gustavo.iam;

//Exceção usada qdo a Api procura usuário que não existe

public class UsuarioNaoEncontradoException extends RuntimeException {

    public UsuarioNaoEncontradoException(String email) {
        super("Usuário não encontrado com o e-mail: " + email);
    }
}
