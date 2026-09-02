package br.com.gustavo.iam.identidade.application;

import br.com.gustavo.iam.identidade.adapter.in.web.dto.ConfirmarMfaRequest;
import br.com.gustavo.iam.identidade.adapter.in.web.dto.IniciarMfaResponse;
import br.com.gustavo.iam.identidade.adapter.in.web.dto.UsuarioResponse;
import br.com.gustavo.iam.identidade.application.port.out.DesafioMfaRepositoryPort;
import br.com.gustavo.iam.identidade.domain.DesafioMfa;
import br.com.gustavo.iam.identidade.domain.StatusDesafioMfa;
import br.com.gustavo.iam.identidade.domain.Usuario;
import br.com.gustavo.iam.shared.exception.MfaInvalidoException;
import br.com.gustavo.iam.shared.exception.UsuarioNaoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

// Service responsável por orquestrar o fluxo de MFA.
// Coordena usuário, desafio persistido e validação segura do código.
@Service
public class MfaService {

    private final UsuarioService usuarioService;
    private final DesafioMfaRepositoryPort desafioMfaRepository;
    private final MfaHashService mfaHashService;

    private final SecureRandom secureRandom = new SecureRandom();

    public MfaService(
            UsuarioService usuarioService,
            DesafioMfaRepositoryPort desafioMfaRepository,
            MfaHashService mfaHashService) {

        this.usuarioService = usuarioService;
        this.desafioMfaRepository = desafioMfaRepository;
        this.mfaHashService = mfaHashService;
    }

    // Inicia um novo desafio de MFA para o usuário.
    // Se já existir um desafio pendente, ele é substituído.
    @Transactional
    public IniciarMfaResponse iniciarMfa(String email) {
        Usuario usuario = usuarioService.buscarPorEmail(email);

        if (usuario == null) {
            throw new UsuarioNaoEncontradoException(email);
        }

        Optional<DesafioMfa> desafioPendente =
                desafioMfaRepository.buscarPendentePorUsuario(usuario.getId());

        if (desafioPendente.isPresent()) {
            DesafioMfa desafioAnterior = desafioPendente.get();
            desafioAnterior.substituir();

            desafioMfaRepository.salvar(desafioAnterior);
        }

        String codigo = gerarCodigo();
        String codigoHash = mfaHashService.gerarHash(codigo);

        DesafioMfa novoDesafio = new DesafioMfa(
                usuario,
                codigoHash,
                LocalDateTime.now().plusMinutes(5)
        );

        desafioMfaRepository.salvar(novoDesafio);

        return new IniciarMfaResponse(
                usuario.getEmail(),
                true,
                codigo
        );
    }

    // Confirma um desafio de MFA pendente.
    @Transactional(noRollbackFor = MfaInvalidoException.class)
    public UsuarioResponse confirmarMfa(String email, ConfirmarMfaRequest request) {
        Usuario usuario = usuarioService.buscarPorEmail(email);

        if (usuario == null) {
            throw new UsuarioNaoEncontradoException(email);
        }

        Optional<DesafioMfa> desafioPendente =
                desafioMfaRepository.buscarPendentePorUsuario(usuario.getId());

        if (desafioPendente.isEmpty()) {
            throw new MfaInvalidoException();
        }

        DesafioMfa desafio = desafioPendente.get();

        boolean codigoValido = mfaHashService.corresponde(
                request.getCodigo(),
                desafio.getCodigoHash()
        );

        desafio.processarTentativa(codigoValido);

        desafioMfaRepository.salvar(desafio);

        if (desafio.getStatus() != StatusDesafioMfa.CONFIRMADO) {
            throw new MfaInvalidoException();
        }

        usuario.ativarMfa();

        return new UsuarioResponse(
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole(),
                usuario.isMfaAtivo(),
                usuario.getStatus()
        );
    }

    // Gera um código aleatório de seis dígitos.
    private String gerarCodigo() {
        int numero = secureRandom.nextInt(1_000_000);

        return String.format("%06d", numero);
    }
}