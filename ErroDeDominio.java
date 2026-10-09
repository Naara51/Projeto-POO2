package br.ueg.eventos.dominio.comum;

public class ErroDeDominio extends RuntimeException {

    public ErroDeDominio(String mensagem) {
        super(mensagem);
    }
}
