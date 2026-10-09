package br.ueg.eventos.dominio.programacao;

import br.ueg.eventos.dominio.comum.ErroDeDominio;

public class ConflitoDeHorario extends ErroDeDominio {

    public ConflitoDeHorario(String mensagem) {
        super(mensagem);
    }
}
