package br.ueg.eventos.dominio.avaliacao;

import java.util.Optional;

/**
 * Resposta de {@link Questionario#verificarElegibilidade}: pode responder, e se não
 * pode, por quê.
 *
 * <h2>Por que não devolver um boolean</h2>
 *
 * <p>Porque o RF-26 exige que o motivo seja apresentado. Com {@code boolean}, a
 * interface receberia {@code false} e teria que adivinhar a causa — ou, pior,
 * reimplementar as verificações do lado de fora para descobrir qual delas falhou.
 * Nesse momento a regra passaria a existir em dois lugares, e um deles ficaria
 * desatualizado.</p>
 *
 * <h2>Por que não lançar exceção direto</h2>
 *
 * <p>Porque há um caso de uso legítimo em que a inelegibilidade não é um erro:
 * a tela que decide se mostra ou não o botão "responder". Perguntar é diferente de
 * tentar. {@link Questionario#verificarElegibilidade} pergunta e devolve este objeto;
 * {@link Questionario#responder} tenta e lança {@link ParticipanteInelegivel}. As duas
 * usam a mesma apuração, então não há como divergirem.</p>
 *
 * <p><b>Evidência de ROO-11.</b> Estado inválido modelado explicitamente, com
 * mensagem compreensível, em vez de código de erro numérico ou {@code null}.</p>
 */
public final class ResultadoDeElegibilidade {

    private static final ResultadoDeElegibilidade ELEGIVEL = new ResultadoDeElegibilidade(null);

    /** Nulo quando elegível. O acesso externo é sempre por {@link #motivo()}, que devolve Optional. */
    private final MotivoDeInelegibilidade motivo;

    private ResultadoDeElegibilidade(MotivoDeInelegibilidade motivo) {
        this.motivo = motivo;
    }

    static ResultadoDeElegibilidade apto() {
        return ELEGIVEL;
    }

    static ResultadoDeElegibilidade impedidoPor(MotivoDeInelegibilidade motivo) {
        if (motivo == null) {
            throw new IllegalArgumentException("Inelegibilidade exige um motivo.");
        }
        return new ResultadoDeElegibilidade(motivo);
    }

    public boolean elegivel() {
        return motivo == null;
    }

    /** Vazio quando elegível. Optional evita que um null escape do domínio. */
    public Optional<MotivoDeInelegibilidade> motivo() {
        return Optional.ofNullable(motivo);
    }

    /** Texto pronto para exibição, tanto no caso positivo quanto no negativo. */
    public String explicacao() {
        return elegivel()
                ? "Participante elegível para responder."
                : motivo.explicacao();
    }

    @Override
    public String toString() {
        return explicacao();
    }
}
