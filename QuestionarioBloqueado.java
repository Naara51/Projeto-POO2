package br.ueg.eventos.dominio.avaliacao;

import br.ueg.eventos.dominio.comum.ErroDeDominio;

/**
 * Lançada quando se tenta alterar a estrutura de um questionário que já não pode
 * mudar, ou usá-lo num estado em que ele não funciona.
 *
 * <h2>A regra que esta exceção protege é a RN-17</h2>
 *
 * <p><i>"Mudanças de regra não podem alterar silenciosamente registros históricos já
 * consolidados."</i></p>
 *
 * <p>Imagine trinta pessoas respondendo "Nota geral: 5" numa escala de 1 a 5. Se o
 * organizador editar a pergunta para escala de 1 a 10, aqueles trinta cincos passam a
 * significar outra coisa — de nota máxima para nota mediana — sem que ninguém tenha
 * tocado nas respostas. O relatório consolidado muda de sentido sozinho. É exatamente
 * a alteração silenciosa que a RN-17 proíbe.</p>
 *
 * <p>Por isso {@link Questionario#adicionarPergunta} recusa qualquer alteração depois
 * da primeira submissão. A alternativa avaliada — versionar o questionário e manter as
 * respostas antigas ligadas à versão antiga — resolve o mesmo problema com muito mais
 * máquina, e está registrada como descartada em
 * {@code docs/04-padroes-e-refatoracoes.md}.</p>
 */
public final class QuestionarioBloqueado extends ErroDeDominio {

    QuestionarioBloqueado(String mensagem) {
        super(mensagem);
    }
}
