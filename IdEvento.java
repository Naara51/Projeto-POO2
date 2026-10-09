package br.ueg.eventos.dominio.comum;

import java.util.UUID;

public record IdEvento(UUID valor) {

    public IdEvento {
        if (valor == null) {
            throw new ValorInvalido("O identificador de evento não pode ser nulo.");
        }
    }
}
