package br.ueg.eventos.dominio.inscricao;

import br.ueg.eventos.dominio.comum.IdAtividade;
import br.ueg.eventos.dominio.comum.IdEvento;
import br.ueg.eventos.dominio.comum.IdUsuario;
import br.ueg.eventos.dominio.comum.ValorInvalido;
import java.time.Instant;
import java.util.Optional;

/**
 * Vínculo do participante com um evento e, quando a política permite, com uma atividade.
 * As transições são atômicas: toda validação ocorre antes de reservar, liberar vaga ou
 * mudar a situação (RF-14, RF-15, RN-06).
 */
public class Inscricao {

    private final IdInscricao id;
    private final IdUsuario participanteId;
    private final IdEvento eventoId;
    private final IdAtividade atividadeId;
    private final PoliticaDeInscricao politica;
    private final Instant momento;
    private SituacaoDaInscricao situacao;

    public Inscricao(
            IdInscricao id,
            IdUsuario participanteId,
            IdEvento eventoId,
            IdAtividade atividadeId,
            PoliticaDeInscricao politica,
            Instant momento) {
        this.id = exigirValor(id, "O identificador da inscrição não pode ser nulo.");
        this.participanteId = exigirValor(participanteId, "A inscrição exige o participante.");
        this.eventoId = exigirValor(eventoId, "A inscrição exige o evento.");
        this.politica = exigirValor(politica, "A inscrição exige uma política de inscrição.");
        this.momento = exigirValor(momento, "A inscrição exige o instante do pedido.");
        this.politica.validarAlvo(atividadeId);
        this.atividadeId = atividadeId;
        this.situacao = SituacaoDaInscricao.PENDENTE;
    }

    /**
     * Traz de volta uma inscrição já gravada, na situação em que ela estava.
     *
     * <h2>Por que não "andar" o agregado até o estado gravado</h2>
     *
     * <p>O construtor sempre cria PENDENTE, e está certo: nenhuma inscrição nasce confirmada.
     * Mas chamar {@code confirmar} na releitura exigiria o {@code ControleDeVagas} <b>daquele
     * momento</b> — que o banco não guarda, e que teria de ser reconstruído antes, num
     * problema circular: as vagas se reconstroem a partir das inscrições confirmadas.</p>
     *
     * <p>Pior: reconfirmar reexecutaria a regra de vagas hoje, sobre um fato do passado. Uma
     * inscrição confirmada quando havia vaga seria recusada na releitura se o limite tivesse
     * sido reduzido depois — o sistema perderia dados por reaplicar regra retroativamente.</p>
     *
     * <p>A política, em compensação, <b>continua</b> validando o alvo: reconstituir uma
     * inscrição POR_ATIVIDADE sem atividade é recusado, porque isso seria dado corrompido, e
     * não um fato do passado.</p>
     *
     * <p>Uso previsto: exclusivamente adaptadores de {@code InscricaoRepository}.</p>
     */
    public static Inscricao reconstituir(
            IdInscricao id,
            IdUsuario participanteId,
            IdEvento eventoId,
            IdAtividade atividadeId,
            PoliticaDeInscricao politica,
            Instant momento,
            SituacaoDaInscricao situacao) {
        if (situacao == null) {
            throw new ValorInvalido("A reconstituição da inscrição exige a situação gravada.");
        }
        Inscricao inscricao = new Inscricao(id, participanteId, eventoId, atividadeId, politica, momento);
        inscricao.situacao = situacao;
        return inscricao;
    }

    /**
     * Reserva a vaga e confirma a inscrição. Repetir a mesma requisição é idempotente:
     * a vaga já reservada não é consumida outra vez e a situação permanece confirmada.
     */
    public void confirmar(ControleDeVagas vagas) {
        exigirValor(vagas, "A confirmação exige o controle de vagas.");
        if (situacao == SituacaoDaInscricao.CANCELADA) {
            throw new InscricaoInvalida("Uma inscrição cancelada não pode ser confirmada novamente.");
        }
        vagas.reservar(id);
        situacao = SituacaoDaInscricao.CONFIRMADA;
    }

    /**
     * Cancela a inscrição dentro do prazo e devolve a vaga. Situação e prazo são
     * validados antes de qualquer mutação, de modo que uma recusa preserva o estado.
     */
    public void cancelar(Instant agora, PrazoDeCancelamento prazo, ControleDeVagas vagas) {
        exigirValor(agora, "O cancelamento exige o instante do pedido.");
        exigirValor(prazo, "O cancelamento exige o prazo configurado.");
        exigirValor(vagas, "O cancelamento exige o controle de vagas.");
        if (situacao != SituacaoDaInscricao.CONFIRMADA) {
            throw new CancelamentoInvalido("Somente uma inscrição confirmada pode ser cancelada.");
        }
        if (!prazo.permite(agora)) {
            throw new CancelamentoInvalido("O prazo de cancelamento foi encerrado.");
        }
        vagas.liberar(id);
        situacao = SituacaoDaInscricao.CANCELADA;
    }

    public IdInscricao id() {
        return id;
    }

    public IdUsuario participanteId() {
        return participanteId;
    }

    public IdEvento eventoId() {
        return eventoId;
    }

    public Optional<IdAtividade> atividadeId() {
        return Optional.ofNullable(atividadeId);
    }

    public PoliticaDeInscricao politica() {
        return politica;
    }

    public Instant momento() {
        return momento;
    }

    public SituacaoDaInscricao situacao() {
        return situacao;
    }

    /**
     * Fronteira consumida pela agenda de Israell (RF-16) e pela elegibilidade de
     * avaliação de Igor (RF-26): somente inscrição confirmada é considerada válida.
     */
    public boolean estaConfirmada() {
        return situacao == SituacaoDaInscricao.CONFIRMADA;
    }

    private static <T> T exigirValor(T valor, String mensagem) {
        if (valor == null) {
            throw new ValorInvalido(mensagem);
        }
        return valor;
    }
}
