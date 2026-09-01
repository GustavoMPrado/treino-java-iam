package br.com.gustavo.iam.identidade.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

// Representa um desafio de MFA associado a um usuário.
// Controla o código protegido, validade, tentativas e estado do desafio.
@Entity
@Table(name = "desafios_mfa")
public class DesafioMfa {

    // Quantidade máxima de tentativas inválidas permitidas.
    private static final int MAX_TENTATIVAS = 3;

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "codigo_hash", nullable = false)
    private String codigoHash;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "expira_em", nullable = false)
    private LocalDateTime expiraEm;

    @Column(nullable = false)
    private int tentativas;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusDesafioMfa status;

    // Construtor exigido pelo JPA.
    protected DesafioMfa() {
    }

    // Cria um novo desafio pendente para o usuário informado.
    public DesafioMfa(Usuario usuario, String codigoHash, LocalDateTime expiraEm) {
        this.id = UUID.randomUUID();
        this.usuario = usuario;
        this.codigoHash = codigoHash;
        this.criadoEm = LocalDateTime.now();
        this.expiraEm = expiraEm;
        this.tentativas = 0;
        this.status = StatusDesafioMfa.PENDENTE;
    }

    public UUID getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public String getCodigoHash() {
        return codigoHash;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public LocalDateTime getExpiraEm() {
        return expiraEm;
    }

    public int getTentativas() {
        return tentativas;
    }

    public StatusDesafioMfa getStatus() {
        return status;
    }

    // Verifica se o prazo de validade do desafio já terminou.
    public boolean estaExpirado() {
        return LocalDateTime.now().isAfter(expiraEm);
    }

    // Marca o desafio como expirado quando o prazo já terminou.
    // Apenas desafios pendentes podem passar para o estado expirado.
    public void expirar() {
        if (status == StatusDesafioMfa.PENDENTE && estaExpirado()) {
            status = StatusDesafioMfa.EXPIRADO;
        }
    }

    // Registra uma tentativa inválida enquanto o desafio estiver pendente.
    // Ao atingir o limite permitido, o desafio é bloqueado.
    public void registrarTentativaInvalida() {
        expirar();

        if (status != StatusDesafioMfa.PENDENTE) {
            return;
        }

        tentativas++;

        if (tentativas >= MAX_TENTATIVAS) {
            status = StatusDesafioMfa.BLOQUEADO;
        }
    }

    // Confirma o desafio quando ele ainda estiver pendente e dentro do prazo.
    // Desafios expirados, bloqueados ou já confirmados não podem ser confirmados.
    public void confirmar() {
        expirar();

        if (status == StatusDesafioMfa.PENDENTE) {
            status = StatusDesafioMfa.CONFIRMADO;
        }
    }

    // Substitui um desafio pendente quando um novo desafio é criado.
    // Desafios expirados ou já finalizados não podem ser substituídos.
    public void substituir() {
        expirar();

        if (status == StatusDesafioMfa.PENDENTE) {
            status = StatusDesafioMfa.SUBSTITUIDO;
        }
    }

    // Processa uma tentativa de confirmação do desafio.
    // Código válido confirma o desafio; código inválido registra uma tentativa.
    public void processarTentativa(boolean codigoValido) {
        expirar();

        if (status != StatusDesafioMfa.PENDENTE) {
            return;
        }

        if (codigoValido) {
            confirmar();
            return;
        }

        registrarTentativaInvalida();
    }
}