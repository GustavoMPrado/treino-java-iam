package br.com.gustavo.iam.identidade.adapter.in.web;

import br.com.gustavo.iam.identidade.adapter.in.web.dto.ConfirmarMfaRequest;
import br.com.gustavo.iam.identidade.adapter.in.web.dto.CriarUsuarioRequest;
import br.com.gustavo.iam.identidade.adapter.in.web.dto.IniciarMfaResponse;
import br.com.gustavo.iam.identidade.adapter.in.web.dto.UsuarioResponse;
import br.com.gustavo.iam.identidade.application.UsuarioService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

// Controller responsável pelos endpoints de usuários.
// Ele recebe requisições HTTP e chama o UsuarioService.
// Usei DTOs para separar o que chega na API e o que sai como resposta.

@RestController
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/usuarios")
    public Collection<UsuarioResponse> listarTodos() {
        return usuarioService.listarTodos();
    }

    @GetMapping("/usuarios/{email}")
    public UsuarioResponse buscarPorEmail(@PathVariable String email) {
        return usuarioService.buscarResponsePorEmail(email);
    }

    @PostMapping("/usuarios")
    public UsuarioResponse cadastrar(@Valid @RequestBody CriarUsuarioRequest request) {
        return usuarioService.cadastrar(request);
    }

    @PatchMapping("/usuarios/{email}/bloquear")
    public UsuarioResponse bloquearUsuario(@PathVariable String email) {
        return usuarioService.bloquearUsuario(email);
    }

    @PatchMapping("/usuarios/{email}/ativar")
    public UsuarioResponse ativarUsuario(@PathVariable String email) {
        return usuarioService.ativarUsuario(email);
    }

    @PatchMapping("/usuarios/{email}/marcar-pendente")
    public UsuarioResponse marcarUsuarioComoPendente(@PathVariable String email) {
        return usuarioService.marcarUsuarioComoPendente(email);
    }

    @PostMapping("/usuarios/{email}/mfa/iniciar")
    public IniciarMfaResponse iniciarMfa(@PathVariable String email) {
        return usuarioService.iniciarMfa(email);
    }

    @PostMapping("/usuarios/{email}/mfa/confirmar")
    public UsuarioResponse confirmarMfa(
            @PathVariable String email,
            @Valid @RequestBody ConfirmarMfaRequest request
    ) {
        return usuarioService.confirmarMfa(email, request);
    }

    @PatchMapping("/usuarios/{email}/mfa/desativar")
    public UsuarioResponse desativarMfa(@PathVariable String email) {
        return usuarioService.desativarMfa(email);
    }
}
