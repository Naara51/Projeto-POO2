package br.ueg.eventos.dominio.evento;

import br.ueg.eventos.dominio.comum.IdEvento;
import br.ueg.eventos.dominio.comum.IdUsuario;
import br.ueg.eventos.dominio.comum.ValorInvalido;
import br.ueg.eventos.dominio.inscricao.PoliticaDeInscricao;

public class Evento {

    private final IdEvento id;
    private final IdUsuario organizadorId;

    private TituloDoEvento titulo;
    private DescricaoDoEvento descricao;
    private PeriodoDoEvento periodo;
    private ModalidadeDoEvento modalidade;
    private LocalDoEvento local;

    // RF-04, "regras gerais": as duas configuracoes que o evento impoe a quem se inscreve.
    private PoliticaDeInscricao politicaDeInscricao = PoliticaDeInscricao.EVENTO_COM_ESCOLHA_DE_ATIVIDADES;
    private AntecedenciaDeCancelamento antecedenciaDeCancelamento = AntecedenciaDeCancelamento.PADRAO;

    private SituacaoDoEvento situacao;

    /**
     * Cria um evento em {@code RASCUNHO}.
     *
     * @param local onde acontece; pode ser {@code null} quando a modalidade não exige
     */
    public Evento(
            IdEvento id,
            IdUsuario organizadorId,
            TituloDoEvento titulo,
            DescricaoDoEvento descricao,
            PeriodoDoEvento periodo,
            ModalidadeDoEvento modalidade,
            LocalDoEvento local) {

        if (id == null) {
            throw new ValorInvalido("O identificador do evento não pode ser nulo.");
        }
        if (organizadorId == null) {
            throw new ValorInvalido("O identificador do organizador não pode ser nulo.");
        }
        this.id = id;
        this.organizadorId = organizadorId;
        this.titulo = exigirTitulo(titulo);
        this.descricao = descricao == null ? new DescricaoDoEvento("") : descricao;
        this.periodo = exigirPeriodo(periodo);
        aplicarRealizacao(modalidade, local);
        this.situacao = SituacaoDoEvento.RASCUNHO;
    }

    /**
     * Traz de volta um evento já gravado, na situação em que ele estava.
     *
     * <h2>Por que isto não é um furo no encapsulamento</h2>
     *
     * <p>O construtor público sempre cria em {@code RASCUNHO}, e é assim que deve ser: um
     * evento nasce rascunho. Mas um adaptador de persistência não está <i>criando</i> um
     * evento — está trazendo de volta um que já existe, e cujas transições já aconteceram.
     * Sem este ponto de entrada, o adaptador teria de chamar {@code publicar} e
     * {@code encerrar} para "andar" até a situação gravada, o que registraria transições que
     * não estão acontecendo agora e exigiria conhecer o organizador para reidratar.</p>
     *
     * <p>O que ele <b>não</b> faz: afrouxar invariante nenhuma. Os três primeiros argumentos
     * passam pelas mesmas validações do construtor, e o evento devolvido continua obedecendo
     * às transições — reconstituir como {@code PUBLICADO} não permite publicar de novo.</p>
     *
     * <p>Uso previsto: exclusivamente adaptadores de {@code EventoRepository}.</p>
     */
    public static Evento reconstituir(
            IdEvento id,
            IdUsuario organizadorId,
            TituloDoEvento titulo,
            DescricaoDoEvento descricao,
            PeriodoDoEvento periodo,
            ModalidadeDoEvento modalidade,
            LocalDoEvento local,
            PoliticaDeInscricao politicaDeInscricao,
            AntecedenciaDeCancelamento antecedencia,
            SituacaoDoEvento situacao) {

        if (situacao == null) {
            throw new ValorInvalido("A reconstituição do evento exige a situação gravada.");
        }
        Evento evento = new Evento(id, organizadorId, titulo, descricao, periodo, modalidade, local);
        evento.situacao = situacao;
        // As regras gravadas voltam como estao. Deixar o padrao aqui faria um evento
        // SOMENTE_EVENTO virar EVENTO_COM_ESCOLHA_DE_ATIVIDADES ao ser relido do banco.
        if (politicaDeInscricao != null) {
            evento.politicaDeInscricao = politicaDeInscricao;
        }
        if (antecedencia != null) {
            evento.antecedenciaDeCancelamento = antecedencia;
        }
        return evento;
    }

