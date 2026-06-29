package br.com.gustavo.iam;

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
    public VerificarAcessoResponse verificarAcesso(@RequestBody VerificarAcessoRequest request) {
        return controleAcessoService.verificarAcesso(request);
    }
}