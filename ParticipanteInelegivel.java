package br.ueg.eventos.dominio.avaliacao;

import br.ueg.eventos.dominio.comum.ErroDeDominio;

/**
 * Lançada quando um participante tenta responder um questionário para o qual não é
 * elegível (RF-26, RN-13).
 *
 * <p>A exceção <b>carrega o motivo</b>, e não apenas uma mensagem de texto. Isso
 * permite que a camada de aplicação reaja de formas diferentes conforme o caso —
 * por exemplo, oferecer o link de inscrição quando o motivo for
 * {@link MotivoDeInelegibilidade#NAO_INSCRITO} — sem precisar interpretar string.</p>
 *
 * <p><b>Evidência de ROO-11.</b> Exceção do domínio que transporta informação de
 * domínio. É a diferença entre "a operação falhou" e "a operação falhou por este
 * motivo, que o usuário entende e sobre o qual pode agir".</p>
 */
public final class ParticipanteInelegivel extends ErroDeDominio {

    private final MotivoDeInelegibilidade motivo;

    /** Visibilidade de pacote: só o Questionário decide que alguém é inelegível. */
    ParticipanteInelegivel(MotivoDeInelegibilidade motivo) {
        super(motivo.explicacao());
        this.motivo = motivo;
    }

    public MotivoDeInelegibilidade motivo() {
        return motivo;
    }
}
