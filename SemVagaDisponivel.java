package br.ueg.eventos.dominio.inscricao;

import br.ueg.eventos.dominio.comum.ErroDeDominio;

public class SemVagaDisponivel extends ErroDeDominio {

    public SemVagaDisponivel(String mensagem) {
        super(mensagem);
    }
}
