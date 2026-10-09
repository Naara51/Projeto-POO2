package br.ueg.eventos.dominio.evento;

import br.ueg.eventos.dominio.comum.ValorInvalido;
import java.time.Instant;
import java.time.ZonedDateTime;

/**
 * Quantas horas antes do início do evento o cancelamento ainda é aceito — RF-15.
 *
 * <h2>Por que horas antes, e não uma data</h2>
 *
 * <p>Uma data absoluta envelhece: mudar o período do evento deixaria o prazo apontando para o
 * calendário antigo, e ninguém seria avisado. A antecedência é <b>relativa</b>, então acompanha
 * a data do evento sem que alguém precise lembrar de ajustá-la.</p>
 *
 * <h2>Por que zero é válido</h2>
 *
 * <p>Zero significa "até o instante em que o evento começa", que é uma regra legítima — e era,
 * inclusive, a regra fixa do sistema antes de RF-15 ser configurável. Negativo não é: seria
 * aceitar cancelamento depois de o evento ter começado, e a vaga liberada aí não serve a
 * ninguém.</p>
 */
public record AntecedenciaDeCancelamento(int horas) {

    /** O padrão da plataforma: dois dias. */
    public static final AntecedenciaDeCancelamento PADRAO = new AntecedenciaDeCancelamento(48);

    public AntecedenciaDeCancelamento {
        if (horas < 0) {
            throw new ValorInvalido(
                    "A antecedência de cancelamento não pode ser negativa. Informada: " + horas + ".");
        }
    }

    /** O instante-limite para cancelar, dado o início do evento. */
    public Instant prazoAPartirDe(ZonedDateTime inicio) {
        if (inicio == null) {
            throw new ValorInvalido("O prazo de cancelamento exige o início do evento.");
        }
        return inicio.minusHours(horas).toInstant();
    }

    @Override
    public String toString() {
        return horas == 0 ? "até o início" : horas + "h antes do início";
    }
}
