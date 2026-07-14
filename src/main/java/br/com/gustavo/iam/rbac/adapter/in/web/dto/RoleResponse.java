package br.com.gustavo.iam.rbac.adapter.in.web.dto;

import br.com.gustavo.iam.identidade.domain.Permissao;

import java.util.List;

// DTO de resposta para exibir uma role e suas permissões.
public class RoleResponse {

    private String nome;
    private List<Permissao> permissoes;

    public RoleResponse(String nome, List<Permissao> permissoes) {
        this.nome = nome;
        this.permissoes = permissoes;
    }

    public String getNome() {
        return nome;
    }

    public List<Permissao> getPermissoes() {
        return permissoes;
    }
}