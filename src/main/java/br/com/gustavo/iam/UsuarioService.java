package br.com.gustavo.iam;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// Service responsável por controlar os usuários em memória.
// Ele simula uma base de dados simples.
// Futuramente, essa responsabilidade pode ser substituída por um Repository.

@Service
public class UsuarioService {

    // Map usado pra guardar os usuários em memória.
    // Chave é o e-mail do usuário.
    // O valor é o objeto Usuario.
    private final Map<String, Usuario> usuarios = new HashMap<>();

    // Construtor do service.
    // Quando o Spring cria esse service, ele já cadastra alguns usuários iniciais pra teste.
    public UsuarioService() {
        cadastrarUsuarioInicial(new Usuario("Gustavo", "gustavo@email.com", Role.ADMIN, true, StatusUsuario.ATIVO));
        cadastrarUsuarioInicial(new Usuario("Maria", "maria@email.com", Role.GESTOR, true, StatusUsuario.ATIVO));
        cadastrarUsuarioInicial(new Usuario("João", "joao@email.com", Role.USER, false, StatusUsuario.ATIVO));
    }

    // Retorna todos os usuários cadastrados já convertidos pra UsuarioResponse.
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
    // Esse metodo continua retornando Usuario porque o ControleAcessoService precisa do objeto interno.
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

    // Metodo usado apenas para carregar usuários iniciais.
    // Ele evita repetir usuarios.put(...) dentro do construtor.
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