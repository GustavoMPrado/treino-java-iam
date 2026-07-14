package br.com.gustavo.iam.auditoria.adapter.in.web.dto;

import java.util.List;

// DTO de resposta para uma página de tentativas de acesso.
public class AuditoriaPaginadaResponse {

    private List<TentativaAcessoResponse> conteudo;
    private int pagina;
    private int tamanho;
    private long totalElementos;
    private int totalPaginas;

    public AuditoriaPaginadaResponse(
            List<TentativaAcessoResponse> conteudo,
            int pagina,
            int tamanho,
            long totalElementos,
            int totalPaginas
    ) {
        this.conteudo = conteudo;
        this.pagina = pagina;
        this.tamanho = tamanho;
        this.totalElementos = totalElementos;
        this.totalPaginas = totalPaginas;
    }

    public List<TentativaAcessoResponse> getConteudo() {
        return conteudo;
    }

    public int getPagina() {
        return pagina;
    }

    public int getTamanho() {
        return tamanho;
    }

    public long getTotalElementos() {
        return totalElementos;
    }

    public int getTotalPaginas() {
        return totalPaginas;
    }
}