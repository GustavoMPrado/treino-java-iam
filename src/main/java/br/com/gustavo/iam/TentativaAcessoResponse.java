package br.com.gustavo.iam;

import java.time.LocalDateTime;

// DTO de resposta para uma tentativa de acesso.
public class TentativaAcessoResponse {

    private String email;
    private Permissao permissao;
    private boolean acessoPermitido;
    private String motivo;
    private LocalDateTime dataHora;

    public TentativaAcessoResponse(
            String email,
            Permissao permissao,
            boolean acessoPermitido,
            String motivo,
            LocalDateTime dataHora
    ) {
        this.email = email;
        this.permissao = permissao;
        this.acessoPermitido = acessoPermitido;
        this.motivo = motivo;
        this.dataHora = dataHora;
    }

    public String getEmail() {
        return email;
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

    public LocalDateTime getDataHora() {
        return dataHora;
    }
}