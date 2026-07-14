package br.com.gustavo.iam.acesso.application;

import br.com.gustavo.iam.auditoria.application.AuditoriaService;
import br.com.gustavo.iam.acesso.adapter.in.web.dto.VerificarAcessoRequest;
import br.com.gustavo.iam.acesso.adapter.in.web.dto.VerificarAcessoResponse;
import br.com.gustavo.iam.identidade.application.UsuarioService;
import br.com.gustavo.iam.identidade.domain.Permissao;
import br.com.gustavo.iam.identidade.domain.StatusUsuario;
import br.com.gustavo.iam.identidade.domain.Usuario;
import org.springframework.stereotype.Service;

// Classe responsável por verificar se um usuário pode executar uma determinada ação.
// Ela consulta o UsuarioService para buscar o usuário pelo e-mail.
// Depois verifica status, MFA e se a role do usuário possui a permissão solicitada.
// Cada tentativa de acesso também é registrada para auditoria.

@Service
public class ControleAcessoService {

    private final UsuarioService usuarioService;
    private final AuditoriaService auditoriaService;

    public ControleAcessoService(UsuarioService usuarioService, AuditoriaService auditoriaService) {
        this.usuarioService = usuarioService;
        this.auditoriaService = auditoriaService;
    }

    public boolean temPermissao(Usuario usuario, Permissao permissao) {
        return usuario.getRole().getPermissoes().contains(permissao);
    }

    public VerificarAcessoResponse verificarAcesso(VerificarAcessoRequest request) {
        Usuario usuario = usuarioService.buscarPorEmail(request.getEmail());

        if (usuario == null) {
            String motivo = "Usuário não encontrado";

            auditoriaService.registrarTentativa(
                    request.getEmail(),
                    request.getPermissao(),
                    false,
                    motivo
            );

            return new VerificarAcessoResponse(
                    null,
                    request.getPermissao(),
                    false,
                    motivo);
        }

        if (usuario.getStatus() == StatusUsuario.BLOQUEADO) {
            String motivo = "Usuário bloqueado";

            auditoriaService.registrarTentativa(
                    usuario.getEmail(),
                    request.getPermissao(),
                    false,
                    motivo
            );

            return new VerificarAcessoResponse(
                    usuario.getNome(),
                    request.getPermissao(),
                    false,
                    motivo);
        }

        if (usuario.getStatus() == StatusUsuario.PENDENTE) {
            String motivo = "Usuário pendente de ativação";

            auditoriaService.registrarTentativa(
                    usuario.getEmail(),
                    request.getPermissao(),
                    false,
                    motivo
            );

            return new VerificarAcessoResponse(
                    usuario.getNome(),
                    request.getPermissao(),
                    false,
                    motivo);
        }

        if (!usuario.isMfaAtivo()) {
            String motivo = "Usuário não pode acessar. MFA não está ativo";

            auditoriaService.registrarTentativa(
                    usuario.getEmail(),
                    request.getPermissao(),
                    false,
                    motivo
            );

            return new VerificarAcessoResponse(
                    usuario.getNome(),
                    request.getPermissao(),
                    false,
                    motivo);
        }

        boolean usuarioTemPermissao = temPermissao(usuario, request.getPermissao());

        if (usuarioTemPermissao) {
            String motivo = "Usuário possui permissão, status ativo e MFA ativo";

            auditoriaService.registrarTentativa(
                    usuario.getEmail(),
                    request.getPermissao(),
                    true,
                    motivo
            );

            return new VerificarAcessoResponse(
                    usuario.getNome(),
                    request.getPermissao(),
                    true,
                    motivo);
        }

        String motivo = "Usuário não possui a permissão solicitada";

        auditoriaService.registrarTentativa(
                usuario.getEmail(),
                request.getPermissao(),
                false,
                motivo
        );

        return new VerificarAcessoResponse(
                usuario.getNome(),
                request.getPermissao(),
                false,
                motivo);
    }
}
