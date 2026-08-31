package br.com.gustavo.iam.identidade.application;

import br.com.gustavo.iam.identidade.adapter.in.web.dto.ConfirmarMfaRequest;
import br.com.gustavo.iam.identidade.adapter.in.web.dto.CriarUsuarioRequest;
import br.com.gustavo.iam.identidade.adapter.in.web.dto.IniciarMfaResponse;
import br.com.gustavo.iam.identidade.adapter.in.web.dto.UsuarioResponse;
import br.com.gustavo.iam.identidade.application.port.out.UsuarioRepositoryPort;
import br.com.gustavo.iam.identidade.domain.Role;
import br.com.gustavo.iam.identidade.domain.StatusUsuario;
import br.com.gustavo.iam.identidade.domain.Usuario;
import br.com.gustavo.iam.shared.exception.MfaInvalidoException;
import br.com.gustavo.iam.shared.exception.UsuarioJaExisteException;
import br.com.gustavo.iam.shared.exception.UsuarioNaoEncontradoException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// Service responsável pelas regras de negócio relacionadas aos usuários.
// A persistência é acessada por meio de uma porta, mantendo a camada de aplicação desacoplada da infraestrutura.

@Service
public class UsuarioService {

    // Estrutura temporária mantida durante a migração da persistência em memória para o banco.
    // Chave é o e-mail do usuário.
    // O valor é o objeto Usuario.
    private final Map<String, Usuario> usuarios = new HashMap<>();

    private final UsuarioRepositoryPort usuarioRepository;

    // Construtor com injeção da porta de persistência.
    // Os usuários iniciais ainda são carregados em memória durante esta etapa de migração.
    public UsuarioService(UsuarioRepositoryPort usuarioRepository) {
        this.usuarioRepository = usuarioRepository;

        cadastrarUsuarioInicial(new Usuario("Gustavo", "gustavo@email.com", Role.ADMIN, true, StatusUsuario.ATIVO));
        cadastrarUsuarioInicial(new Usuario("Maria", "maria@email.com", Role.GESTOR, true, StatusUsuario.ATIVO));
        cadastrarUsuarioInicial(new Usuario("João", "joao@email.com", Role.USER, false, StatusUsuario.ATIVO));
    }

    // Retorna todos os usuários cadastrados convertidos para UsuarioResponse.
    public Collection<UsuarioResponse> listarTodos() {
        Collection<UsuarioResponse> responses = new ArrayList<>();

        for (Usuario usuario : usuarios.values()) {
            UsuarioResponse response = converterParaResponse(usuario);
            responses.add(response);
        }

        return responses;
    }

    // Busca um usuário pelo e-mail e devolve como UsuarioResponse.
    // Se o usuário não existir, lança uma exceção para a API responder 404.
    public UsuarioResponse buscarResponsePorEmail(String email) {
        Usuario usuario = buscarPorEmail(email);

        if (usuario == null) {
            throw new UsuarioNaoEncontradoException(email);
        }

        return converterParaResponse(usuario);
    }

    // Busca um usuário pelo e-mail.
    // Retorna o objeto Usuario usado internamente pelo controle de acesso.
    public Usuario buscarPorEmail(String email) {
        return usuarios.get(email);
    }

    // Cadastra um novo usuário a partir do DTO CriarUsuarioRequest.
    // Depois devolve os dados cadastrados como UsuarioResponse.
    public UsuarioResponse cadastrar(CriarUsuarioRequest request) {
        if (usuarios.containsKey(request.getEmail())) {
            throw new UsuarioJaExisteException(request.getEmail());
        }

        Usuario usuario = new Usuario(
                request.getNome(),
                request.getEmail(),
                request.getRole(),
                request.getMfaAtivo(),
                request.getStatus()
        );

        usuarios.put(usuario.getEmail(), usuario);

        return converterParaResponse(usuario);
    }

    // Bloqueia uma identidade existente.
    // Se o usuário não existir, lança exceção para a API responder 404.
    public UsuarioResponse bloquearUsuario(String email) {
        Usuario usuario = buscarPorEmail(email);

        if (usuario == null) {
            throw new UsuarioNaoEncontradoException(email);
        }

        usuario.bloquear();

        return converterParaResponse(usuario);
    }

    // Ativa uma identidade existente.
    // Essa ação pode ser usada após validação ou liberação administrativa.
    public UsuarioResponse ativarUsuario(String email) {
        Usuario usuario = buscarPorEmail(email);

        if (usuario == null) {
            throw new UsuarioNaoEncontradoException(email);
        }

        usuario.ativar();

        return converterParaResponse(usuario);
    }

    // Marca uma identidade como pendente.
    // Pode representar uma conta aguardando validação ou aprovação.
    public UsuarioResponse marcarUsuarioComoPendente(String email) {
        Usuario usuario = buscarPorEmail(email);

        if (usuario == null) {
            throw new UsuarioNaoEncontradoException(email);
        }

        usuario.marcarComoPendente();

        return converterParaResponse(usuario);
    }

    // Inicia o fluxo simulado de MFA para uma identidade existente.
    // O MFA ainda não fica ativo; ele fica pendente até a confirmação do código.
    public IniciarMfaResponse iniciarMfa(String email) {
        Usuario usuario = buscarPorEmail(email);

        if (usuario == null) {
            throw new UsuarioNaoEncontradoException(email);
        }

        String codigoSimulado = gerarCodigoMfaSimulado();

        usuario.iniciarMfa(codigoSimulado);

        return new IniciarMfaResponse(
                usuario.getEmail(),
                usuario.isMfaPendente(),
                usuario.getCodigoMfaSimulado());
    }

    // Confirma o MFA usando o código informado.
    // Se o código estiver correto, o MFA passa a ficar ativo.
    public UsuarioResponse confirmarMfa(String email, ConfirmarMfaRequest request) {
        Usuario usuario = buscarPorEmail(email);

        if (usuario == null) {
            throw new UsuarioNaoEncontradoException(email);
        }

        boolean mfaConfirmado = usuario.confirmarMfa(request.getCodigo());

        if (!mfaConfirmado) {
            throw new MfaInvalidoException();
        }

        return converterParaResponse(usuario);
    }

    // Desativa o MFA de uma identidade existente.
    // Também limpa qualquer fluxo pendente de MFA.
    public UsuarioResponse desativarMfa(String email) {
        Usuario usuario = buscarPorEmail(email);

        if (usuario == null) {
            throw new UsuarioNaoEncontradoException(email);
        }

        usuario.desativarMfa();

        return converterParaResponse(usuario);
    }

    // Gera um código fixo apenas para simular o fluxo de MFA.
    // Futuramente, isso pode ser substituído por TOTP, e-mail, SMS ou provedor externo.
    private String gerarCodigoMfaSimulado() {
        return "123456";
    }

    // Método temporário usado para carregar os usuários iniciais em memória.
    // Será removido quando a migração para persistência estiver concluída.
    private void cadastrarUsuarioInicial(Usuario usuario) {

        usuarios.put(usuario.getEmail(), usuario);
    }

    // Converte o objeto interno Usuario para o DTO de saída UsuarioResponse.
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