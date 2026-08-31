package br.com.gustavo.iam.identidade.application;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Base64;

// Service responsável por proteger e validar códigos de MFA usando HMAC-SHA256.
// O segredo usado na geração do hash será fornecido pela configuração da aplicação.
public class MfaHashService {

    private static final String ALGORITMO = "HmacSHA256";

    private final SecretKeySpec chaveSecreta;

    public MfaHashService(String segredo) {
        if (segredo == null || segredo.isBlank()) {
            throw new IllegalArgumentException("O segredo do MFA não pode ser vazio.");
        }

        this.chaveSecreta = new SecretKeySpec(
                segredo.getBytes(StandardCharsets.UTF_8),
                ALGORITMO
        );
    }

    // Gera o hash do código MFA usando o segredo configurado.
    public String gerarHash(String codigo) {
        byte[] hash = executarHmac(codigo);

        return Base64.getEncoder().encodeToString(hash);
    }

    // Compara o código informado com o hash armazenado.
    public boolean corresponde(String codigo, String hashEsperado) {
        if (hashEsperado == null || hashEsperado.isBlank()) {
            return false;
        }

        byte[] hashGerado = executarHmac(codigo);
        byte[] hashArmazenado = Base64.getDecoder().decode(hashEsperado);

        return MessageDigest.isEqual(hashGerado, hashArmazenado);
    }

    // Executa o HMAC-SHA256 usando a chave secreta configurada.
    private byte[] executarHmac(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("O código MFA não pode ser vazio.");
        }

        try {
            Mac mac = Mac.getInstance(ALGORITMO);
            mac.init(chaveSecreta);

            return mac.doFinal(codigo.getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Não foi possível gerar o hash do código MFA.", exception);
        }
    }
}