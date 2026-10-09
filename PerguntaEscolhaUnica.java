package br.ueg.eventos.dominio.avaliacao;

import br.ueg.eventos.dominio.comum.ValorInvalido;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Pergunta com um conjunto fechado de alternativas, das quais o participante escolhe
 * exatamente uma (RF-25).
 *
 * <p>Exemplo: <i>"Como você avalia o domínio do tema pelo palestrante?"</i> com as
 * opções Excelente, Bom, Regular e Ruim.</p>
 *
 * <h2>Invariantes protegidas na construção (ROO-02)</h2>
 *
 * <ul>
 *   <li><b>Ao menos duas opções.</b> Uma pergunta de escolha com uma opção só não é
 *       uma escolha — é uma afirmação. Aceitar isso produziria um questionário sem
 *       sentido e um relatório com 100% em todas as respostas.</li>
 *   <li><b>Sem opções repetidas.</b> Duas opções iguais tornariam a contagem da
 *       consolidação ambígua: em qual das duas o voto entra? A verificação usa
 *       {@link LinkedHashSet}, que aproveita o {@code equals} de {@link Opcao} —
 *       mais uma razão prática para Opção ser objeto de valor.</li>
 * </ul>
 *
 * <p>As duas são verificadas <b>no construtor</b>. Depois disso, nenhum código
 * precisa perguntar se a lista de opções está coerente: não existe instância
 * incoerente. É isso que o item 8.3 chama de "invariantes protegidas nos objetos
 * apropriados, não apenas na interface".</p>
 */
public final class PerguntaEscolhaUnica extends Pergunta {

    private final List<Opcao> opcoes;

    public PerguntaEscolhaUnica(String enunciado, boolean obrigatoria, List<Opcao> opcoes) {
        super(enunciado, obrigatoria);

        if (opcoes == null || opcoes.size() < 2) {
            throw new ValorInvalido(
                    "Uma pergunta de escolha única exige ao menos duas opções. Informado: "
                            + (opcoes == null ? 0 : opcoes.size()) + ".");
        }

        Set<Opcao> distintas = new LinkedHashSet<>(opcoes);
        if (distintas.size() != opcoes.size()) {
            throw new ValorInvalido(
                    "A pergunta \"" + enunciado + "\" tem opções repetidas, "
                            + "o que tornaria a contagem das respostas ambígua.");
        }

        this.opcoes = List.copyOf(opcoes);
    }

    /** Conveniência para montar a pergunta a partir dos rótulos. */
    public static PerguntaEscolhaUnica comRotulos(String enunciado, boolean obrigatoria, String... rotulos) {
        List<Opcao> opcoes = java.util.Arrays.stream(rotulos).map(Opcao::new).toList();
        return new PerguntaEscolhaUnica(enunciado, obrigatoria, opcoes);
    }

    @Override
    protected void validarConteudo(String conteudo) {
        boolean pertence = opcoes.stream().anyMatch(opcao -> opcao.correspondeA(conteudo));
        if (!pertence) {
            throw new RespostaInvalida(this, "\"" + conteudo + "\" não é uma das opções");
        }
    }

    @Override
    public String descricaoDoFormato() {
        return "uma das opções: " + String.join(", ", opcoes.stream().map(Opcao::rotulo).toList());
    }

    @Override
    public FormaDeResposta formaDeResposta() {
        return FormaDeResposta.escolhaUnica(opcoes.stream().map(Opcao::rotulo).toList());
    }

    @Override
    public ResumoDaPergunta consolidar(List<RespostaConsolidavel> respostas) {
        return new ResumoDeEscolhaUnica(enunciado(), opcoes, respostas);
    }

    /** Lista imutável — devolvida diretamente porque {@code List.copyOf} já protege. */
    public List<Opcao> opcoes() {
        return opcoes;
    }
}
