package br.ueg.eventos.dominio.usuario;

import br.ueg.eventos.dominio.comum.ValorInvalido;
import java.util.Locale;

public record Email(String valor) {

    public Email {
        if (valor == null) {
            throw new ValorInvalido("O e-mail não pode ser nulo.");
        }
        valor = valor.trim().toLowerCase(Locale.ROOT);
        if (!valor.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new ValorInvalido("O e-mail informado é inválido.");
        }
    }
}
