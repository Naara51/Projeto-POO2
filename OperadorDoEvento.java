package br.ueg.eventos.dominio.evento;

import br.ueg.eventos.dominio.comum.IdUsuario;
import br.ueg.eventos.dominio.comum.ValorInvalido;

/**
 * Quem está operando um evento, e com que alcance — RN-18.
 *
 * <h2>O problema que ele resolve</h2>
 *
 * <p>A RN-18 diz: <i>"Somente <b>administrador ou</b> organizador autorizado altera dados
 * operacionais do evento"</i>. O agregado cumpria só a metade que exclui — comparava o
 * identificador de quem pedia com o {@code organizadorId} e recusava qualquer outro. O
 * administrador, que a regra nomeia primeiro, era recusado junto com o participante.</p>
 *
 * <h2>Por que um objeto, e não um identificador</h2>
 *
 * <p>A alternativa seria a camada de aplicação passar o identificador do <b>dono</b> quando quem
 * opera é o administrador. Isso funcionaria e seria mentir para o agregado: ele registraria a
 * ação como se o organizador a tivesse feito. Numa operação de <b>contingência</b> — alguém
 * agindo sobre o evento de outra pessoa — saber quem agiu é o ponto inteiro.</p>
 *
 * <p>O formato é o mesmo de {@code frequencia.AutorizacaoDeFrequencia}: o domínio recebe a
 * <b>decisão pronta</b> e continua sem conhecer perfis. Quem traduz {@code Usuario.podeExecutar}
 * em alcance é a camada de aplicação, e o pacote {@code evento} segue sem importar
 * {@code usuario}.</p>
 *
 * <h2>Por que um enum, e não um booleano</h2>
 *
 * <p>{@code new OperadorDoEvento(id, true)} não diz o que é "true". Com o enum, a chamada se lê
 * sozinha, e um terceiro alcance — se algum dia houver — entra sem trocar o tipo do campo.</p>
 *
 * @param id      quem está agindo; é este identificador que fica registrado, não o do dono
 * @param alcance até onde a ação dele vale
 */
public record OperadorDoEvento(IdUsuario id, AlcanceDoOperador alcance) {

    public OperadorDoEvento {
        if (id == null) {
            throw new ValorInvalido("A operação sobre um evento exige quem a está fazendo.");
        }
        if (alcance == null) {
            throw new ValorInvalido("A operação sobre um evento exige o alcance de quem a faz.");
        }
    }

    /** O caso comum: o organizador agindo sobre o próprio evento. */
    public static OperadorDoEvento dono(IdUsuario id) {
        return new OperadorDoEvento(id, AlcanceDoOperador.PROPRIO_EVENTO);
    }

    /** A contingência: quem tem acesso administrativo age sobre o evento de qualquer organizador. */
    public static OperadorDoEvento administrativo(IdUsuario id) {
        return new OperadorDoEvento(id, AlcanceDoOperador.QUALQUER_EVENTO);
    }

    /**
     * Este operador alcança um evento deste organizador?
     *
     * <p>Responde <b>somente sobre quem</b>. Se a situação do evento aceita a mudança é outra
     * pergunta, e continua sendo do agregado — alcance administrativo não atropela o ciclo de
     * vida: encerrar um rascunho segue recusado para todo mundo.</p>
     */
    public boolean alcanca(IdUsuario organizadorId) {
        return alcance == AlcanceDoOperador.QUALQUER_EVENTO || id.equals(organizadorId);
    }
}
