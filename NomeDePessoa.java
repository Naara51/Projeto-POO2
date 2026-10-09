package br.ueg.eventos.dominio.usuario;

import br.ueg.eventos.dominio.comum.ValorInvalido;

public record NomeDePessoa(String valor) {

    public NomeDePessoa {
        if (valor == null) {
            throw new ValorInvalido("O nome da pessoa não pode ser nulo.");
        }
        valor = valor.trim();
        if (valor.isEmpty()) {
            throw new ValorInvalido("O nome da pessoa não pode ser vazio.");
        }
    }
}
