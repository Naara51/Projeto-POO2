package br.ueg.eventos.dominio.avaliacao;

import java.util.List;

/**
 * Resultado consolidado de uma pergunta (RF-28).
 *
 * <p>O critério de aceitação do RF-28 pede que o organizador consulte "quantidades,
 * distribuições simples e comentários". Três formas de apresentação diferentes, uma
 * para cada tipo de pergunta — o mesmo ponto de variação de {@link Pergunta}, visto
 * do outro lado.</p>
 *
 * <p>Por isso a hierarquia é espelhada: cada {@code Pergunta} produz o seu próprio
 * tipo de resumo. Quem monta o relatório final percorre uma
 * {@code List&lt;ResumoDaPergunta&gt;} e chama {@link #linhas()} em cada um, sem
 * distinguir tipos. Um quarto tipo de pergunta traz o seu resumo junto e o relatório
 * não muda.</p>
 *
 * <p><b>Evidência de ROO-05 e ROO-04.</b> O polimorfismo aparece nos dois lados da
 * mesma variação, e a composição (questionário contém perguntas, cada uma produz um
 * resumo) substitui o que seria uma cadeia de condicionais no relatório.</p>
 */
public abstract class ResumoDaPergunta {

    private final String enunciado;
    private final int totalDeRespostas;

    protected ResumoDaPergunta(String enunciado, int totalDeRespostas) {
        this.enunciado = enunciado;
        this.totalDeRespostas = totalDeRespostas;
    }

    /**
     * Linhas de apresentação do resumo, sem o cabeçalho — cada especialização decide
     * o que mostrar e em que ordem.
     */
    public abstract List<String> linhas();

    public String enunciado() {
        return enunciado;
    }

    /** Quantas respostas preenchidas esta pergunta recebeu entre as submissões válidas. */
    public int totalDeRespostas() {
        return totalDeRespostas;
    }

    /** Bloco textual completo: enunciado, total e as linhas específicas do tipo. */
    public String apresentacaoTextual() {
        StringBuilder texto = new StringBuilder();
        texto.append(enunciado).append('\n');
        texto.append("  respostas: ").append(totalDeRespostas).append('\n');
        for (String linha : linhas()) {
            texto.append("  ").append(linha).append('\n');
        }
        return texto.toString();
    }

    @Override
    public String toString() {
        return apresentacaoTextual();
    }
}
