package br.com.gustavo.iam.identidade.application.port.in;

import br.com.gustavo.iam.identidade.domain.Usuario;

public interface UsuarioAutenticadoUseCase {

    Usuario resolverUsuarioAutenticado();
}
