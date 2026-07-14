package br.com.gustavo.iam.auditoria.domain;

import br.com.gustavo.iam.identidade.domain.Permissao;

import java.time.LocalDateTime;

// Classe que representa uma tentativa de acesso no sistema.
// Ela guarda quem tentou acessar, qual permissão foi solicitada,
// se o acesso foi permitido ou negado, o motivo e o momento da tentativa.

public class TentativaAcesso {

    private String email;
    private Permissao permissao;
    private boolean acessoPermitido;
    private String motivo;
    private LocalDateTime dataHora;

    public TentativaAcesso(String email, Permissao permissao, boolean acessoPermitido, String motivo, LocalDateTime dataHora) {
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
