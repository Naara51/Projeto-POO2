package br.ueg.eventos.dominio.programacao;

import br.ueg.eventos.dominio.comum.IdAtividade;
import br.ueg.eventos.dominio.comum.IdEvento;
import br.ueg.eventos.dominio.comum.ValorInvalido;
import br.ueg.eventos.dominio.frequencia.CriterioDePresenca;
import br.ueg.eventos.dominio.inscricao.ControleDeVagas;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Atividade {

    private final IdAtividade id;
    private final IdEvento eventoId;
    private final String titulo;
    private final TipoDeAtividade tipo;
    private final HorarioDaAtividade horario;
    private final Local local;
    private final Trilha trilha;
    private final Categoria categoria;
    private final List<VinculoDePessoa> vinculos = new ArrayList<>();

    /**
     * RF-19 — o critério de presença é configurável por atividade.
     *
     * <p>Começa em {@code CHECK_IN_UNICO} em vez de exigir escolha no construtor, pela mesma
     * razão que o evento começa em rascunho: é o estado inicial razoável, e obrigar a decidir
     * antes de existir tornaria o cadastro mais difícil sem impedir erro nenhum.</p>
     */
    private CriterioDePresenca criterioDePresenca = CriterioDePresenca.CHECK_IN_UNICO;

    /**
     * RF-13 — a capacidade é configuração da atividade. {@code null} significa sem limite.
     *
     * <p>O tipo é {@code Integer} e não {@code int} porque "sem limite" e "limite zero" são
     * coisas diferentes, e um {@code int} não teria como distingui-las sem um valor mágico.</p>
     */
    private Integer limiteDeVagas;

    public Atividade(
            IdAtividade id,
            IdEvento eventoId,
            String titulo,
            TipoDeAtividade tipo,
            HorarioDaAtividade horario,
            Local local,
            Trilha trilha,
            Categoria categoria) {
        if (titulo == null || titulo.isBlank()) {
            throw new ValorInvalido("A atividade deve ter um título.");
        }
        this.id = exigirValor(id, "O identificador da atividade não pode ser nulo.");
        this.eventoId = exigirValor(eventoId, "A atividade deve pertencer a um evento.");
        this.titulo = titulo.trim();
        this.tipo = exigirValor(tipo, "A atividade deve ter um tipo.");
        this.horario = exigirValor(horario, "A atividade deve ter um horário.");
        this.local = exigirValor(local, "A atividade deve ter um local.");
        this.trilha = exigirValor(trilha, "A atividade deve pertencer a uma trilha.");
        this.categoria = categoria;
    }

    public IdAtividade id() {
        return id;
    }

    public IdEvento eventoId() {
        return eventoId;
    }

    public String titulo() {
        return titulo;
    }

    public TipoDeAtividade tipo() {
        return tipo;
    }

    public HorarioDaAtividade horario() {
        return horario;
    }

    public Local local() {
        return local;
    }

    public Trilha trilha() {
        return trilha;
    }

    public Optional<Categoria> categoria() {
        return Optional.ofNullable(categoria);
    }

    /** RF-13: a capacidade desta atividade, quando houver. */
    public Optional<Integer> limiteDeVagas() {
        return Optional.ofNullable(limiteDeVagas);
    }

    /**
     * RF-13: define a capacidade, ou a remove com {@code null}.
     *
     * <p>Zero e negativo são recusados: uma atividade com zero vagas não é "limitada", é
     * inexistente, e aceitar isso adiaria o erro para o momento em que alguém tentasse se
     * inscrever e recebesse uma recusa sem explicação.</p>
     */
    public void definirLimiteDeVagas(Integer limite) {
        if (limite != null && limite <= 0) {
            throw new ValorInvalido("O limite de vagas da atividade deve ser positivo. Informado: " + limite + ".");
        }
        this.limiteDeVagas = limite;
    }

    /**
     * O controle de vagas desta atividade, vazio.
     *
     * <p>Quem sabe qual é a capacidade é a atividade, então é ela quem monta o controle — em vez
     * de cada chamador decidir entre {@code limitadoA} e {@code ilimitado} e um deles decidir
     * diferente. Cabe ao caso de uso preenchê-lo com as inscrições já confirmadas.</p>
     */
    public ControleDeVagas controleDeVagas() {
        return limiteDeVagas == null
                ? ControleDeVagas.ilimitado()
                : ControleDeVagas.limitadoA(limiteDeVagas);
    }

    /** RF-19: qual regra decide se alguém esteve presente nesta atividade. */
    public CriterioDePresenca criterioDePresenca() {
        return criterioDePresenca;
    }

    /**
     * RF-19: troca a regra de presença.
     *
     * <p>Trocar não reescreve marcação nenhuma — a situação é recalculada na próxima consulta.
     * É o que RN-12 e RN-17 exigem: mudar a regra não pode alterar o que foi registrado.</p>
     */
    public void definirCriterioDePresenca(CriterioDePresenca criterio) {
        this.criterioDePresenca =
                exigirValor(criterio, "A atividade precisa de um critério de presença.");
    }

    public void vincular(VinculoDePessoa vinculo) {
        exigirValor(vinculo, "O vínculo de pessoa não pode ser nulo.");
        if (vinculos.contains(vinculo)) {
            throw new ValorInvalido("A pessoa já está vinculada a esta atividade com o mesmo papel.");
        }
        vinculos.add(vinculo);
    }

    public List<VinculoDePessoa> vinculos() {
        return Collections.unmodifiableList(vinculos);
    }

    public boolean conflitaCom(Atividade outra) {
        exigirValor(outra, "A atividade comparada não pode ser nula.");
        return local.equals(outra.local) && horario.sobrepoe(outra.horario);
    }

    private static <T> T exigirValor(T valor, String mensagem) {
        if (valor == null) {
            throw new ValorInvalido(mensagem);
        }
        return valor;
    }
}
