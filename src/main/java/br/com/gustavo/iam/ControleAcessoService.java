package br.com.gustavo.iam;

// Classe responsável por verificar se um usuário pode executar uma determinada ação.
// Ela consulta as permissões da role do usuário e também verifica se o MFA está ativo.
// Neste exercício, ela simula uma regra simples de autorização dentro de um sistema IAM.

public class ControleAcessoService {

    public boolean temPermissao(Usuario usuario, Permissao permissao){
        return usuario.getRole().getPermissoes().contains(permissao);
    }

    public void verificarAcesso(Usuario usuario, Permissao permissao){
        if(!usuario.isMfaAtivo()){
            System.out.println(usuario.getNome() + " não pode acessar. MFA não está ativo.");
            return;
        }

        if(temPermissao(usuario, permissao)){
            System.out.println(usuario.getNome() + " tem permissão para: " + permissao);
        }else {
            System.out.println(usuario.getNome() + " Não tem permissão para: " + permissao);
        }
    }
}
