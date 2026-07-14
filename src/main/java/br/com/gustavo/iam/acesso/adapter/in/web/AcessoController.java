package br.com.gustavo.iam.acesso.adapter.in.web;

import br.com.gustavo.iam.acesso.adapter.in.web.dto.VerificarAcessoRequest;
import br.com.gustavo.iam.acesso.adapter.in.web.dto.VerificarAcessoResponse;
import br.com.gustavo.iam.acesso.application.ControleAcessoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// Controller responsável pelos endpoints de verificação de acesso.

@RestController
public class AcessoController {

    private final ControleAcessoService controleAcessoService;

    public AcessoController(ControleAcessoService controleAcessoService) {
        this.controleAcessoService = controleAcessoService;
    }

    @PostMapping("/acessos/verificar")
    public VerificarAcessoResponse verificarAcesso(@Valid @RequestBody VerificarAcessoRequest request) {
        return controleAcessoService.verificarAcesso(request);
    }
}