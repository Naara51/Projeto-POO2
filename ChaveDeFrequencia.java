package br.ueg.eventos.dominio.frequencia;

import br.ueg.eventos.dominio.comum.IdAtividade;
import br.ueg.eventos.dominio.comum.IdUsuario;
import br.ueg.eventos.dominio.comum.ValorInvalido;

/**
 * Identidade de um {@link HistoricoDeFrequencia}: o par participante + atividade.
 *
 * <p>Não existe "identificador de histórico" gerado pelo sistema, porque não faria
 * sentido haver dois históricos do mesmo participante na mesma atividade. A identidade é
 * natural, e por isso é um valor — não uma chave artificial.</p>
 *
 * <p>Com ela o agregado passa a cumprir o que o seu próprio javadoc promete: uma marcação
 * de outra pessoa, ou de outra atividade, é recusada em vez de silenciosamente absorvida.
 * Isso protege RN-12 e RN-17, que dependem do histórico ser a trilha fiel de uma pessoa
 * numa atividade.</p>
 */
public record ChaveDeFrequencia(IdUsuario participanteId, IdAtividade atividadeId) {

    public ChaveDeFrequencia {
        if (participanteId == null) {
            throw new ValorInvalido("A chave de frequência exige o participante.");
        }
        if (atividadeId == null) {
            throw new ValorInvalido("A chave de frequência exige a atividade.");
        }
    }

    /** A qual histórico esta marcação pertence. */
    public static ChaveDeFrequencia de(RegistroDeFrequencia registro) {
        if (registro == null) {
            throw new ValorInvalido("O registro de frequência não pode ser nulo.");
        }
        return new ChaveDeFrequencia(registro.participanteId(), registro.atividadeId());
    }
}
