package br.ueg.eventos.dominio.avaliacao;

import java.util.List;

/**
 * Consolidação de uma {@link PerguntaTexto}: a lista dos comentários recebidos.
 *
 * <p>Não há o que somar nem distribuir em texto livre. O RF-28 pede "comentários", e
 * comentário se apresenta lendo.</p>
 *
 * <p>Cada linha traz o autor <b>já apresentado</b> pela política de identificação —
 * o nome real quando o questionário é identificado, a palavra "Anônimo" quando não é.
 * Esta classe não sabe qual das duas coisas está acontecendo, e é justamente por não
 * saber que ela não tem como errar.</p>
 */
public final class ResumoDeTexto extends ResumoDaPergunta {

    private final List<String> comentarios;

    ResumoDeTexto(String enunciado, List<RespostaConsolidavel> respostas) {
        super(enunciado, respostas.size());
        this.comentarios = respostas.stream()
                .map(r -> r.autorApresentavel() + ": " + r.conteudo())
                .toList();
    }

    /** Cópia imutável — devolvida por {@code List.copyOf} na construção via stream. */
    public List<String> comentarios() {
        return comentarios;
    }

    @Override
    public List<String> linhas() {
        if (comentarios.isEmpty()) {
            return List.of("(nenhum comentário)");
        }
        return comentarios.stream().map(c -> "- " + c).toList();
    }
}
