package br.com.gustavo.iam.shared.exception;

public class IdentidadeAutenticadaInvalidaException extends RuntimeException {

    public IdentidadeAutenticadaInvalidaException(String mensagem) {
        super(mensagem);
    }
}
