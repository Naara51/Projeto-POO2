package br.ueg.eventos.dominio.avaliacao;

import br.ueg.eventos.dominio.comum.ErroDeDominio;

/**
 * Lançada quando o conteúdo informado não satisfaz o formato exigido pela pergunta
 * (RF-25, RNF-07).
 *
 * <p>A mensagem é montada com três partes, e as três importam:</p>
 *
 * <pre>
 * Resposta inválida para "Nota geral da palestra": o valor 9 está fora da escala.
 * Formato esperado: número inteiro de 1 a 5.
 * </pre>
 *
 * <ol>
 *   <li><b>Qual pergunta</b> — num questionário de dez perguntas, "resposta inválida"
 *       sozinho não ajuda ninguém.</li>
 *   <li><b>O que houve</b> — a explicação específica daquele tipo de pergunta.</li>
 *   <li><b>O que era esperado</b> — vem de {@link Pergunta#descricaoDoFormato()}, que
 *       cada especialização responde por si.</li>
 * </ol>
 *
 * <p>O RNF-07 pede "mensagens compreensíveis". Esta é a diferença prática entre
 * cumprir o requisito e alegar que cumpriu.</p>
 */
public final class RespostaInvalida extends ErroDeDominio {

    private final String enunciadoDaPergunta;

    /** Visibilidade de pacote: quem julga o formato é a própria Pergunta. */
    RespostaInvalida(Pergunta pergunta, String motivo) {
        super("Resposta inválida para \"" + pergunta.enunciado() + "\": " + motivo
                + ". Formato esperado: " + pergunta.descricaoDoFormato());
        this.enunciadoDaPergunta = pergunta.enunciado();
    }

    public String enunciadoDaPergunta() {
        return enunciadoDaPergunta;
    }
}
