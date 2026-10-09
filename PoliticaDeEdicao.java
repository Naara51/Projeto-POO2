package br.ueg.eventos.dominio.avaliacao;

/**
 * O que acontece quando um participante tenta responder um questionário que ele já
 * respondeu (RF-27, RN-14).
 *
 * <p>O critério de aceitação do RF-27 é explícito quanto a existirem <b>dois</b>
 * comportamentos legítimos: <i>"nova tentativa é bloqueada ou atualiza a resposta,
 * conforme regra documentada"</i>. Ou seja, o próprio requisito descreve um ponto de
 * variação — e ponto de variação é exatamente onde o item 8.4 espera ver polimorfismo
 * em vez de condicional.</p>
 *
 * <h2>Padrão de projeto: Strategy (ROO-10)</h2>
 *
 * <p>Cada constante é uma estratégia com a sua própria implementação. O
 * {@link Questionario} guarda uma delas e delega; ele nunca pergunta "qual política é
 * esta?". Trocar a política de um questionário é trocar o objeto, não editar um
 * {@code if}.</p>
 *
 * <p><b>Por que enum e não interface com classes separadas:</b> as estratégias aqui
 * são um conjunto fechado e sem estado próprio. Enum dá exatamente isso, com
 * serialização, comparação e listagem de graça. Se um dia a equipe precisar de uma
 * política parametrizada — "permite edição até 24h após o envio" —, aí sim vale
 * promover para interface. Registrar essa fronteira faz parte da justificativa
 * exigida pelo ROO-10: padrão aplicado onde resolve problema, não por ornamento.</p>
 */
public enum PoliticaDeEdicao {

    /**
     * A primeira resposta válida é definitiva. Uma segunda tentativa é recusada com
     * {@link MotivoDeInelegibilidade#JA_RESPONDIDO}.
     */
    BLOQUEIA_NOVA_TENTATIVA("bloqueia nova tentativa") {
        @Override
        public boolean permiteSubstituicao() {
            return false;
        }

        @Override
        void aplicarSobre(Avaliacao submissaoAnterior) {
            // Inalcançável em uso normal: verificarElegibilidade já recusa a segunda
            // tentativa antes de chegar aqui. A guarda existe para que uma futura
            // alteração no Questionario que quebre essa ordem falhe alto, em vez de
            // silenciosamente permitir duas respostas válidas e violar a RN-14.
            throw new IllegalStateException(
                    "Estado inconsistente: política que bloqueia nova tentativa não substitui submissão.");
        }
    },

    /**
     * A resposta anterior é marcada como {@link SituacaoDaAvaliacao#SUBSTITUIDA} e a
     * nova passa a ser a válida. O histórico é preservado — nada é apagado (RN-17).
     */
    SUBSTITUI_ANTERIOR("substitui a resposta anterior") {
        @Override
        public boolean permiteSubstituicao() {
            return true;
        }

        @Override
        void aplicarSobre(Avaliacao submissaoAnterior) {
            submissaoAnterior.marcarComoSubstituida();
        }
    };

    private final String descricao;

    PoliticaDeEdicao(String descricao) {
        this.descricao = descricao;
    }

    /** Usado pelo {@link Questionario} ao apurar a elegibilidade. */
    public abstract boolean permiteSubstituicao();

    /**
     * Executa o efeito da política sobre a submissão anterior.
     *
     * <p>Visibilidade de pacote de propósito: só o {@link Questionario} pode acionar
     * a substituição, porque só ele sabe que a nova submissão foi de fato aceita.</p>
     */
    abstract void aplicarSobre(Avaliacao submissaoAnterior);

    public String descricao() {
        return descricao;
    }
}
