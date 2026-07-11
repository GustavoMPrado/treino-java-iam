package br.com.gustavo.iam;

// Exception usada quando o código de MFA é inválido
// ou quando não existe fluxo de MFA iniciado para o usuário.
public class MfaInvalidoException extends RuntimeException {

    public MfaInvalidoException() {
        super("Código de MFA inválido ou fluxo de MFA não iniciado.");
    }
}