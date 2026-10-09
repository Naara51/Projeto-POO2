package br.ueg.eventos.dominio.evento;

import br.ueg.eventos.dominio.comum.ValorInvalido;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public record PeriodoDoEvento(ZonedDateTime inicio, ZonedDateTime fim) {

    public PeriodoDoEvento {
        if (inicio == null || fim == null) {
            throw new ValorInvalido("O início e o fim do evento não podem ser nulos.");
        }
        if (!inicio.getZone().equals(fim.getZone())) {
            throw new ValorInvalido("O início e o fim do evento devem usar o mesmo fuso horário.");
        }
        if (fim.isBefore(inicio)) {
            throw new ValorInvalido("O fim do evento não pode ser anterior ao início.");
        }
    }

    public ZoneId fuso() {
        return inicio.getZone();
    }
}
