package br.ueg.eventos.dominio.frequencia;

import br.ueg.eventos.dominio.comum.ErroDeDominio;

public class TokenExpirado extends ErroDeDominio {

    public TokenExpirado(String mensagem) {
        super(mensagem);
    }
}
