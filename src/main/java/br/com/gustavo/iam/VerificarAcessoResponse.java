package br.com.gustavo.iam;

public class VerificarAcessoResponse {

    private String usuario;
    private Permissao permissao;
    private boolean acessoPermitido;
    private String motivo;

    public VerificarAcessoResponse(String usuario, Permissao permissao, boolean acessoPermitido, String motivo) {
        this.usuario = usuario;
        this.permissao = permissao;
        this.acessoPermitido = acessoPermitido;
        this.motivo = motivo;
    }

    public String getUsuario() {
        return usuario;
    }

    public Permissao getPermissao() {
        return permissao;
    }

    public boolean isAcessoPermitido() {
        return acessoPermitido;
    }

    public String getMotivo() {
        return motivo;
    }
}
