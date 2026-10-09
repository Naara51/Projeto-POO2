package br.ueg.eventos.dominio.programacao;

import br.ueg.eventos.dominio.comum.IdUsuario;
import br.ueg.eventos.dominio.comum.ValorInvalido;

public record VinculoDePessoa(IdUsuario pessoaId, PapelNaAtividade papel) {

    public VinculoDePessoa {
        if (pessoaId == null) {
            throw new ValorInvalido("A pessoa vinculada à atividade não pode ser nula.");
        }
        if (papel == null) {
            throw new ValorInvalido("O papel da pessoa na atividade não pode ser nulo.");
        }
    }
}
