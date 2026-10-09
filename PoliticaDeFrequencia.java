package br.ueg.eventos.dominio.frequencia;

import br.ueg.eventos.dominio.comum.ValorInvalido;
import java.util.List;

/**
 * Critério de presença configurado por atividade (RF-19, RN-09). Cada implementação
 * calcula a situação a partir dos registros, sem alterá-los: trocar a política muda o
 * resultado do cálculo, nunca o histórico já gravado (RN-17).
 */
public interface PoliticaDeFrequencia {

    SituacaoDePresenca calcular(List<RegistroDeFrequencia> registros);

    default List<RegistroDeFrequencia> exigirRegistros(List<RegistroDeFrequencia> registros) {
        if (registros == null) {
            throw new ValorInvalido("O cálculo de presença exige a lista de registros.");
        }
        return registros;
    }
}
