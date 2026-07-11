package br.com.gustavo.iam;

import jakarta.validation.constraints.NotBlank;

// DTO de entrada usado para confirmar o MFA.
// O usuário informa o código recebido no fluxo simulado.
public class ConfirmarMfaRequest {

    @NotBlank
    private String codigo;

    public ConfirmarMfaRequest() {
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}