package br.ueg.eventos.dominio.programacao;

import br.ueg.eventos.dominio.comum.ValorInvalido;

public record TipoDeAtividade(String nome) {

    public TipoDeAtividade {
        if (nome == null || nome.isBlank()) {
            throw new ValorInvalido("O tipo da atividade deve ter um nome.");
        }
        nome = nome.trim();
    }
}
