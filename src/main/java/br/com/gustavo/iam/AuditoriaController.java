package br.com.gustavo.iam;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public Collection<TentativaAcesso> listarTentativas() {
        return auditoriaService.listarTentativas();
    }

    @GetMapping("/auditoria/acessos/{email}")
    public Collection<TentativaAcesso> listarTentativasPorEmail(@PathVariable String email) {
        return auditoriaService.listarTentativasPorEmail(email);
    }
}
