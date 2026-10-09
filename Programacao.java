package br.ueg.eventos.dominio.programacao;

import br.ueg.eventos.dominio.comum.ValorInvalido;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Grade de atividades de um evento. Aplica a política de conflito sobre as atividades
 * que disputam o mesmo local (RF-07) e responde consultas por filtros combináveis (RF-09).
 * Atividades simultâneas em locais diferentes são permitidas (RN-03).
 */
public class Programacao {

    private static final Comparator<Atividade> POR_INICIO =
            Comparator.comparing(atividade -> atividade.horario().inicio());

    private final PoliticaDeConflito politica;
    private final List<Atividade> atividades = new ArrayList<>();

    public Programacao(PoliticaDeConflito politica) {
        if (politica == null) {
            throw new ValorInvalido("A programação exige uma política de conflito.");
        }
        this.politica = politica;
    }

    public List<Atividade> adicionar(Atividade atividade) {
        if (atividade == null) {
            throw new ValorInvalido("A atividade adicionada à programação não pode ser nula.");
        }
        List<Atividade> conflitos = politica.validar(noMesmoLocal(atividade), atividade);
        atividades.add(atividade);
        return conflitos;
    }

    public List<Atividade> consultar(FiltroDeProgramacao filtro) {
        if (filtro == null) {
            throw new ValorInvalido("O filtro da programação não pode ser nulo.");
        }
        return atividades.stream()
                .filter(filtro::aceita)
                .sorted(POR_INICIO)
                .toList();
    }

    private List<Atividade> noMesmoLocal(Atividade nova) {
        return atividades.stream()
                .filter(atividade -> atividade.local().equals(nova.local()))
                .toList();
    }
}
