package br.ueg.eventos.dominio.inscricao;

import br.ueg.eventos.dominio.comum.ErroDeDominio;

public class CancelamentoInvalido extends ErroDeDominio {

    public CancelamentoInvalido(String mensagem) {
        super(mensagem);
    }
}
