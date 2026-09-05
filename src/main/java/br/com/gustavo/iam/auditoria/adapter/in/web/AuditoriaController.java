package br.com.gustavo.iam.auditoria.adapter.in.web;

import br.com.gustavo.iam.auditoria.application.AuditoriaService;
import br.com.gustavo.iam.auditoria.domain.TentativaAcesso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// Controller responsável pelos endpoints de consulta da auditoria.
// Permite visualizar as tentativas de acesso registradas pelo sistema.
@RestController
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping("/auditoria/acessos")
    public Page<TentativaAcesso> listarTentativas(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Boolean permitido,
            @PageableDefault(
                    size = 20,
                    sort = "dataHora",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        if (email != null && permitido != null) {
            return auditoriaService.listarTentativasPorEmailEResultado(
                    email,
                    permitido,
                    pageable);
        }

        if (email != null) {
            return auditoriaService.listarTentativasPorEmail(
                    email,
                    pageable);
        }

        if (permitido != null) {
            return auditoriaService.listarTentativasPorResultado(
                    permitido,
                    pageable);
        }

        return auditoriaService.listarTentativas(pageable);
    }
}
