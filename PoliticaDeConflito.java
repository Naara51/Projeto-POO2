package br.ueg.eventos.dominio.programacao;

import br.ueg.eventos.dominio.comum.ValorInvalido;
import java.util.List;

/**
 * Política única de conflito da plataforma (RN-07). O critério de conflito é sempre a
 * sobreposição de horário entre a atividade nova e as atividades candidatas informadas
 * pelo chamador: a programação informa as atividades do mesmo local e a agenda pessoal
 * informa os itens do próprio participante. A política decide apenas o que fazer com o
 * conflito detectado — bloquear ou alertar.
 */
public enum PoliticaDeConflito {

    BLOQUEAR {
        @Override
        public List<Atividade> validar(List<Atividade> candidatas, Atividade nova) {
            List<Atividade> conflitos = conflitos(candidatas, nova);
            if (!conflitos.isEmpty()) {
                throw new ConflitoDeHorario(
                        "A atividade \"" + nova.titulo() + "\" conflita com \""
                                + conflitos.get(0).titulo() + "\" no mesmo horário.");
            }
            return conflitos;
        }
    },

    ALERTAR {
        @Override
        public List<Atividade> validar(List<Atividade> candidatas, Atividade nova) {
            return conflitos(candidatas, nova);
        }
    };

    public abstract List<Atividade> validar(List<Atividade> candidatas, Atividade nova);

    static List<Atividade> conflitos(List<Atividade> candidatas, Atividade nova) {
        if (candidatas == null || nova == null) {
            throw new ValorInvalido("A verificação de conflito exige a atividade nova e as candidatas.");
        }
        return candidatas.stream()
                .filter(candidata -> candidata.horario().sobrepoe(nova.horario()))
                .toList();
    }
}
