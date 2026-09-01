package br.com.gustavo.iam.identidade.application;

import br.com.gustavo.iam.identidade.application.port.out.DesafioMfaRepositoryPort;
import org.springframework.stereotype.Service;

// Service responsável por orquestrar o fluxo de MFA.
// Coordena usuário, desafio persistido e validação segura do código.
@Service
public class MfaService {

    private final UsuarioService usuarioService;
    private final DesafioMfaRepositoryPort desafioMfaRepository;
    private final MfaHashService mfaHashService;

    public MfaService(
            UsuarioService usuarioService,
            DesafioMfaRepositoryPort desafioMfaRepository,
            MfaHashService mfaHashService) {

        this.usuarioService = usuarioService;
        this.desafioMfaRepository = desafioMfaRepository;
        this.mfaHashService = mfaHashService;
    }
}