package br.com.gustavo.iam.auditoria.domain;

import br.com.gustavo.iam.identidade.domain.Usuario;
import br.com.gustavo.iam.identidade.domain.Permissao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


import java.time.LocalDateTime;
import java.util.UUID;

// Entidade de auditoria que registra uma tentativa de acesso.
// Armazena a identidade envolvida, a permissão solicitada,
// o resultado da decisão, o motivo e o momento da tentativa.

@Entity
@Table(name = "tentativas_acesso")
public class TentativaAcesso {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Permissao permissao;

    @Column(name = "acesso_permitido", nullable = false)
    private boolean acessoPermitido;

    @Column(nullable = false)
    private String motivo;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    protected TentativaAcesso() {
    }

    public TentativaAcesso(String email, Permissao permissao, boolean acessoPermitido, String motivo, LocalDateTime dataHora) {
        this.id = UUID.randomUUID();
        this.email = email;
        this.permissao = permissao;
        this.acessoPermitido = acessoPermitido;
        this.motivo = motivo;
        this.dataHora = dataHora;

    }

    public UUID getId() {
        return id;
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
