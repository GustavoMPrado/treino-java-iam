package br.com.gustavo.iam.auditoria.adapter.in.web;

import br.com.gustavo.iam.auditoria.application.AuditoriaService;
import br.com.gustavo.iam.auditoria.domain.TentativaAcesso;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

//Controller responsável pelos endpoints de consulta da auditoria
// Ele permite visualizar as tentativas de acesso registradas pelo sistema

@RestController
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping("/auditoria/acessos")
    public Collection<TentativaAcesso> listarTentativas(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Boolean permitido
    ) {
        if (email != null && permitido != null) {
            return auditoriaService.listarTentativasPorEmailEResultado(email, permitido);
        }

        if (email != null) {
            return auditoriaService.listarTentativasPorEmail(email);
        }

        if (permitido != null) {
            return auditoriaService.listarTentativasPorResultado(permitido);
        }

        return auditoriaService.listarTentativas();
    }

}
