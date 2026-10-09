package br.ueg.eventos.dominio.inscricao;

import br.ueg.eventos.dominio.comum.ValorInvalido;
import java.util.LinkedHashSet;
import java.util.OptionalInt;
import java.util.Set;

/**
 * Capacidade de um alvo de inscrição (RF-14, RN-06). As reservas são guardadas por
 * {@link IdInscricao}, de modo que repetir a mesma requisição não consome outra vaga.
 * O controle desativado é representado por {@link #ilimitado()} e nunca recusa reserva.
 */
public class ControleDeVagas {

    private final OptionalInt limite;
    private final Set<IdInscricao> reservas = new LinkedHashSet<>();

    private ControleDeVagas(OptionalInt limite) {
        this.limite = limite;
    }

    public static ControleDeVagas limitadoA(int limite) {
        if (limite <= 0) {
            throw new ValorInvalido("O limite de vagas deve ser maior que zero.");
        }
        return new ControleDeVagas(OptionalInt.of(limite));
    }

    public static ControleDeVagas ilimitado() {
        return new ControleDeVagas(OptionalInt.empty());
    }

    public void reservar(IdInscricao inscricaoId) {
        exigirIdentificador(inscricaoId);
        if (reservas.contains(inscricaoId)) {
            return;
        }
        if (!temVagaDisponivel()) {
            throw new SemVagaDisponivel("Não há vaga disponível para confirmar a inscrição.");
        }
        reservas.add(inscricaoId);
    }

    public void liberar(IdInscricao inscricaoId) {
        exigirIdentificador(inscricaoId);
        reservas.remove(inscricaoId);
    }

    public boolean temVagaDisponivel() {
        return limite.isEmpty() || reservas.size() < limite.getAsInt();
    }

    public int reservadas() {
        return reservas.size();
    }

    /**
     * Vagas restantes. Quando o controle está desativado devolve {@link Integer#MAX_VALUE},
     * pois não existe limite a consumir.
     */
    public int disponiveis() {
        return limite.isEmpty() ? Integer.MAX_VALUE : limite.getAsInt() - reservas.size();
    }

    public boolean estaAtivo() {
        return limite.isPresent();
    }

    private static void exigirIdentificador(IdInscricao inscricaoId) {
        if (inscricaoId == null) {
            throw new ValorInvalido("A reserva de vaga exige o identificador da inscrição.");
        }
    }
}
