package br.ueg.eventos.dominio.frequencia;

import br.ueg.eventos.dominio.comum.IdAtividade;
import br.ueg.eventos.dominio.comum.IdUsuario;
import br.ueg.eventos.dominio.comum.ValorInvalido;
import java.time.Instant;

/**
 * Marcação imutável de frequência (RN-11). Guarda participante, atividade, instante,
 * tipo e origem. A origem {@link OrigemDaMarcacao#MANUAL} exige responsável e motivo,
 * preservando a rastreabilidade exigida por RF-22 e RN-12; a leitura por QR Code não
 * aceita nenhum dos dois, porque não há intervenção humana a registrar.
 */
public record RegistroDeFrequencia(
        IdUsuario participanteId,
        IdAtividade atividadeId,
        Instant instante,
        TipoDeMarcacao tipo,
        OrigemDaMarcacao origem,
        IdUsuario responsavelId,
        String motivo) {

    public RegistroDeFrequencia {
        exigirValor(participanteId, "O registro de frequência exige o participante.");
        exigirValor(atividadeId, "O registro de frequência exige a atividade.");
        exigirValor(instante, "O registro de frequência exige o instante da marcação.");
        exigirValor(tipo, "O registro de frequência exige o tipo da marcação.");
        exigirValor(origem, "O registro de frequência exige a origem da marcação.");

        if (origem == OrigemDaMarcacao.MANUAL) {
            exigirValor(responsavelId, "A marcação manual exige o responsável.");
            if (motivo == null || motivo.isBlank()) {
                throw new ValorInvalido("A marcação manual exige o motivo.");
            }
            motivo = motivo.trim();
        } else {
            if (responsavelId != null) {
                throw new ValorInvalido("A leitura por QR Code não possui responsável manual.");
            }
            if (motivo != null && !motivo.isBlank()) {
                throw new ValorInvalido("A leitura por QR Code não possui motivo de correção.");
            }
            motivo = null;
        }
    }

    public static RegistroDeFrequencia porQrCode(
            IdUsuario participanteId, IdAtividade atividadeId, Instant instante, TipoDeMarcacao tipo) {
        return new RegistroDeFrequencia(
                participanteId, atividadeId, instante, tipo, OrigemDaMarcacao.QR_CODE, null, null);
    }

    public static RegistroDeFrequencia manual(
            IdUsuario participanteId,
            IdAtividade atividadeId,
            Instant instante,
            TipoDeMarcacao tipo,
            IdUsuario responsavelId,
            String motivo) {
        return new RegistroDeFrequencia(
                participanteId, atividadeId, instante, tipo, OrigemDaMarcacao.MANUAL, responsavelId, motivo);
    }

    private static void exigirValor(Object valor, String mensagem) {
        if (valor == null) {
            throw new ValorInvalido(mensagem);
        }
    }
}
