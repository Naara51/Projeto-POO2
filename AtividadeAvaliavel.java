package br.ueg.eventos.dominio.programacao;

import br.ueg.eventos.dominio.comum.IdAtividade;
import br.ueg.eventos.dominio.comum.IdUsuario;

/**
 * Contrato mínimo publicado pela programação para o domínio de avaliação.
 * Expõe o identificador e o título da atividade e as consultas de elegibilidade,
 * sem revelar inscrição, presença ou estado interno da atividade.
 *
 * <p>O título existe porque o relatório consolidado da avaliação identifica a
 * atividade avaliada; as duas consultas de elegibilidade atendem RF-26 e RN-13.
 * Nada além disso é publicado: a avaliação não precisa de horário, local,
 * classificações nem vínculos.</p>
 */
public interface AtividadeAvaliavel {

    IdAtividade id();

    /** Título da atividade, usado apenas para identificar o relatório consolidado. */
    String titulo();

    /** RF-26 / RN-13: o participante está inscrito nesta atividade? */
    boolean estaInscrito(IdUsuario participanteId);

    /** RF-26 / RN-13: o participante tem presença validada nesta atividade? */
    boolean temPresencaValidada(IdUsuario participanteId);
}
