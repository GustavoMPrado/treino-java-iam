package br.com.gustavo.iam.identidade.application;

import br.com.gustavo.iam.identidade.application.port.out.UsuarioRepositoryPort;
import br.com.gustavo.iam.identidade.domain.Usuario;
import br.com.gustavo.iam.shared.exception.IdentidadeAutenticadaInvalidaException;
import br.com.gustavo.iam.shared.exception.UsuarioNaoEncontradoException;
import org.springframework.stereotype.Service;
@Service
public class IdentidadeAutenticadaService {

    private final UsuarioRepositoryPort usuarioRepository;

    public IdentidadeAutenticadaService(UsuarioRepositoryPort usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario resolverUsuario(String subject, String email, boolean emailVerificado) {
        if (subject == null || subject.isBlank()) {
            throw new IdentidadeAutenticadaInvalidaException(
                    "A identidade autenticada não possui subject."
            );
        }

        Usuario usuarioVinculado = usuarioRepository.buscarPorKeycloakSubject(subject)
                .orElse(null);

        if (usuarioVinculado != null) {
            return usuarioVinculado;
        }

        if (email == null || email.isBlank()) {
            throw new IdentidadeAutenticadaInvalidaException(
                    "A identidade autenticada não possui e-mail."
            );
        }

        if (!emailVerificado) {
            throw new IdentidadeAutenticadaInvalidaException(
                    "O e-mail da identidade autenticada não está verificado."
            );
        }

        Usuario usuario = usuarioRepository.buscarPorEmail(email)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(email));

        if (usuario.getKeycloakSubject() != null) {
            throw new IdentidadeAutenticadaInvalidaException(
                    "O usuário já está vinculado a outra identidade do Keycloak."
            );
        }

        usuario.vincularKeycloakSubject(subject);

        return usuarioRepository.salvar(usuario);
    }
}
