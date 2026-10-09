package br.ueg.eventos.dominio.inscricao;

import br.ueg.eventos.dominio.comum.ErroDeDominio;

public class InscricaoInvalida extends ErroDeDominio {

    public InscricaoInvalida(String mensagem) {
        super(mensagem);
    }
}
