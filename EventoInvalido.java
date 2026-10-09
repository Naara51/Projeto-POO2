package br.ueg.eventos.dominio.evento;

import br.ueg.eventos.dominio.comum.ErroDeDominio;

public class EventoInvalido extends ErroDeDominio {

    public EventoInvalido(String mensagem) {
        super(mensagem);
    }
}
