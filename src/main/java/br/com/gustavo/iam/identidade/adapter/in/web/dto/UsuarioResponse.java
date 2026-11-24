package br.com.gustavo.iam.identidade.adapter.in.web.dto;

// DTO de saída usado pra devolver os dados de um usuário pela API
// Representa o JSON de resposta dos endpoints de usuários
// Pode ser criado diretamente ou convertido a partir da entidade Usuario.
// Não tem setters porque a resposta é imutável após a criação.

import br.com.gustavo.iam.identidade.domain.Role;
import br.com.gustavo.iam.identidade.domain.StatusUsuario;
import br.com.gustavo.iam.identidade.domain.Usuario;

public class UsuarioResponse {

    private String nome;
    private String email;
    private Role role;
    private boolean mfaAtivo;
    private StatusUsuario status;

    public UsuarioResponse(String nome, String email, Role role, boolean mfaAtivo, StatusUsuario status) {
        this.nome = nome;
        this.email = email;
        this.role = role;
        this.mfaAtivo = mfaAtivo;
        this.status = status;
    }

    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole(),
                usuario.isMfaAtivo(),
                usuario.getStatus()
        );
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