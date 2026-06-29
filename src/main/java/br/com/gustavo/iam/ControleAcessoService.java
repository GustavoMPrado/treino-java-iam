package br.com.gustavo.iam;

import org.springframework.stereotype.Service;

// Classe responsável por verificar se um usuário pode executar uma determinada ação.
// Ela consulta o UsuarioService para buscar o usuário pelo e-mail.
// Depois verifica se o MFA está ativo e se a role do usuário possui a permissão solicitada.

@Service
public class ControleAcessoService {

    private final UsuarioService usuarioService;

    public ControleAcessoService(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public boolean temPermissao(Usuario usuario, Permissao permissao) {
        return usuario.getRole().getPermissoes().contains(permissao);
    }

    public VerificarAcessoResponse verificarAcesso(VerificarAcessoRequest request) {
        Usuario usuario = usuarioService.buscarPorEmail(request.getEmail());

        if (usuario == null) {
            return new VerificarAcessoResponse(
                    null,
                    request.getPermissao(),
                    false,
                    "Usuário não encontrado");
        }

        if (!usuario.isMfaAtivo()) {
            return new VerificarAcessoResponse(
                    usuario.getNome(),
                    request.getPermissao(),
                    false,
                    "Usuário não pode acessar. MFA não está ativo");
        }

        boolean usuarioTemPermissao = temPermissao(usuario, request.getPermissao());

        if (usuarioTemPermissao) {
            return new VerificarAcessoResponse(
                    usuario.getNome(),
                    request.getPermissao(),
                    true,
                    "Usuário possui permissão e MFA ativo");
        }

        return new VerificarAcessoResponse(
                usuario.getNome(),
                request.getPermissao(),
                false,
                "Usuário não possui a permissão solicitada");
    }
}
