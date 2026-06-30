package br.com.gustavo.iam;

// Classe que representa um usuário do sistema.
// Cada usuário possui nome, e-mail, uma role/perfil de acesso e a informação se o MFA está ativo.
// Neste exercício, o usuário não decide suas permissões diretamente.
// As permissões vêm da role associada a ele.

public class Usuario {

    private String nome;
    private String email;
    private Role role;
    private boolean mfaAtivo;
    private StatusUsuario status;

    public Usuario(String nome, String email, Role role, boolean mfaAtivo, StatusUsuario status) {
        this.nome = nome;
        this.email = email;
        this.role = role;
        this.mfaAtivo = mfaAtivo;
        this.status = status;
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

    public StatusUsuario getStatus() {
        return status;
    }
}
