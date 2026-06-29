package br.com.gustavo.iam;

// DTO de entrada usado pra criar um novo usuário.
// Ele representa os dados que chegam no corpo da requisição POST /usuarios.
// Usei setters porque o Spring tem que preencher esse objeto com os dados do JSON recebido.

public class CriarUsuarioRequest {

    private String nome;
    private String email;
    private Role role;
    private boolean mfaAtivo;

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

    public boolean isMfaAtivo() {
        return mfaAtivo;
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

    public void setMfaAtivo(boolean mfaAtivo) {
        this.mfaAtivo = mfaAtivo;
    }
}