package br.ueg.eventos.dominio.avaliacao;

import java.util.Objects;

/**
 * A resposta de um participante a uma pergunta, já validada e imutável.
 *
 * <h2>Uma resposta inválida não chega a existir</h2>
 *
 * <p>O construtor chama {@link Pergunta#validar(String)} <b>antes</b> de atribuir os
 * campos. Se o conteúdo não serve, a exceção sobe e o objeto nunca é criado. A
 * consequência prática é que qualquer {@code Resposta} que exista no sistema é,
 * por construção, uma resposta válida — nenhum método precisa desconfiar dela.</p>
 *
 * <p>Isto é o que o documento 004 quer dizer com <i>"submete-se à validação da sua
 * Pergunta antes de ser aceita"</i>: a Resposta não sabe julgar formato algum, e não
 * deveria. Ela sabe a quem perguntar.</p>
 *
 * <h2>Por que o construtor tem visibilidade de pacote</h2>
 *
 * <p>Uma resposta solta, fora de uma submissão, não significa nada — não se sabe de
 * quem é nem quando foi dada. Só a {@link Avaliacao} cria respostas, e ela só é criada
 * pelo {@link Questionario}, que só a cria depois de confirmar elegibilidade. A
 * cadeia inteira de invariantes é sustentada por visibilidade, não por documentação
 * pedindo que ninguém faça besteira.</p>
 *
 * <p><b>Evidência de ROO-02 (encapsulamento).</b> O item 8.4 pede invariantes
 * protegidas "sem setters públicos indiscriminados". Aqui não há setter nenhum: os
 * dois campos são {@code final}.</p>
 */
public final class Resposta {

    private final Pergunta pergunta;
    private final String conteudo;

    Resposta(Pergunta pergunta, String conteudo) {
        Objects.requireNonNull(pergunta, "A resposta precisa apontar para uma pergunta.");
        String texto = conteudo == null ? "" : conteudo.strip();

        pergunta.validar(texto); // se não servir, nenhum objeto é criado

        this.pergunta = pergunta;
        this.conteudo = texto;
    }

    /**
     * Reidrata uma resposta gravada.
     *
     * <p>Passa pelo mesmo construtor, então a validação de formato continua valendo: um
     * conteúdo que não serve para a sua pergunta não vira objeto. Existe como método nomeado
     * apenas para que o ponto de chamada diga o que está acontecendo.</p>
     */
    static Resposta reconstituir(Pergunta pergunta, String conteudo) {
        return new Resposta(pergunta, conteudo);
    }

    public Pergunta pergunta() {
        return pergunta;
    }

    public String conteudo() {
        return conteudo;
    }

    /**
     * Falso quando a pergunta era opcional e o participante deixou em branco.
     * Respostas não preenchidas ficam de fora da consolidação — contá-las como zero
     * distorceria médias e percentuais.
     */
    public boolean preenchida() {
        return !conteudo.isEmpty();
    }

    /** Forma legível, usada na apresentação de uma submissão individual. */
    public String apresentacaoLegivel() {
        return pergunta.enunciado() + ": " + (preenchida() ? conteudo : "(sem resposta)");
    }

    @Override
    public String toString() {
        return apresentacaoLegivel();
    }
}
