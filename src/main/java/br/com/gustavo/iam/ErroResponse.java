package br.com.gustavo.iam;

// DTO usado para devolver mensagens de erro da API.
public class ErroResponse {

    private String mensagem;

    public ErroResponse(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getMensagem() {
        return mensagem;
    }
}