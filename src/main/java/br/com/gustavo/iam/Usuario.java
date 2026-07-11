package br.com.gustavo.iam;

// Classe que representa um usuário do sistema.
// Cada usuário possui nome, e-mail, uma role/perfil de acesso e informações de MFA.
// Neste exercício, o usuário não decide suas permissões diretamente.
// As permissões vêm da role associada a ele.
public class Usuario {

    private String nome;
    private String email;
    private Role role;
    private boolean mfaAtivo;
    private boolean mfaPendente;
    private String codigoMfaSimulado;
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
