package br.com.gustavo.iam;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


// DTO de entrada usado pra criar um novo usuário.
// Ele representa os dados que chegam no corpo da requisição POST /usuarios.
// Usei setters porque o Spring tem que preencher esse objeto com os dados do JSON recebido.
//As validações ajudam a garantir que a identidade seja cadastrada com os dados mínimos válidos

public class CriarUsuarioRequest {

    @NotBlank(message = "Nome é obrigatório.")
    private String nome;

    @NotBlank(message = "E-mail é obrigatório.")
    @Email(message = "E-mail deve ser válido. ")
    private String email;

    @NotNull(message = "Role é obrigatória.")
    private Role role;

    @NotNull(message = "MFA ativo é obrigatório.")
    private Boolean mfaAtivo;

    @NotNull(message = "Status é obrigatório.")
    private StatusUsuario status;

    public CriarUsuarioRequest() {
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public Boolean getMfaAtivo() {
        return mfaAtivo;
    }

    public StatusUsuario getStatus(){
        return status;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setMfaAtivo(Boolean mfaAtivo) {
        this.mfaAtivo = mfaAtivo;
    }

    public void setStatus(StatusUsuario status) {
        this.status = status;
    }
}