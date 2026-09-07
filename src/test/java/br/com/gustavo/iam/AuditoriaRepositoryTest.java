package br.com.gustavo.iam;

import br.com.gustavo.iam.auditoria.adapter.out.persistence.AuditoriaRepositoryAdapter;
import br.com.gustavo.iam.auditoria.application.port.out.AuditoriaRepositoryPort;
import br.com.gustavo.iam.auditoria.domain.TentativaAcesso;
import br.com.gustavo.iam.identidade.domain.Permissao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({
        TestcontainersConfiguration.class,
        AuditoriaRepositoryAdapter.class
})
class AuditoriaRepositoryTest {

    @Autowired
    private AuditoriaRepositoryPort auditoriaRepository;

    @Test
    void deveSalvarEListarTentativa() {
        TentativaAcesso tentativa = new TentativaAcesso(
                "gustavo@email.com",
                Permissao.VER_PERFIL,
                true,
                "Usuário possui permissão",
                LocalDateTime.now());

        auditoriaRepository.salvar(tentativa);

        Page<TentativaAcesso> pagina =
                auditoriaRepository.listarTodas(
                        PageRequest.of(0, 20));

        assertEquals(1, pagina.getTotalElements());
        assertEquals("gustavo@email.com", pagina.getContent().get(0).getEmail());
        assertEquals(Permissao.VER_PERFIL, pagina.getContent().get(0).getPermissao());
        assertTrue(pagina.getContent().get(0).isAcessoPermitido());
    }

    @Test
    void deveBuscarTentativaPorEmailEResultado() {
        TentativaAcesso tentativa = new TentativaAcesso(
                "maria@email.com",
                Permissao.DELETAR_USUARIO,
                false,
                "Usuário não possui a permissão solicitada",
                LocalDateTime.now());

        auditoriaRepository.salvar(tentativa);

        Page<TentativaAcesso> pagina =
                auditoriaRepository.buscarPorEmailEResultado(
                        "maria@email.com",
                        false,
                        PageRequest.of(0, 20));

        assertEquals(1, pagina.getTotalElements());
        assertEquals("maria@email.com", pagina.getContent().get(0).getEmail());
        assertFalse(pagina.getContent().get(0).isAcessoPermitido());
    }
}