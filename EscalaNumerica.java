package br.ueg.eventos.dominio.avaliacao;

import br.ueg.eventos.dominio.comum.ValorInvalido;

/**
 * Faixa de valores aceitos por uma {@link PerguntaEscala} — por exemplo, de 1 a 5.
 *
 * <p>Mesma justificativa de {@link JanelaDeRespostas}: dois inteiros soltos dentro
 * da pergunta permitiriam uma escala de 5 a 1, que não significa nada. Aqui a
 * invariante {@code minimo < maximo} é garantida na construção.</p>
 *
 * <p><b>Evidência de ROO-03.</b> O método {@link #contem(int)} coloca a decisão
 * "este valor pertence à escala?" dentro do objeto que conhece a escala. Sem ele, a
 * comparação {@code valor >= min && valor <= max} apareceria repetida na validação
 * e de novo na consolidação — e uma das duas cópias acabaria divergindo.</p>
 */
public record EscalaNumerica(int minimo, int maximo) {

    public EscalaNumerica {
        if (minimo >= maximo) {
            throw new ValorInvalido(
                    "A escala numérica exige mínimo menor que o máximo. "
                            + "Informado: mínimo " + minimo + ", máximo " + maximo + ".");
        }
    }

    /** Escala de 1 a 5, a mais comum em questionários de satisfação. */
    public static EscalaNumerica deUmACinco() {
        return new EscalaNumerica(1, 5);
    }

    public boolean contem(int valor) {
        return valor >= minimo && valor <= maximo;
    }

    /** Quantidade de valores distintos aceitos. Usado para montar a distribuição (RF-28). */
    public int quantidadeDeValores() {
        return maximo - minimo + 1;
    }

    public String descricao() {
        return "de " + minimo + " a " + maximo;
    }
}
