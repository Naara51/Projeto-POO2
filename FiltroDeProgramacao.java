package br.ueg.eventos.dominio.programacao;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Filtro combinável da programação (RF-09). Cada campo informado vira um critério;
 * campo nulo não restringe a consulta. Os critérios são compostos por função, sem
 * cadeia de {@code if} por tipo de atividade.
 */
public record FiltroDeProgramacao(
        LocalDate data,
        Trilha trilha,
        TipoDeAtividade tipo,
        Local local,
        Categoria categoria) {

    public boolean aceita(Atividade atividade) {
        return criterios().stream().allMatch(criterio -> criterio.test(atividade));
    }

    private List<Predicate<Atividade>> criterios() {
        return List.of(
                criterio(data, atividade -> atividade.horario().inicio().toLocalDate()),
                criterio(trilha, Atividade::trilha),
                criterio(tipo, Atividade::tipo),
                criterio(local, Atividade::local),
                criterio(categoria, atividade -> atividade.categoria().orElse(null)));
    }

    private static <T> Predicate<Atividade> criterio(T esperado, Function<Atividade, T> extrator) {
        if (esperado == null) {
            return atividade -> true;
        }
        return atividade -> esperado.equals(extrator.apply(atividade));
    }
}
