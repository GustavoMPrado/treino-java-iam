package br.com.gustavo.iam.identidade.application;

import br.com.gustavo.iam.identidade.application.port.out.IdentidadeAutenticadaPort;
import br.com.gustavo.iam.identidade.application.port.out.UsuarioRepositoryPort;
import br.com.gustavo.iam.identidade.domain.Role;
import br.com.gustavo.iam.identidade.domain.StatusUsuario;
import br.com.gustavo.iam.identidade.domain.Usuario;
import br.com.gustavo.iam.shared.exception.IdentidadeAutenticadaInvalidaException;
import br.com.gustavo.iam.shared.exception.UsuarioNaoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IdentidadeAutenticadaServiceTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepository;

    @Mock
    private IdentidadeAutenticadaPort identidadeAutenticadaPort;

    private IdentidadeAutenticadaService identidadeAutenticadaService;

    @BeforeEach
    void setUp() {
        identidadeAutenticadaService =
                new IdentidadeAutenticadaService(
                        usuarioRepository,
                        identidadeAutenticadaPort
                );
    }

    @Test
    void deveRetornarUsuarioQuandoSubjectJaEstiverVinculado() {
        Usuario usuario = new Usuario(
                "Gustavo",
                "gustavo@email.com",
                Role.USER,
                StatusUsuario.ATIVO
        );

        usuario.vincularKeycloakSubject("sub-123");

        when(usuarioRepository.buscarPorKeycloakSubject("sub-123"))
                .thenReturn(Optional.of(usuario));

        Usuario resultado = identidadeAutenticadaService.resolverUsuario(
                "sub-123",
                "gustavo@email.com",
                true
        );

        assertSame(usuario, resultado);

        verify(usuarioRepository, never()).buscarPorEmail(anyString());
        verify(usuarioRepository, never()).salvar(any(Usuario.class));
    }

    @Test
    void deveVincularSubjectAoUsuarioQuandoAindaNaoHouverVinculo() {
        Usuario usuario = new Usuario(
                "Gustavo",
                "gustavo@email.com",
                Role.USER,
                StatusUsuario.ATIVO
        );

        when(usuarioRepository.buscarPorKeycloakSubject("sub-123"))
                .thenReturn(Optional.empty());

        when(usuarioRepository.buscarPorEmail("gustavo@email.com"))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.salvar(usuario))
                .thenReturn(usuario);

        Usuario resultado = identidadeAutenticadaService.resolverUsuario(
                "sub-123",
                "gustavo@email.com",
                true
        );

        assertSame(usuario, resultado);
        assertEquals("sub-123", usuario.getKeycloakSubject());

        verify(usuarioRepository).buscarPorKeycloakSubject("sub-123");
        verify(usuarioRepository).buscarPorEmail("gustavo@email.com");
        verify(usuarioRepository).salvar(usuario);
    }
    @Test
    void deveLancarExcecaoQuandoSubjectNaoForInformado() {
        IdentidadeAutenticadaInvalidaException exception = assertThrows(
                IdentidadeAutenticadaInvalidaException.class,
                () -> identidadeAutenticadaService.resolverUsuario(
                        null,
                        "gustavo@email.com",
                        true
                )
        );

        assertEquals(
                "A identidade autenticada não possui subject.",
                exception.getMessage()
        );

        verify(usuarioRepository, never()).buscarPorKeycloakSubject(anyString());
        verify(usuarioRepository, never()).buscarPorEmail(anyString());
        verify(usuarioRepository, never()).salvar(any(Usuario.class));
    }

    @Test
    void deveLancarExcecaoQuandoEmailNaoForInformado() {
        when(usuarioRepository.buscarPorKeycloakSubject("sub-123"))
                .thenReturn(Optional.empty());

        IdentidadeAutenticadaInvalidaException exception = assertThrows(
                IdentidadeAutenticadaInvalidaException.class,
                () -> identidadeAutenticadaService.resolverUsuario(
                        "sub-123",
                        null,
                        true
                )
        );

        assertEquals(
                "A identidade autenticada não possui e-mail.",
                exception.getMessage()
        );

        verify(usuarioRepository).buscarPorKeycloakSubject("sub-123");
        verify(usuarioRepository, never()).buscarPorEmail(anyString());
        verify(usuarioRepository, never()).salvar(any(Usuario.class));
    }

    @Test
    void deveLancarExcecaoQuandoEmailNaoEstiverVerificado() {
        when(usuarioRepository.buscarPorKeycloakSubject("sub-123"))
                .thenReturn(Optional.empty());

        IdentidadeAutenticadaInvalidaException exception = assertThrows(
                IdentidadeAutenticadaInvalidaException.class,
                () -> identidadeAutenticadaService.resolverUsuario(
                        "sub-123",
                        "gustavo@email.com",
                        false
                )
        );

        assertEquals(
                "O e-mail da identidade autenticada não está verificado.",
                exception.getMessage()
        );

        verify(usuarioRepository).buscarPorKeycloakSubject("sub-123");
        verify(usuarioRepository, never()).buscarPorEmail(anyString());
        verify(usuarioRepository, never()).salvar(any(Usuario.class));
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioLocalNaoExistir() {
        when(usuarioRepository.buscarPorKeycloakSubject("sub-123"))
                .thenReturn(Optional.empty());

        when(usuarioRepository.buscarPorEmail("gustavo@email.com"))
                .thenReturn(Optional.empty());

        UsuarioNaoEncontradoException exception = assertThrows(
                UsuarioNaoEncontradoException.class,
                () -> identidadeAutenticadaService.resolverUsuario(
                        "sub-123",
                        "gustavo@email.com",
                        true
                )
        );

        assertEquals(
                "Usuário não encontrado com o e-mail: gustavo@email.com",
                exception.getMessage()
        );

        verify(usuarioRepository).buscarPorKeycloakSubject("sub-123");
        verify(usuarioRepository).buscarPorEmail("gustavo@email.com");
        verify(usuarioRepository, never()).salvar(any(Usuario.class));
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioJaEstiverVinculadoAOutroSubject() {
        Usuario usuario = new Usuario(
                "Gustavo",
                "gustavo@email.com",
                Role.USER,
                StatusUsuario.ATIVO
        );

        usuario.vincularKeycloakSubject("sub-antigo");

        when(usuarioRepository.buscarPorKeycloakSubject("sub-novo"))
                .thenReturn(Optional.empty());

        when(usuarioRepository.buscarPorEmail("gustavo@email.com"))
                .thenReturn(Optional.of(usuario));

        IdentidadeAutenticadaInvalidaException exception = assertThrows(
                IdentidadeAutenticadaInvalidaException.class,
                () -> identidadeAutenticadaService.resolverUsuario(
                        "sub-novo",
                        "gustavo@email.com",
                        true
                )
        );

        assertEquals(
                "O usuário já está vinculado a outra identidade do Keycloak.",
                exception.getMessage()
        );

        verify(usuarioRepository).buscarPorKeycloakSubject("sub-novo");
        verify(usuarioRepository).buscarPorEmail("gustavo@email.com");
        verify(usuarioRepository, never()).salvar(any(Usuario.class));
    }

    @Test
    void deveLancarExcecaoQuandoSubjectEstiverVazio() {
        IdentidadeAutenticadaInvalidaException exception = assertThrows(
                IdentidadeAutenticadaInvalidaException.class,
                () -> identidadeAutenticadaService.resolverUsuario(
                        "   ",
                        "gustavo@email.com",
                        true
                )
        );

        assertEquals(
                "A identidade autenticada não possui subject.",
                exception.getMessage()
        );

        verify(usuarioRepository, never()).buscarPorKeycloakSubject(anyString());
        verify(usuarioRepository, never()).buscarPorEmail(anyString());
        verify(usuarioRepository, never()).salvar(any(Usuario.class));
    }

    @Test
    void deveLancarExcecaoQuandoEmailEstiverVazio() {
        when(usuarioRepository.buscarPorKeycloakSubject("sub-123"))
                .thenReturn(Optional.empty());

        IdentidadeAutenticadaInvalidaException exception = assertThrows(
                IdentidadeAutenticadaInvalidaException.class,
                () -> identidadeAutenticadaService.resolverUsuario(
                        "sub-123",
                        "   ",
                        true
                )
        );

        assertEquals(
                "A identidade autenticada não possui e-mail.",
                exception.getMessage()
        );

        verify(usuarioRepository).buscarPorKeycloakSubject("sub-123");
        verify(usuarioRepository, never()).buscarPorEmail(anyString());
        verify(usuarioRepository, never()).salvar(any(Usuario.class));
    }

    @Test
    void deveResolverUsuarioAutenticado() {
        Usuario usuario = new Usuario(
                "Gustavo",
                "gustavo@email.com",
                Role.USER,
                StatusUsuario.ATIVO
        );

        usuario.vincularKeycloakSubject("sub-123");

        IdentidadeAutenticada identidade = new IdentidadeAutenticada(
                "sub-123",
                "gustavo@email.com",
                true
        );

        when(identidadeAutenticadaPort.obterIdentidadeAutenticada())
                .thenReturn(identidade);

        when(usuarioRepository.buscarPorKeycloakSubject("sub-123"))
                .thenReturn(Optional.of(usuario));

        Usuario resultado =
                identidadeAutenticadaService.resolverUsuarioAutenticado();

        assertSame(usuario, resultado);

        verify(identidadeAutenticadaPort).obterIdentidadeAutenticada();
        verify(usuarioRepository).buscarPorKeycloakSubject("sub-123");
    }
}


