package br.ueg.eventos.dominio.frequencia;

import java.util.List;

/**
 * Presença confirmada por validação manual de um responsável autorizado.
 * Leituras automáticas de QR Code não substituem essa validação.
 */
public final class ValidacaoManual implements PoliticaDeFrequencia {

    @Override
    public SituacaoDePresenca calcular(List<RegistroDeFrequencia> registros) {
        boolean validado = exigirRegistros(registros).stream()
                .anyMatch(registro -> registro.tipo() == TipoDeMarcacao.VALIDACAO_MANUAL
                        && registro.origem() == OrigemDaMarcacao.MANUAL);
        return validado ? SituacaoDePresenca.PRESENTE : SituacaoDePresenca.AUSENTE;
    }
}
