package br.ueg.eventos.dominio.avaliacao;

import br.ueg.eventos.dominio.comum.ValorInvalido;

/**
 * Uma alternativa de resposta de uma {@link PerguntaEscolhaUnica}.
 *
 * <p>Poderia ser uma {@code String}. Não é, por dois motivos concretos:</p>
 *
 * <ol>
 *   <li><b>A validação tem onde morar.</b> Uma opção em branco não faz sentido, e a
 *       recusa acontece na criação, não na hora de responder.</li>
 *   <li><b>O tipo comunica intenção.</b> {@code List&lt;Opcao&gt;} diz o que a lista
 *       é; {@code List&lt;String&gt;} não diz nada — poderia ser qualquer coisa.
 *       Isso é o que o RNF-13 chama de "nomes expressivos".</li>
 * </ol>
 *
 * <p><b>Evidência de ROO-03.</b> Objeto de valor: imutável, sem identidade própria,
 * comparado pelo conteúdo. Duas opções com o mesmo rótulo são a mesma opção — o que
 * é exatamente o comportamento necessário para contar respostas na consolidação
 * (RF-28).</p>
 */
public record Opcao(String rotulo) {

    public Opcao {
        if (rotulo == null || rotulo.isBlank()) {
            throw new ValorInvalido("A opção de resposta não pode ter rótulo vazio.");
        }
        rotulo = rotulo.strip();
    }

    /** Comparação usada na validação da resposta: tolerante a maiúsculas e minúsculas. */
    public boolean correspondeA(String conteudo) {
        return rotulo.equalsIgnoreCase(conteudo == null ? "" : conteudo.strip());
    }

    @Override
    public String toString() {
        return rotulo;
    }
}
