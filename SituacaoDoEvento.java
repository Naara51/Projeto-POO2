package br.ueg.eventos.dominio.evento;

import java.util.Arrays;
import java.util.List;

/**
 * O ciclo de vida do evento — e, em cada etapa, se ela é visível ao público.
 *
 * <h2>Por que a visibilidade mora aqui</h2>
 *
 * <p>RF-10 pede que o site liste os eventos disponíveis. A regra "publicado e encerrado
 * aparecem; rascunho não" precisa ser conhecida em três lugares:</p>
 *
 * <ul>
 *   <li>{@code Evento.estaVisivelPublicamente()}, para responder sobre um evento;</li>
 *   <li>o adaptador em memória, que filtra uma coleção;</li>
 *   <li>o adaptador JDBC, que precisa de uma cláusula {@code WHERE situacao IN (...)}.</li>
 * </ul>
 *
 * <p>Se cada um escrevesse a sua, o dia em que "encerrado" deixasse de aparecer no site
 * exigiria lembrar dos três — e o esquecido continuaria compilando e respondendo errado.
 * Com {@link #visivelPublicamente()}, o agregado delega e o adaptador JDBC <b>deriva</b> a
 * lista do {@link #publicas()}, em vez de digitá-la.</p>
 */
public enum SituacaoDoEvento {

    /** Em preparação. Não aparece para o público, e tudo nele ainda pode mudar. */
    RASCUNHO(false, true, true),

    /**
     * Aberto ao público (RF-10).
     *
     * <p>Texto ainda se corrige; data, local e modalidade não. Ver {@link #aceitaMudarRealizacao()}.</p>
     */
    PUBLICADO(true, true, false),

    /**
     * Já aconteceu, mas permanece consultável.
     *
     * <p>Continua visível de propósito: quem participou precisa consultar a programação,
     * a própria frequência e responder avaliação depois do fim (RF-27).</p>
     */
    ENCERRADO(true, false, false);

    private final boolean visivelPublicamente;
    private final boolean aceitaCorrigirTexto;
    private final boolean aceitaMudarRealizacao;

    SituacaoDoEvento(
            boolean visivelPublicamente, boolean aceitaCorrigirTexto, boolean aceitaMudarRealizacao) {
        this.visivelPublicamente = visivelPublicamente;
        this.aceitaCorrigirTexto = aceitaCorrigirTexto;
        this.aceitaMudarRealizacao = aceitaMudarRealizacao;
    }

    /**
     * RF-04: título e descrição podem ser corrigidos nesta situação?
     *
     * <p>Sim em rascunho e publicado: erro de digitação num título não pode virar definitivo só
     * porque o evento foi ao ar. Não em encerrado — o que passou é histórico, e é o mesmo
     * princípio de RN-17, que proíbe mudança de regra alterar registro já consolidado.</p>
     */
    public boolean aceitaCorrigirTexto() {
        return aceitaCorrigirTexto;
    }

    /**
     * RF-04: período, local e modalidade podem mudar nesta situação?
     *
     * <p>Só em rascunho. Depois de publicado há gente inscrita, e mudar a data ou transformar um
     * presencial em on-line altera aquilo a que essas pessoas disseram sim. O sistema não desfaz
     * sozinho uma decisão que uma pessoa tomou — é a mesma razão pela qual reduzir o limite de
     * vagas não cancela ninguém.</p>
     *
     * <p>Um evento que <b>precise</b> mudar de data depois de publicado é um evento novo, ou uma
     * conversa com quem se inscreveu. Nenhuma das duas é decisão que o software toma.</p>
     */
    public boolean aceitaMudarRealizacao() {
        return aceitaMudarRealizacao;
    }

    /** RF-10: um evento nesta situação aparece para quem não está autenticado? */
    public boolean visivelPublicamente() {
        return visivelPublicamente;
    }

    /**
     * As situações públicas, na ordem de declaração.
     *
     * <p>Existe para que o adaptador de persistência monte a cláusula {@code IN} a partir
     * desta lista, em vez de repetir os nomes no SQL.</p>
     */
    public static List<SituacaoDoEvento> publicas() {
        return Arrays.stream(values()).filter(SituacaoDoEvento::visivelPublicamente).toList();
    }
}
