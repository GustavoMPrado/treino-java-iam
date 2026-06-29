package br.com.gustavo.iam;

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
    public UsuarioResponse cadastrar(@RequestBody CriarUsuarioRequest request) {
        return usuarioService.cadastrar(request);
    }
}
