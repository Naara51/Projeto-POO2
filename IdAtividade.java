package br.ueg.eventos.dominio.comum;

import java.util.UUID;

public record IdAtividade(UUID valor) {

    public IdAtividade {
        if (valor == null) {
            throw new ValorInvalido("O identificador de atividade não pode ser nulo.");
        }
    }
}
