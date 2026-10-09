package br.ueg.eventos.dominio.evento;

/**
 * Até onde vale a ação de quem opera um evento — RN-18.
 *
 * <p>Dois valores, e a diferença entre eles é o que o PDF separa na seção 3: o organizador
 * <i>"cria e mantém eventos"</i> — os dele —, e o administrador tem <i>"acesso
 * administrativo"</i>, que é o que torna a contingência possível quando o organizador não está
 * disponível.</p>
 *
 * <p>Este enum <b>não</b> é o perfil da pessoa. É a decisão já tomada sobre a ação, traduzida
 * pela camada de aplicação a partir de {@code Usuario.podeExecutar}. O pacote {@code evento}
 * continua sem importar {@code usuario}, que é o que mantém a fronteira entre os donos.</p>
 */
public enum AlcanceDoOperador {

    /** Só o próprio evento. É o alcance de um organizador. */
    PROPRIO_EVENTO,

    /** Qualquer evento, de qualquer organizador. É o alcance administrativo. */
    QUALQUER_EVENTO
}
