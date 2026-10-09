package br.ueg.eventos.dominio.frequencia;

import br.ueg.eventos.dominio.comum.ErroDeDominio;

/**
 * O código apresentado não foi emitido para esta atividade — RF-20, decisão D-05.
 *
 * <p>É diferente de {@link TokenExpirado}, que é o código certo tarde demais. A distinção é
 * deliberada e a mensagem de cada uma diz o que fazer: pedir um código novo, ou conferir se
 * leu o QR Code da sala certa.</p>
 *
 * <p>Aqui separar não abre brecha de enumeração, como abriria no login: o QR Code é projetado
 * para a sala inteira, então o código não é segredo de ninguém — o que ele prova é que quem
 * leu estava onde o código foi exibido.</p>
 */
public class TokenNaoReconhecido extends ErroDeDominio {

    public TokenNaoReconhecido(String mensagem) {
        super(mensagem);
    }
}