    /**
     * RF-04 — corrige título e descrição.
     *
     * <p>Vale em rascunho e em publicado: um erro de digitação no nome não pode virar definitivo
     * porque o evento foi ao ar. Quem decide é {@code SituacaoDoEvento.aceitaCorrigirTexto()}, e
     * não um {@code if} aqui — acrescentar uma situação obriga a responder lá.</p>
     */
    public void corrigirApresentacao(
            OperadorDoEvento operador, TituloDoEvento titulo, DescricaoDoEvento descricao) {

        exigirOperador(operador);
        if (!situacao.aceitaCorrigirTexto()) {
            throw new EventoInvalido(
                    "Um evento " + situacao.name().toLowerCase() + " não tem mais o texto alterado.");
        }
        // Valida antes de atribuir qualquer coisa: uma recusa nao pode deixar o evento com
        // titulo novo e descricao velha.
        TituloDoEvento novoTitulo = exigirTitulo(titulo);

        this.titulo = novoTitulo;
        this.descricao = descricao == null ? new DescricaoDoEvento("") : descricao;
    }

    /**
     * RF-04 — muda período, modalidade e local.
     *
     * <p>Só em rascunho. Depois de publicado existe gente inscrita, e mudar a data ou a
     * modalidade altera aquilo a que essas pessoas disseram sim.</p>
     */
    public void mudarRealizacao(
            OperadorDoEvento operador,
            PeriodoDoEvento periodo,
            ModalidadeDoEvento modalidade,
            LocalDoEvento local) {

        exigirOperador(operador);
        if (!situacao.aceitaMudarRealizacao()) {
            throw new EventoInvalido(
                    "Um evento " + situacao.name().toLowerCase()
                            + " não muda de data, local ou modalidade: há inscrições feitas sobre eles.");
        }
        PeriodoDoEvento novoPeriodo = exigirPeriodo(periodo);

        aplicarRealizacao(modalidade, local);
        this.periodo = novoPeriodo;
    }

    // ------------------------------------------------------------------ invariantes

    private void aplicarRealizacao(ModalidadeDoEvento modalidade, LocalDoEvento local) {
        if (modalidade == null) {
            throw new ValorInvalido("A modalidade do evento não pode ser nula.");
        }
        // Quem sabe se exige local e a modalidade. Aqui so se pergunta.
        if (modalidade.exigeLocal() && local == null) {
            throw new ValorInvalido(
                    "Um evento " + modalidade.name().toLowerCase() + " exige o local onde acontece.");
        }
        this.modalidade = modalidade;
        this.local = local;
    }

    private static TituloDoEvento exigirTitulo(TituloDoEvento titulo) {
        if (titulo == null) {
            throw new ValorInvalido("O título do evento não pode ser nulo.");
        }
        return titulo;
    }

    private static PeriodoDoEvento exigirPeriodo(PeriodoDoEvento periodo) {
        if (periodo == null) {
            throw new ValorInvalido("O período do evento não pode ser nulo.");
        }
        return periodo;
    }

    /**
     * RF-04 — as regras gerais: política de inscrição (RF-13) e antecedência de cancelamento
     * (RF-15).
     *
     * <p>Segue a mesma restrição de período, local e modalidade: só em rascunho. As duas
     * alteram aquilo a que quem já se inscreveu disse sim — mudar de
     * {@code EVENTO_COM_ESCOLHA_DE_ATIVIDADES} para {@code SOMENTE_EVENTO} tornaria inválidas
     * escolhas já feitas, e encurtar o prazo surpreenderia quem contava com ele.</p>
     */
    public void definirRegrasGerais(
            OperadorDoEvento operador,
            PoliticaDeInscricao politicaDeInscricao,
            AntecedenciaDeCancelamento antecedencia) {

        exigirOperador(operador);
        if (!situacao.aceitaMudarRealizacao()) {
            throw new EventoInvalido(
                    "Um evento " + situacao.name().toLowerCase()
                            + " não muda as regras de inscrição e cancelamento: há inscrições feitas sob elas.");
        }
        if (politicaDeInscricao == null) {
            throw new ValorInvalido("A política de inscrição do evento não pode ser nula.");
        }
        if (antecedencia == null) {
            throw new ValorInvalido("A antecedência de cancelamento não pode ser nula.");
        }
        this.politicaDeInscricao = politicaDeInscricao;
        this.antecedenciaDeCancelamento = antecedencia;
    }

