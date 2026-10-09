package br.ueg.eventos.dominio.programacao;

import br.ueg.eventos.dominio.comum.ValorInvalido;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Agenda do participante (RF-16, RF-17, RF-18). Deriva apenas de atividades escolhidas
 * ou inscritas (RN-08), recusa duplicidade e aplica a mesma política de conflito da
 * plataforma sobre os itens já selecionados. Quando a política bloqueia, a agenda
 * permanece inalterada.
 */
public class AgendaPessoal {

    private static final Comparator<Atividade> POR_INICIO =
            Comparator.comparing(atividade -> atividade.horario().inicio());

    private final PoliticaDeConflito politica;
    private final List<Atividade> itens = new ArrayList<>();

    public AgendaPessoal(PoliticaDeConflito politica) {
        if (politica == null) {
            throw new ValorInvalido("A agenda pessoal exige uma política de conflito.");
        }
        this.politica = politica;
    }

    /**
     * Inclui a atividade escolhida e devolve os conflitos apenas alertados.
     * Uma atividade já presente não é incluída novamente e não gera alerta.
     */
    public List<Atividade> adicionar(Atividade atividade) {
        if (atividade == null) {
            throw new ValorInvalido("A atividade selecionada não pode ser nula.");
        }
        if (jaSelecionada(atividade)) {
            return List.of();
        }
        List<Atividade> conflitos = politica.validar(List.copyOf(itens), atividade);
        itens.add(atividade);
        return conflitos;
    }

    public List<Atividade> itens() {
        return itens.stream().sorted(POR_INICIO).toList();
    }

    private boolean jaSelecionada(Atividade atividade) {
        return itens.stream().anyMatch(item -> item.id().equals(atividade.id()));
    }
}
