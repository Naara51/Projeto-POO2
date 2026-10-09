package br.ueg.eventos.dominio.programacao;

import br.ueg.eventos.dominio.comum.ValorInvalido;

public record Categoria(String nome) {

    public Categoria {
        if (nome == null || nome.isBlank()) {
            throw new ValorInvalido("A categoria deve ter um nome.");
        }
        nome = nome.trim();
    }
}
