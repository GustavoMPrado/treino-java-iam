package br.com.gustavo.iam;

// DTO de saída usado pra devolver os dados de um usuário pela API.
// Ele representa o JSON de resposta dos endpoints de usuários.
// Sem setters porque a resposta já nasce pronta no código e o Spring apenas lê os valores pelos getters.

public class UsuarioResponse {

    private String nome;
    private String email;
    private Role role;
    private boolean mfaAtivo;

    public UsuarioResponse(String nome, String email, Role role, boolean mfaAtivo) {
        this.nome = nome;
        this.email = email;
        this.role = role;
        this.mfaAtivo = mfaAtivo;
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
}