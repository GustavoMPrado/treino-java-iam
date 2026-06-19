// Classe principal do programa.
// Aqui cries alguns usuários de exemplo e testei se eles têm acesso a determinadas permissões.
// O objetivo é simular, de forma simples, um controle de acesso baseado em usuário, role, permissão e MFA.

public class Main {

    public static void main(String[] args) {

        ControleAcessoService service = new ControleAcessoService();

        Usuario admin = new Usuario("Gustavo", "gustavo@email.com", Role.ADMIN, true);

        Usuario gestor = new Usuario("Maria", "maria@email.com", Role.GESTOR, true);

        Usuario usuarioComum = new Usuario("João", "joao@email.com", Role.USER, false);

        service.verificarAcesso(admin, Permissao.DELETAR_USUARIO);

        service.verificarAcesso(gestor, Permissao.DELETAR_USUARIO);

        service.verificarAcesso(usuarioComum, Permissao.VER_PERFIL);
    }
}
