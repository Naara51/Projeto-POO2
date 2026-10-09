package br.ueg.eventos.dominio.comum;

import java.util.UUID;

public record IdUsuario(UUID valor) {

    public IdUsuario {
        if (valor == null) {
            throw new ValorInvalido("O identificador de usuário não pode ser nulo.");
        }
    }
}