    /** RF-13: o evento exige escolher atividade, dispensa, ou deixa opcional? */
    public PoliticaDeInscricao politicaDeInscricao() {
        return politicaDeInscricao;
    }

    /** RF-15: quantas horas antes do início o cancelamento ainda vale. */
    public AntecedenciaDeCancelamento antecedenciaDeCancelamento() {
        return antecedenciaDeCancelamento;
    }

    /**
     * RF-15 — o instante-limite para cancelar uma inscrição deste evento.
     *
     * <p>Calculado aqui porque é aqui que moram as duas metades: o início e a antecedência.
     * Enquanto o prazo vinha de fora, quem estava sendo verificado é que informava o limite —
     * ver decisão E-13.</p>
     */
    public java.time.Instant prazoDeCancelamento() {
        return antecedenciaDeCancelamento.prazoAPartirDe(periodo.inicio());
    }

    public TituloDoEvento titulo() {
        return titulo;
    }

    public DescricaoDoEvento descricao() {
        return descricao;
    }

    public ModalidadeDoEvento modalidade() {
        return modalidade;
    }

    /** Onde acontece. Vazio quando a modalidade não exige e nada foi informado. */
    public java.util.Optional<LocalDoEvento> local() {
        return java.util.Optional.ofNullable(local);
    }

    public IdEvento id() {
        return id;
    }

    public IdUsuario organizadorId() {
        return organizadorId;
    }

    public PeriodoDoEvento periodo() {
        return periodo;
    }

    public void publicar(OperadorDoEvento operador) {
        exigirOperador(operador);
        if (situacao != SituacaoDoEvento.RASCUNHO) {
            throw new EventoInvalido("Somente evento em rascunho pode ser publicado.");
        }
        situacao = SituacaoDoEvento.PUBLICADO;
    }

    public void encerrar(OperadorDoEvento operador) {
        exigirOperador(operador);
        if (situacao != SituacaoDoEvento.PUBLICADO) {
            throw new EventoInvalido("Somente evento publicado pode ser encerrado.");
        }
        situacao = SituacaoDoEvento.ENCERRADO;
    }

    /** RF-10. A regra vive em {@link SituacaoDoEvento}, para que o adaptador de persistência
     * possa derivar dela a sua cláusula {@code WHERE} em vez de repeti-la. */
    public boolean estaVisivelPublicamente() {
        return situacao.visivelPublicamente();
    }

    public SituacaoDoEvento situacao() {
        return situacao;
    }

    /**
     * RN-18: quem altera dados operacionais do evento.
     *
     * <p>A regra tem duas metades, e por muito tempo só a primeira estava aqui. A que exclui:
     * participante e visitante nunca passam — nem chegam, porque a camada de aplicação exige
     * {@code GERENCIAR_EVENTO} antes. A que inclui: <i>"somente <b>administrador ou</b>
     * organizador autorizado"</i> — o administrador é nomeado primeiro na regra, e era recusado
     * junto com o intruso.</p>
     *
     * <p>Quem decide o alcance não é este agregado: ele recebe a decisão pronta em
     * {@link OperadorDoEvento}, no mesmo formato de {@code AutorizacaoDeFrequencia}. Assim o
     * pacote {@code evento} continua sem conhecer perfis.</p>
     */
    private void exigirOperador(OperadorDoEvento operador) {
        if (operador == null || !operador.alcanca(organizadorId)) {
            throw new EventoInvalido(
                    "Este evento só pode ser alterado pelo organizador dele "
                            + "ou por quem tem acesso administrativo.");
        }
    }
}
