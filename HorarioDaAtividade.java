package br.ueg.eventos.dominio.programacao;

import br.ueg.eventos.dominio.comum.ValorInvalido;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public record HorarioDaAtividade(ZonedDateTime inicio, ZonedDateTime fim) {

    public HorarioDaAtividade {
        if (inicio == null || fim == null) {
            throw new ValorInvalido("O início e o fim da atividade não podem ser nulos.");
        }
        if (!inicio.getZone().equals(fim.getZone())) {
            throw new ValorInvalido("O início e o fim da atividade devem usar o mesmo fuso horário.");
        }
        if (!fim.isAfter(inicio)) {
            throw new ValorInvalido("O horário da atividade deve ter início anterior ao fim.");
        }
    }

    public boolean sobrepoe(HorarioDaAtividade outro) {
        if (outro == null) {
            throw new ValorInvalido("O horário comparado não pode ser nulo.");
        }
        return inicio.isBefore(outro.fim) && outro.inicio.isBefore(fim);
    }

    public ZoneId fuso() {
        return inicio.getZone();
    }
}
