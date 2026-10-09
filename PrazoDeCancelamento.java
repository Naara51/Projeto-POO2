package br.ueg.eventos.dominio.inscricao;

import br.ueg.eventos.dominio.comum.ValorInvalido;
import java.time.Instant;

/**
 * Limite temporal para cancelar uma inscrição (RF-15). O instante corrente é sempre
 * informado pelo chamador; o domínio não consulta o relógio da máquina.
 */
public record PrazoDeCancelamento(Instant limite) {

    public PrazoDeCancelamento {
        if (limite == null) {
            throw new ValorInvalido("O prazo de cancelamento precisa de um instante limite.");
        }
    }

    public boolean permite(Instant agora) {
        if (agora == null) {
            throw new ValorInvalido("O instante consultado no prazo não pode ser nulo.");
        }
        return !agora.isAfter(limite);
    }
}
