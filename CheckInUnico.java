package br.ueg.eventos.dominio.frequencia;

import java.util.List;

/** Presença por uma única leitura de check-in. */
public final class CheckInUnico implements PoliticaDeFrequencia {

    @Override
    public SituacaoDePresenca calcular(List<RegistroDeFrequencia> registros) {
        boolean fezCheckIn = exigirRegistros(registros).stream()
                .anyMatch(registro -> registro.tipo() == TipoDeMarcacao.CHECK_IN);
        return fezCheckIn ? SituacaoDePresenca.PRESENTE : SituacaoDePresenca.AUSENTE;
    }
}
