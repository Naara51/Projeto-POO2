package br.ueg.eventos.dominio.avaliacao;

import java.util.Objects;

/**
 * O que o participante informou para uma pergunta, antes de qualquer validação.
 *
 * <p>É o dado de entrada de {@link Questionario#responder}. Existe para que a
 * assinatura do método diga o que ela recebe: uma
 * {@code List&lt;RespostaInformada&gt;} é autoexplicativa, enquanto um
 * {@code Map&lt;Pergunta, String&gt;} obrigaria quem lê a deduzir o que é chave e o
 * que é valor — e não preservaria a ordem em que o participante respondeu.</p>
 *
 * <p>A distinção entre {@code RespostaInformada} e {@link Resposta} é proposital e
 * vale a pena entender: <b>informada</b> é o que chegou, ainda não julgado;
 * <b>Resposta</b> só existe depois de a {@link Pergunta} ter aceitado o conteúdo.
 * Não há como construir uma {@code Resposta} inválida no sistema. Se os dois
 * conceitos fossem a mesma classe, existiriam objetos "meio válidos" circulando, e
 * cada método precisaria reverificar.</p>
 */
public record RespostaInformada(Pergunta pergunta, String conteudo) {

    public RespostaInformada {
        Objects.requireNonNull(pergunta, "A resposta informada precisa apontar para uma pergunta.");
        conteudo = conteudo == null ? "" : conteudo.strip();
    }

    /** Conveniência para os testes e para a demonstração. */
    public static RespostaInformada de(Pergunta pergunta, String conteudo) {
        return new RespostaInformada(pergunta, conteudo);
    }
}
