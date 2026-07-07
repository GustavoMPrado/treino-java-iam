package br.com.gustavo.iam;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// DTO de entrada usado para verificar se um usuário pode executar uma permissão.
// Ele representa os dados que chegam no corpo da requisição POST /acessos/verificar.
// As validações garantem que a decisão de acesso receba email e permissão válidos.
public class VerificarAcessoRequest {

    @NotBlank(message = "Email é obrigatório.")
    @Email(message = "Email deve ser válido.")
    private String email;

    @NotNull(message = "Permissão é obrigatória.")
    private Permissao permissao;

    public VerificarAcessoRequest() {
    }

    public VerificarAcessoRequest(String email, Permissao permissao) {
        this.email = email;
        this.permissao = permissao;
    }

    public String getEmail() {
        return email;
    }

    public Permissao getPermissao() {
        return permissao;
    }

    public void setPermissao(Permissao permissao) {
        this.permissao = permissao;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
