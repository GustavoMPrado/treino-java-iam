package br.com.gustavo.iam;

public class VerificarAcessoRequest {

    private String email;
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
