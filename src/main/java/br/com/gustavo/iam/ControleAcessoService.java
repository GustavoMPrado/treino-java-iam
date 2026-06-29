package br.com.gustavo.iam;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

// Classe responsável por verificar se um usuário pode executar uma determinada ação.
// Ela consulta as permissões da role do usuário e também verifica se o MFA está ativo.
// Neste exercício, ela simula uma regra simples de autorização dentro de um sistema IAM.


@Service
public class ControleAcessoService {

    private final List<Usuario> usuarios = new ArrayList<>();

    public ControleAcessoService() {
        usuarios.add(new Usuario("Gustavo", "gustavo@email.com", Role.ADMIN, true));
        usuarios.add(new Usuario("Maria", "maria@email.com", Role.GESTOR, true));
        usuarios.add(new Usuario("João", "joao@email.com", Role.USER, false));
    }

    public boolean temPermissao(Usuario usuario, Permissao permissao) {
        return usuario.getRole().getPermissoes().contains(permissao);
    }

    public VerificarAcessoResponse verificarAcesso(VerificarAcessoRequest request) {
        Usuario usuario = buscarUsuarioPorEmail(request.getEmail());

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
                    "Usuário não pode acessar. MFA não está ativo. ");

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

    private Usuario buscarUsuarioPorEmail(String email) {
        for (Usuario usuario : usuarios) {
            if (usuario.getEmail().equals(email)) {
                return usuario;
            }
        }

        return null;
    }
}
