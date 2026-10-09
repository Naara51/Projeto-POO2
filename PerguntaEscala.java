package br.ueg.eventos.dominio.avaliacao;

import java.util.List;
import java.util.Objects;

/**
 * Pergunta respondida por um número inteiro dentro de uma faixa (RF-25).
 *
 * <p>Exemplo: <i>"Nota geral da palestra"</i>, de 1 a 5.</p>
 *
 * <h2>Duas formas diferentes de recusar, e por que isso importa</h2>
 *
 * <p>Há dois jeitos de errar uma resposta de escala, e eles precisam de mensagens
 * diferentes:</p>
 *
 * <ul>
 *   <li><b>"ótimo"</b> — não é número nenhum. A mensagem tem que dizer que se espera
 *       um número.</li>
 *   <li><b>"9"</b> numa escala de 1 a 5 — é número, mas está fora da faixa. A mensagem
 *       tem que dizer qual é a faixa.</li>
 * </ul>
 *
 * <p>Tratar os dois casos com um "resposta inválida" genérico deixaria o participante
 * adivinhando. O RNF-07 pede mensagem compreensível, e compreensível quer dizer que a
 * pessoa sabe o que fazer depois de ler.</p>
 *
 * <p>Note que o {@code catch} é de {@link NumberFormatException} — uma exceção
 * técnica, da biblioteca padrão — e o que sai dele é {@link RespostaInvalida}, uma
 * exceção de domínio. Traduzir erro técnico em erro de negócio na fronteira em que
 * ele ocorre é o que impede detalhes de implementação de vazarem para a camada de
 * cima.</p>
 */
public final class PerguntaEscala extends Pergunta {

    private final EscalaNumerica escala;

    public PerguntaEscala(String enunciado, boolean obrigatoria, EscalaNumerica escala) {
        super(enunciado, obrigatoria);
        this.escala = Objects.requireNonNull(escala, "A pergunta de escala exige uma escala numérica.");
    }

    /** Atalho para o caso mais comum: nota obrigatória de 1 a 5. */
    public static PerguntaEscala notaDeUmACinco(String enunciado) {
        return new PerguntaEscala(enunciado, true, EscalaNumerica.deUmACinco());
    }

    @Override
    protected void validarConteudo(String conteudo) {
        int valor;
        try {
            valor = Integer.parseInt(conteudo);
        } catch (NumberFormatException naoENumero) {
            throw new RespostaInvalida(this, "\"" + conteudo + "\" não é um número inteiro");
        }

        if (!escala.contem(valor)) {
            throw new RespostaInvalida(this, "o valor " + valor + " está fora da escala");
        }
    }

    @Override
    public String descricaoDoFormato() {
        return "número inteiro " + escala.descricao();
    }

    @Override
    public FormaDeResposta formaDeResposta() {
        return FormaDeResposta.escala(escala);
    }

    @Override
    public ResumoDaPergunta consolidar(List<RespostaConsolidavel> respostas) {
        return new ResumoDeEscala(enunciado(), escala, respostas);
    }

    public EscalaNumerica escala() {
        return escala;
    }
}
