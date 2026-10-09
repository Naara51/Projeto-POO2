package br.ueg.eventos.dominio.avaliacao;

import java.util.List;

/**
 * Como uma pergunta quer ser exibida — RF-25, <i>"cada tipo é <b>exibido</b>, validado e
 * persistido corretamente"</i>.
 *
 * <h2>O que este objeto resolve</h2>
 *
 * <p>A validação e a persistência de cada tipo já eram polimórficas. A exibição não era: a API
 * publicava só {@code descricaoDoFormato()}, uma frase — <i>"uma das opções: Ótimo, Bom,
 * Ruim"</i> —, e a tela, sem mais nada, desenhava um campo de texto livre para os três tipos.
 * Quem respondia a uma escala de 1 a 5 recebia o mesmo campo de quem escrevia um comentário, e
 * uma escolha única obrigava a digitar a opção em vez de oferecê-la.</p>
 *
 * <h2>Por que não deixar a tela deduzir</h2>
 *
 * <p>Seria possível analisar a frase em JavaScript e adivinhar o tipo. Isso reimplementaria, do
 * lado do cliente, a decisão que {@code PerguntaTexto}, {@code PerguntaEscolhaUnica} e
 * {@code PerguntaEscala} já tomam — duas versões da mesma regra, e a segunda escrita na
 * linguagem que não tem como ser testada pelo domínio.</p>
 *
 * <h2>Por que um objeto só, com campos nulos</h2>
 *
 * <p>Os campos que não se aplicam vêm nulos: escala não tem {@code tamanhoMaximo}, texto não tem
 * limites. A alternativa — três records distintos — obrigaria quem consome a perguntar de qual
 * deles se trata, que é exatamente o {@code if} por tipo que se quer evitar. Aqui o consumidor
 * lê {@link #tipo()} uma vez, na hora de escolher o controle, e os campos vêm prontos.</p>
 *
 * @param tipo          o nome da forma; é o que a tela usa para escolher o controle
 * @param opcoes        os rótulos, em ordem, quando há escolha; lista vazia nos demais tipos
 * @param minimo        o menor valor aceito, só em escala
 * @param maximo        o maior valor aceito, só em escala
 * @param tamanhoMaximo o limite de caracteres, só em texto
 */
public record FormaDeResposta(
        TipoDePergunta tipo,
        List<String> opcoes,
        Integer minimo,
        Integer maximo,
        Integer tamanhoMaximo) {

    /**
     * A lista de opções é congelada na construção.
     *
     * <p>Sem isto, quem recebesse a forma poderia acrescentar uma opção à lista e, dependendo da
     * implementação de origem, alterar a pergunta por fora — burlando a validação que
     * {@code PerguntaEscolhaUnica} faz no construtor (ROO-02).</p>
     */
    public FormaDeResposta {
        opcoes = opcoes == null ? List.of() : List.copyOf(opcoes);
    }

    static FormaDeResposta texto(int tamanhoMaximo) {
        return new FormaDeResposta(TipoDePergunta.TEXTO, List.of(), null, null, tamanhoMaximo);
    }

    static FormaDeResposta escolhaUnica(List<String> opcoes) {
        return new FormaDeResposta(TipoDePergunta.ESCOLHA_UNICA, opcoes, null, null, null);
    }

    static FormaDeResposta escala(EscalaNumerica escala) {
        return new FormaDeResposta(
                TipoDePergunta.ESCALA, List.of(), escala.minimo(), escala.maximo(), null);
    }
}
