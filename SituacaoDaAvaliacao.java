package br.ueg.eventos.dominio.avaliacao;

/**
 * Situação de uma submissão dentro do questionário.
 *
 * <p>Só existem dois estados porque só há duas coisas que podem acontecer com uma
 * submissão: ela é a resposta que vale, ou foi trocada por uma mais nova. Não existe
 * "excluída" — a RN-17 proíbe que mudanças de regra alterem silenciosamente registros
 * históricos, e apagar uma submissão é a forma mais direta de violar isso.</p>
 *
 * <p><b>Evidência de ROO-01.</b> O documento de requisitos usa a palavra "resposta
 * válida" (RN-14). O modelo usa o mesmo vocabulário, e {@link #VALIDA} tem exatamente
 * o significado que a regra descreve — não é um {@code int situacao} com 0 e 1, cujo
 * sentido só existiria na cabeça de quem escreveu.</p>
 */
public enum SituacaoDaAvaliacao {

    /** É a resposta que conta para a consolidação e para a RN-14. */
    VALIDA("válida"),

    /** Foi trocada por uma submissão posterior. Permanece no histórico. */
    SUBSTITUIDA("substituída");

    private final String descricao;

    SituacaoDaAvaliacao(String descricao) {
        this.descricao = descricao;
    }

    public String descricao() {
        return descricao;
    }
}
