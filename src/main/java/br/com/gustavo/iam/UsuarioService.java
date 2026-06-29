package br.com.gustavo.iam;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// Service responsável por controlar os usuários em memória.
// Agora, ele simula uma base de dados simples.
// Futuramente, essa responsabilidade vai substituída por um Repository com banco de dados.

@Service
public class UsuarioService {

    // Map usado para guardar os usuários em memória.
    // A chave é o e-mail do usuário.
    // O valor é o objeto Usuario.
    private final Map<String, Usuario> usuarios = new HashMap<>();

    // Construtor do service.
    // Quando o Spring cria esse service, ele já cadastra alguns usuários iniciais pra teste.
    public UsuarioService() {
        cadastrar(new Usuario("Gustavo", "gustavo@email.com", Role.ADMIN, true));
        cadastrar(new Usuario("Maria", "maria@email.com", Role.GESTOR, true));
        cadastrar(new Usuario("João", "joao@email.com", Role.USER, false));
    }

    // Retorna todos os usuários cadastrados.
    // Como usei Map, os usuários ficam guardados nos valores do Map.
    public Collection<Usuario> listarTodos() {
        return usuarios.values();
    }

    // Busca um usuário por e-mail.
    // Como o e-mail é a chave do Map, consegui buscar direto sem percorrer uma lista.
    public Usuario buscarPorEmail(String email) {
        return usuarios.get(email);
    }

    // Cadastra um novo usuário em memória.
    // O e-mail é usado como chave para guardar o usuário no Map.
    public Usuario cadastrar(Usuario usuario) {
        usuarios.put(usuario.getEmail(), usuario);
        return usuario;
    }
}