package br.com.gustavo.iam.identidade.domain;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

// Entidade representa um usuário do sistema.
// A role define as permissões, enquanto o status e o MFA participam das decisões de acesso.


@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "mfa_ativo", nullable = false)
    private boolean mfaAtivo;

    @Transient
    private boolean mfaPendente;

    @Transient
    private String codigoMfaSimulado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusUsuario status;

    protected Usuario() {
    }

    public Usuario(String nome, String email, Role role, boolean mfaAtivo, StatusUsuario status) {
        this.id = UUID.randomUUID();
        this.nome = nome;
        this.email = email;
        this.role = role;
        this.mfaAtivo = mfaAtivo;
        this.status = status;
    }

    public UUID getId() {
        return id;
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

    public boolean isMfaPendente() {

        return mfaPendente;
    }

    public String getCodigoMfaSimulado() {

        return codigoMfaSimulado;
    }

    public StatusUsuario getStatus() {

        return status;
    }

    public void bloquear() {

        this.status = StatusUsuario.BLOQUEADO;
    }

    public void ativar() {

        this.status = StatusUsuario.ATIVO;
    }

    public void marcarComoPendente() {

        this.status = StatusUsuario.PENDENTE;
    }


    public void iniciarMfa(String codigoMfaSimulado) {
        this.mfaPendente = true;
        this.codigoMfaSimulado = codigoMfaSimulado;
    }

    public boolean confirmarMfa(String codigoInformado) {
        if (!mfaPendente) {
            return false;
        }

        if (!codigoMfaSimulado.equals(codigoInformado)) {
            return false;
        }

        this.mfaAtivo = true;
        this.mfaPendente = false;
        this.codigoMfaSimulado = null;

        return true;
    }

    public void desativarMfa() {
        this.mfaAtivo = false;
        this.mfaPendente = false;
        this.codigoMfaSimulado = null;
    }
}
