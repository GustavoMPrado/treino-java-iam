package br.com.gustavo.iam.identidade.adapter.in.web.dto;

// DTO de resposta usado quando o fluxo de MFA é iniciado.
// Neste momento, o código é retornado apenas para simular o processo.
public class IniciarMfaResponse {

    private String email;
    private boolean mfaPendente;
    private String codigoSimulado;

    public IniciarMfaResponse(String email, boolean mfaPendente, String codigoSimulado) {
        this.email = email;
        this.mfaPendente = mfaPendente;
        this.codigoSimulado = codigoSimulado;
    }

    public String getEmail() {
        return email;
    }

    public boolean isMfaPendente() {
        return mfaPendente;
    }

    public String getCodigoSimulado() {
        return codigoSimulado;
    }
}