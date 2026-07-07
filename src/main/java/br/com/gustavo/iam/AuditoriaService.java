package br.com.gustavo.iam;

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
}
