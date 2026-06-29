// Enum que representa as permissões possíveis dentro do sistema.
// Cada valor é uma ação que um usuário pode ou não executar.
// Usei enum porque essas permissões são fixas e evitei erros de digitação com Strings.

public enum Permissao {
    CRIAR_USUARIO,
    DELETAR_USUARIO,
    VER_RELATORIO,
    APROVAR_ACESSO,
    VER_PERFIL
}
//Permissões fixas no sistema