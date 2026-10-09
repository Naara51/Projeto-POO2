package br.ueg.eventos.dominio.frequencia;

import java.util.List;

/**
 * Presença por entrada e saída. Apenas uma das duas marcações caracteriza participação
 * parcial; nenhuma delas caracteriza ausência.
 */
public final class EntradaESaida implements PoliticaDeFrequencia {

    @Override
    public SituacaoDePresenca calcular(List<RegistroDeFrequencia> registros) {
        List<RegistroDeFrequencia> validados = exigirRegistros(registros);
        boolean entrou = contem(validados, TipoDeMarcacao.ENTRADA);
        boolean saiu = contem(validados, TipoDeMarcacao.SAIDA);

        if (entrou && saiu) {
            return SituacaoDePresenca.PRESENTE;
        }
        if (entrou || saiu) {
            return SituacaoDePresenca.PARCIAL;
        }
        return SituacaoDePresenca.AUSENTE;
    }

    private static boolean contem(List<RegistroDeFrequencia> registros, TipoDeMarcacao tipo) {
        return registros.stream().anyMatch(registro -> registro.tipo() == tipo);
    }
}
