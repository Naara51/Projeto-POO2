package br.ueg.eventos.dominio.inscricao;

import br.ueg.eventos.dominio.comum.IdAtividade;
import br.ueg.eventos.dominio.comum.ValorInvalido;

/**
 * Configuração de inscrição do evento (RF-13, RN-05). Cada valor decide sozinho se a
 * atividade é proibida, opcional ou obrigatória, mantendo a variação dentro do enum
 * em vez de espalhar condicionais pelos chamadores.
 */
public enum PoliticaDeInscricao {

    SOMENTE_EVENTO {
        @Override
        public void validarAlvo(IdAtividade atividadeId) {
            if (atividadeId != null) {
                throw new ValorInvalido("Esta inscrição é somente para o evento e não aceita atividade.");
            }
        }
    },

    EVENTO_COM_ESCOLHA_DE_ATIVIDADES {
        @Override
        public void validarAlvo(IdAtividade atividadeId) {
            // A atividade é opcional: o participante pode inscrever-se apenas no evento.
        }
    },

    POR_ATIVIDADE {
        @Override
        public void validarAlvo(IdAtividade atividadeId) {
            if (atividadeId == null) {
                throw new ValorInvalido("Esta inscrição exige a atividade escolhida.");
            }
        }
    };

    public abstract void validarAlvo(IdAtividade atividadeId);
}
