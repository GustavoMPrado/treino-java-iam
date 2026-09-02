package br.com.gustavo.iam.identidade.application;

import br.com.gustavo.iam.identidade.adapter.in.web.dto.CriarUsuarioRequest;
import br.com.gustavo.iam.identidade.adapter.in.web.dto.UsuarioResponse;
import br.com.gustavo.iam.identidade.application.port.out.UsuarioRepositoryPort;
import br.com.gustavo.iam.identidade.domain.Usuario;
import br.com.gustavo.iam.shared.exception.UsuarioJaExisteException;
import br.com.gustavo.iam.shared.exception.UsuarioNaoEncontradoException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

// Service responsável pelas regras de negócio relacionadas aos usuários.
// A persistência é acessada por meio de uma porta, mantendo a camada de aplicação desacoplada da infraestrutura.
@Service
public class UsuarioService {

    private final UsuarioRepositoryPort usuarioRepository;

    public UsuarioService(UsuarioRepositoryPort usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Retorna todos os usuários cadastrados convertidos para UsuarioResponse.
    public Collection<UsuarioResponse> listarTodos() {
        Collection<Usuario> usuarios = usuarioRepository.listarTodos();
        Collection<UsuarioResponse> responses = new ArrayList<>();

        for (Usuario usuario : usuarios) {
            responses.add(converterParaResponse(usuario));
        }

        return responses;
    }

    // Busca um usuário pelo e-mail e devolve como UsuarioResponse.
    public UsuarioResponse buscarResponsePorEmail(String email) {
        Usuario usuario = buscarPorEmail(email);

        if (usuario == null) {
            throw new UsuarioNaoEncontradoException(email);
        }

        return converterParaResponse(usuario);
    }

    // Busca um usuário pelo e-mail.
    // Retorna null quando a identidade não existe.
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.buscarPorEmail(email)
                .orElse(null);
    }

    // Cadastra um novo usuário.
    public UsuarioResponse cadastrar(CriarUsuarioRequest request) {
        if (usuarioRepository.existePorEmail(request.getEmail())) {
            throw new UsuarioJaExisteException(request.getEmail());
        }

        Usuario usuario = new Usuario(
                request.getNome(),
                request.getEmail(),
                request.getRole(),
                request.getMfaAtivo(),
                request.getStatus()
        );

        Usuario usuarioSalvo = usuarioRepository.salvar(usuario);

        return converterParaResponse(usuarioSalvo);
    }

    // Bloqueia uma identidade existente.
    public UsuarioResponse bloquearUsuario(String email) {
        Usuario usuario = buscarPorEmail(email);

        if (usuario == null) {
            throw new UsuarioNaoEncontradoException(email);
        }

        usuario.bloquear();

        Usuario usuarioSalvo = usuarioRepository.salvar(usuario);

        return converterParaResponse(usuarioSalvo);
    }

    // Ativa uma identidade existente.
    public UsuarioResponse ativarUsuario(String email) {
        Usuario usuario = buscarPorEmail(email);

        if (usuario == null) {
            throw new UsuarioNaoEncontradoException(email);
        }

        usuario.ativar();

        Usuario usuarioSalvo = usuarioRepository.salvar(usuario);

        return converterParaResponse(usuarioSalvo);
    }

    // Marca uma identidade como pendente.
    public UsuarioResponse marcarUsuarioComoPendente(String email) {
        Usuario usuario = buscarPorEmail(email);

        if (usuario == null) {
            throw new UsuarioNaoEncontradoException(email);
        }

        usuario.marcarComoPendente();

        Usuario usuarioSalvo = usuarioRepository.salvar(usuario);

        return converterParaResponse(usuarioSalvo);
    }

    // Desativa o MFA de uma identidade existente.
    public UsuarioResponse desativarMfa(String email) {
        Usuario usuario = buscarPorEmail(email);

        if (usuario == null) {
            throw new UsuarioNaoEncontradoException(email);
        }

        usuario.desativarMfa();

        Usuario usuarioSalvo = usuarioRepository.salvar(usuario);

        return converterParaResponse(usuarioSalvo);
    }

    // Converte o objeto interno Usuario para o DTO de saída.
    private UsuarioResponse converterParaResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole(),
                usuario.isMfaAtivo(),
                usuario.getStatus()
        );
    }
}