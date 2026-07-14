package br.com.gustavo.iam.auditoria.application;

import br.com.gustavo.iam.auditoria.domain.TentativaAcesso;
import br.com.gustavo.iam.identidade.domain.Permissao;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//Service responsável por registrar e listar tentativas de acesso.
//Por enquanto, os registros ficam em memória.

@Service
public class AuditoriaService {

    private final List<TentativaAcesso> tentativas = new ArrayList<>();

    public void registrarTentativa(String email, Permissao permissao, boolean acessoPermitido, String motivo) {
        TentativaAcesso tentativa = new TentativaAcesso(email, permissao, acessoPermitido, motivo, LocalDateTime.now());
        tentativas.add(tentativa);
    }

    public Collection<TentativaAcesso> listarTentativas() {
        return tentativas;
    }

    public Collection<TentativaAcesso> listarTentativasPorEmail(String email) {
        List<TentativaAcesso> tentativasDoUsuario = new ArrayList<>();

        for (TentativaAcesso tentativa : tentativas) {
            if (tentativa.getEmail().equalsIgnoreCase(email)) {
                tentativasDoUsuario.add(tentativa);
            }
        }

        return tentativasDoUsuario;
    }

    public Collection<TentativaAcesso> listarTentativasPorResultado(boolean acessoPermitido) {
        List<TentativaAcesso> tentativasFiltradas = new ArrayList<>();

        for (TentativaAcesso tentativa : tentativas) {
            if (tentativa.isAcessoPermitido() == acessoPermitido) {
                tentativasFiltradas.add(tentativa);
            }
        }

        return tentativasFiltradas;
    }

    public Collection<TentativaAcesso> listarTentativasPorEmailEResultado(String email, boolean acessoPermitido) {
        List<TentativaAcesso> tentativasFiltradas = new ArrayList<>();

        for (TentativaAcesso tentativa : tentativas) {
            boolean mesmoEmail = tentativa.getEmail().equalsIgnoreCase(email);
            boolean mesmoResultado = tentativa.isAcessoPermitido() == acessoPermitido;

            if (mesmoEmail && mesmoResultado) {
                tentativasFiltradas.add(tentativa);
            }
        }

        return tentativasFiltradas;
    }
}
