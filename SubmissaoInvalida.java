package br.ueg.eventos.dominio.avaliacao;

import br.ueg.eventos.dominio.comum.ErroDeDominio;

/**
 * Lançada quando o conjunto de respostas de uma submissão não fecha, mesmo que cada
 * resposta isolada seja válida.
 *
 * <p>São três situações, e todas só podem ser percebidas olhando a submissão inteira:</p>
 *
 * <ul>
 *   <li>uma pergunta <b>obrigatória</b> ficou sem resposta;</li>
 *   <li>a mesma pergunta foi respondida <b>duas vezes</b> na mesma submissão;</li>
 *   <li>foi enviada resposta para uma pergunta que <b>não pertence</b> àquele
 *       questionário.</li>
 * </ul>
 *
 * <h2>Por que isto não é responsabilidade da Pergunta</h2>
 *
 * <p>Uma {@link Pergunta} sabe se <i>ela</i> é obrigatória, mas não tem como saber se
 * <i>todas</i> as obrigatórias do questionário foram atendidas — ela não conhece as
 * outras. Quem tem essa visão é a {@link Avaliacao}, que é a submissão completa.</p>
 *
 * <p>Esse é o critério usado para distribuir responsabilidades em todo o subdomínio:
 * <b>a regra mora no objeto que possui a informação necessária para decidi-la</b>.
 * Foi o mesmo critério que moveu o bloqueio de múltiplas tentativas da Avaliação para
 * o Questionário, e a validação de formato da Avaliação para a Pergunta — as duas
 * primeiras reorganizações registradas em {@code docs/02-modelagem-e-decisoes.md}.</p>
 */
public final class SubmissaoInvalida extends ErroDeDominio {

    SubmissaoInvalida(String mensagem) {
        super(mensagem);
    }
}
