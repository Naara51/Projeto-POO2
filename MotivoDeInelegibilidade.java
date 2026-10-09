package br.ueg.eventos.dominio.avaliacao;

/**
 * Por que um participante não pode responder um questionário.
 *
 * <h2>Este enum é a resposta a uma exigência literal do requisito</h2>
 *
 * <p>O critério de aceitação do RF-26 diz: <i>"usuário inelegível não envia avaliação;
 * <b>motivo é apresentado</b>"</i>. E o cenário CA-06 exige demonstrar que "outro
 * usuário inelegível é impedido". Um método que devolvesse {@code boolean} atenderia à
 * primeira metade e não teria como atender à segunda — a informação de <i>qual</i>
 * condição falhou simplesmente não existiria.</p>
 *
 * <p>Por isso {@link Questionario#verificarElegibilidade} devolve
 * {@link ResultadoDeElegibilidade} e não {@code boolean}. Foi a terceira das
 * responsabilidades reorganizadas em relação ao documento 002, e está registrada em
 * {@code docs/02-modelagem-e-decisoes.md}.</p>
 *
 * <p><b>Evidência de ROO-11.</b> O item 8.4 pede "modelar falhas e estados inválidos
 * de forma explícita, preservando consistência e mensagens compreensíveis". Cada
 * constante carrega a explicação pronta para o usuário final, em português, sem que a
 * camada de interface precise traduzir código de erro.</p>
 */
public enum MotivoDeInelegibilidade {

    NAO_INSCRITO(
            "Você não está inscrito nesta atividade."),

    SEM_PRESENCA_VALIDADA(
            "Sua presença nesta atividade ainda não foi validada."),

    FORA_DA_JANELA_DE_RESPOSTAS(
            "Este questionário não está aberto para respostas neste momento."),

    JA_RESPONDIDO(
            "Você já respondeu este questionário e ele não permite alteração da resposta.");

    private final String explicacao;

    MotivoDeInelegibilidade(String explicacao) {
        this.explicacao = explicacao;
    }

    /** Texto pronto para ser exibido ao participante. */
    public String explicacao() {
        return explicacao;
    }
}
