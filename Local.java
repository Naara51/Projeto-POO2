package br.ueg.eventos.dominio.programacao;

import br.ueg.eventos.dominio.comum.ValorInvalido;

public record Local(String nome) {

    public Local {
        if (nome == null || nome.isBlank()) {
            throw new ValorInvalido("O local da atividade deve ter um nome.");
        }
        nome = nome.trim();
    }
}
