package br.ueg.eventos.dominio.programacao;

import br.ueg.eventos.dominio.comum.ValorInvalido;

public record Trilha(String nome) {

    public Trilha {
        if (nome == null || nome.isBlank()) {
            throw new ValorInvalido("A trilha deve ter um nome.");
        }
        nome = nome.trim();
    }
}
