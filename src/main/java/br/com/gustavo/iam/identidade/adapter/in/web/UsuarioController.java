package br.com.gustavo.iam.identidade.adapter.in.web;

import br.com.gustavo.iam.identidade.adapter.in.web.dto.CriarUsuarioRequest;
import br.com.gustavo.iam.identidade.adapter.in.web.dto.UsuarioResponse;
import br.com.gustavo.iam.identidade.application.UsuarioService;
import br.com.gustavo.iam.identidade.application.port.in.UsuarioAutenticadoUseCase;
import br.com.gustavo.iam.identidade.domain.Usuario;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

// Controller responsável pelos endpoints de usuários.
// Ele recebe requisições HTTP e chama os services da aplicação.
// Usa DTOs para separar o que chega na API e o que sai como resposta.

@RestController
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioAutenticadoUseCase usuarioAutenticadoUseCase;
    public UsuarioController(
            UsuarioService usuarioService,
            UsuarioAutenticadoUseCase usuarioAutenticadoUseCase) {
        this.usuarioService = usuarioService;
        this.usuarioAutenticadoUseCase = usuarioAutenticadoUseCase;
    }

    @GetMapping("/usuarios")
    public Collection<UsuarioResponse> listarTodos() {
        return usuarioService.listarTodos();
    }

    @GetMapping("/usuarios/me")
    public UsuarioResponse buscarUsuarioAutenticado() {
        Usuario usuario = usuarioAutenticadoUseCase.resolverUsuarioAutenticado();

        return UsuarioResponse.from(usuario);
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
}