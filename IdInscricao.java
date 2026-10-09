package br.ueg.eventos.dominio.inscricao;

import br.ueg.eventos.dominio.comum.ValorInvalido;
import java.util.UUID;

public record IdInscricao(UUID valor) {

    public IdInscricao {
        if (valor == null) {
            throw new ValorInvalido("O identificador de inscrição não pode ser nulo.");
        }
    }
}
