package br.ueg.eventos.dominio.frequencia;

import br.ueg.eventos.dominio.comum.ErroDeDominio;

public class CorrecaoNaoAutorizada extends ErroDeDominio {

    public CorrecaoNaoAutorizada(String mensagem) {
        super(mensagem);
    }
}
