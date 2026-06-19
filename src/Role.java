import java.util.List;

// Enum que representa os perfis de acesso do sistema.
// Cada role agrupa permissões diferentes.
// Exemplo: ADMIN terá mais permissões que USER.
// Quando eu crio uma role como ADMIN, eu passo uma lista de permissões.
// O construtor recebe essa lista e guarda dentro do atributo permissoes.

public enum Role {
    ADMIN(List.of(
            Permissao.CRIAR_USUARIO,
            Permissao.DELETAR_USUARIO,
            Permissao.VER_RELATORIO,
            Permissao.APROVAR_ACESSO,
            Permissao.VER_PERFIL)),

    GESTOR(List.of(
            Permissao.VER_RELATORIO,
            Permissao.APROVAR_ACESSO,
            Permissao.VER_PERFIL)),

    USER(List.of(
            Permissao.VER_PERFIL));

    private final List<Permissao> permissoes;

    Role(List<Permissao> permissoes){
        this.permissoes = permissoes;
    }

    public List<Permissao> getPermissoes() {
        return permissoes;
    }
}
