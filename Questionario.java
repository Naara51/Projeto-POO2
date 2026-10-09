package br.ueg.eventos.dominio.avaliacao;

import br.ueg.eventos.dominio.comum.IdUsuario;
import br.ueg.eventos.dominio.comum.ValorInvalido;
import br.ueg.eventos.dominio.programacao.AtividadeAvaliavel;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * O instrumento de avaliação de uma atividade: título, instruções, perguntas,
 * políticas e a janela em que aceita respostas (RF-24).
 *
 * <h2>É a raiz do subdomínio</h2>
 *
 * <p>Todo acesso ao subdomínio de Avaliação passa por aqui. Perguntas são adicionadas
 * pelo questionário; submissões são criadas pelo questionário; a consolidação é pedida
 * ao questionário. Nenhuma dessas operações tem entrada alternativa — e é isso que
 * permite garantir as regras: se houvesse dois caminhos para criar uma submissão, um
 * deles esqueceria de verificar a elegibilidade.</p>
 *
 * <h2>As três responsabilidades que chegaram aqui vindas da Avaliação</h2>
 *
 * <ol>
 *   <li><b>Bloquear múltiplas tentativas (RF-27, RN-14).</b> Uma submissão isolada não
 *       tem como conhecer as outras submissões do mesmo participante. Quem tem essa
 *       visão é o questionário, que as guarda todas.</li>
 *   <li><b>Verificar elegibilidade (RF-26, RN-13).</b> Depende da atividade, e o
 *       questionário é quem conhece a atividade. Com isso a {@link Avaliacao} não
 *       precisa depender de inscrição nem de presença.</li>
 *   <li><b>Impedir alteração após a primeira submissão (RN-17).</b> Só o questionário
 *       sabe se já existe submissão.</li>
 * </ol>
 *
 * <p>O ganho de acoplamento é mensurável: o subdomínio inteiro depende de <b>uma</b>
 * abstração externa, {@link AtividadeAvaliavel}, com três métodos. Sem essa
 * reorganização, dependeria também de {@code Inscricao} e {@code Presenca}.</p>
 *
 * <h2>Uma observação sobre o pacote</h2>
 *
 * <p>Todas as classes deste subdomínio ficam no mesmo pacote de propósito. Isso é o
 * que torna possível dar visibilidade de pacote ao construtor da {@link Avaliacao}, ao
 * construtor da {@link Resposta} e ao método {@link Avaliacao#autor()}. O pacote é a
 * fronteira de encapsulamento: de fora, só existe o que é {@code public}, e o que é
 * público é exatamente o que faz sentido usar.</p>
 */
public final class Questionario {

    private final String titulo;
    private final String instrucoes;
    private final AtividadeAvaliavel atividade;
    private final PoliticaDeIdentificacao politicaDeIdentificacao;
    private final PoliticaDeEdicao politicaDeEdicao;
    private final JanelaDeRespostas janelaDeRespostas;

    private final List<Pergunta> perguntas = new ArrayList<>();
    private final List<Avaliacao> submissoes = new ArrayList<>();

    public Questionario(String titulo,
                        String instrucoes,
                        AtividadeAvaliavel atividade,
                        PoliticaDeIdentificacao politicaDeIdentificacao,
                        PoliticaDeEdicao politicaDeEdicao,
                        JanelaDeRespostas janelaDeRespostas) {

        if (titulo == null || titulo.isBlank()) {
            throw new ValorInvalido("O questionário precisa de um título.");
        }
        this.titulo = titulo.strip();
        this.instrucoes = instrucoes == null ? "" : instrucoes.strip();
        this.atividade = Objects.requireNonNull(atividade,
                "O questionário precisa estar vinculado a uma atividade avaliável.");
        this.politicaDeIdentificacao = Objects.requireNonNull(politicaDeIdentificacao,
                "A política de identificação deve ser informada antes do envio (RN-15).");
        this.politicaDeEdicao = Objects.requireNonNull(politicaDeEdicao,
                "A política de edição deve ser definida na criação do questionário (RF-27).");
        this.janelaDeRespostas = Objects.requireNonNull(janelaDeRespostas,
                "O questionário precisa de uma janela de respostas.");
    }

    /**
     * Traz de volta um questionário já gravado, com perguntas e submissões.
     *
     * <h2>Por que não reusar {@code adicionarPergunta} e {@code responder}</h2>
     *
     * <p>Os dois métodos aplicam regras que valem <b>na hora de criar</b>, e que seriam
     * erradas na releitura:</p>
     *
     * <ul>
     *   <li>{@code adicionarPergunta} recusa alteração depois da primeira submissão (RN-17).
     *       Um questionário gravado tem as duas coisas, então reidratar por ele seria
     *       impossível na ordem certa e desonesto na ordem errada;</li>
     *   <li>{@code responder} verifica elegibilidade (RN-13) contra o estado de <b>hoje</b>.
     *       Uma resposta enviada por quem estava inscrito e depois cancelou a inscrição
     *       desapareceria na releitura — o sistema perderia dado por reaplicar regra sobre
     *       fato passado;</li>
     *   <li>{@code responder} também reexecutaria a {@link PoliticaDeEdicao}, reescrevendo
     *       quais submissões valem. A situação gravada é o que RN-17 manda preservar.</li>
     * </ul>
     *
     * <p>O que <b>continua</b> sendo verificado: cada conteúdo passa pelo construtor de
     * {@link Resposta} e é validado pela sua pergunta. Conteúdo que não serve é dado
     * corrompido, e não um fato do passado a preservar.</p>
     *
     * <p>As respostas de cada submissão são casadas com as perguntas <b>por posição</b>. É a
     * mesma decisão que faz a ordem das perguntas ser a ordem da lista: não há campo
     * {@code ordem} em {@link Pergunta} para não existirem duas fontes de verdade.</p>
     *
     * <p>Uso previsto: exclusivamente adaptadores de {@code AvaliacaoRepository}.</p>
     */
    public static Questionario reconstituir(String titulo,
                                            String instrucoes,
                                            AtividadeAvaliavel atividade,
                                            PoliticaDeIdentificacao politicaDeIdentificacao,
                                            PoliticaDeEdicao politicaDeEdicao,
                                            JanelaDeRespostas janelaDeRespostas,
                                            List<Pergunta> perguntasGravadas,
                                            List<SubmissaoReconstituida> submissoesGravadas) {

        Questionario questionario = new Questionario(
                titulo, instrucoes, atividade, politicaDeIdentificacao, politicaDeEdicao, janelaDeRespostas);

        Objects.requireNonNull(perguntasGravadas, "A reconstituição exige a lista de perguntas.");
        Objects.requireNonNull(submissoesGravadas, "A reconstituição exige a lista de submissões.");

        questionario.perguntas.addAll(perguntasGravadas);

        for (SubmissaoReconstituida gravada : submissoesGravadas) {
            questionario.submissoes.add(questionario.montarSubmissao(gravada));
        }
        return questionario;
    }

    /**
     * As submissões na forma em que devem ser gravadas — o inverso de {@link #reconstituir}.
     *
     * <h2>Por que o adaptador não lê a Avaliacao diretamente</h2>
     *
     * <p>{@code Avaliacao.autor()} tem visibilidade de pacote, e isso não é descuido: é o que
     * impede uma tela de contornar {@code PoliticaDeIdentificacao.apresentarAutor} e revelar
     * quem respondeu anonimamente (RN-15). Tornar o método público para atender à persistência
     * abriria exatamente esse caminho para todo o resto do sistema.</p>
     *
     * <p>Esta visão resolve sem abrir nada: ela devolve {@link SubmissaoReconstituida}, que é o
     * <b>mesmo</b> tipo que {@link #reconstituir} recebe. O adaptador grava o que vai reler, e o
     * ciclo fecha — há um teste que compara os dois lados justamente para garantir isso.</p>
     */
    public List<SubmissaoReconstituida> submissoesParaPersistencia() {
        return submissoes.stream()
                .map(avaliacao -> new SubmissaoReconstituida(
                        avaliacao.autor(),
                        avaliacao.dataHoraSubmissao(),
                        avaliacao.politicaVigente(),
                        avaliacao.situacao(),
                        avaliacao.respostas().stream().map(Resposta::conteudo).toList()))
                .toList();
    }

    private Avaliacao montarSubmissao(SubmissaoReconstituida gravada) {
        if (gravada.conteudos().size() > perguntas.size()) {
            throw new SubmissaoInvalida(
                    "A submissão gravada tem mais respostas do que o questionário \"" + titulo
                            + "\" tem perguntas.");
        }
        List<Resposta> respostas = new ArrayList<>();
        for (int posicao = 0; posicao < gravada.conteudos().size(); posicao++) {
            respostas.add(Resposta.reconstituir(perguntas.get(posicao), gravada.conteudos().get(posicao)));
        }
        return new Avaliacao(
                this,
                gravada.autor(),
                respostas,
                gravada.dataHoraSubmissao(),
                gravada.politicaVigente(),
                gravada.situacao());
    }

    // ------------------------------------------------------------------
    // Configuração (RF-24)
    // ------------------------------------------------------------------

    /**
     * Acrescenta uma pergunta ao fim do questionário.
     *
     * <p>A ordem das perguntas é a ordem desta lista — não existe campo
     * {@code ordem} em {@link Pergunta}, justamente para não haver duas fontes de
     * verdade que possam divergir.</p>
     *
     * @throws QuestionarioBloqueado se já houver qualquer submissão (RN-17)
     */
    public void adicionarPergunta(Pergunta pergunta) {
        Objects.requireNonNull(pergunta, "Pergunta não pode ser nula.");
        garantirQueAindaPodeSerAlterado();
        perguntas.add(pergunta);
    }

    private void garantirQueAindaPodeSerAlterado() {
        if (!submissoes.isEmpty()) {
            throw new QuestionarioBloqueado(
                    "O questionário \"" + titulo + "\" já recebeu " + submissoes.size()
                            + " submissão(ões) e não pode mais ser alterado. "
                            + "Alterar as perguntas agora mudaria o significado das respostas "
                            + "já consolidadas (RN-17).");
        }
    }

    // ------------------------------------------------------------------
    // Elegibilidade (RF-26, RN-13)
    // ------------------------------------------------------------------

    /**
     * Apura se um participante pode responder e, se não puder, por quê.
     *
     * <p>A ordem das verificações é intencional: vai do impedimento mais permanente
     * para o mais circunstancial. Quem não está inscrito precisa se inscrever; quem
     * está inscrito mas sem presença precisa que a presença seja validada; quem tem
     * tudo isso pode estar apenas fora do horário. Informar o primeiro obstáculo real
     * é mais útil do que informar o último verificado.</p>
     *
     * <p>Este método <b>não lança exceção</b>. Perguntar se pode responder é diferente
     * de tentar responder — a tela que decide exibir ou não o botão precisa perguntar
     * sem que isso seja um erro. Quem tenta é {@link #responder}, e essa sim lança.
     * As duas usam esta mesma apuração, então não há como divergirem.</p>
     */
    public ResultadoDeElegibilidade verificarElegibilidade(IdUsuario participante,
                                                          LocalDateTime momento) {
        Objects.requireNonNull(participante, "Participante não pode ser nulo.");
        Objects.requireNonNull(momento, "O momento da verificação deve ser informado.");

        if (!atividade.estaInscrito(participante)) {
            return ResultadoDeElegibilidade.impedidoPor(MotivoDeInelegibilidade.NAO_INSCRITO);
        }
        if (!atividade.temPresencaValidada(participante)) {
            return ResultadoDeElegibilidade.impedidoPor(MotivoDeInelegibilidade.SEM_PRESENCA_VALIDADA);
        }
        if (!janelaDeRespostas.contem(momento)) {
            return ResultadoDeElegibilidade.impedidoPor(MotivoDeInelegibilidade.FORA_DA_JANELA_DE_RESPOSTAS);
        }
        if (submissaoValidaDe(participante).isPresent() && !politicaDeEdicao.permiteSubstituicao()) {
            return ResultadoDeElegibilidade.impedidoPor(MotivoDeInelegibilidade.JA_RESPONDIDO);
        }
        return ResultadoDeElegibilidade.apto();
    }

    // ------------------------------------------------------------------
    // Submissão (RF-25, RF-27, RN-14)
    // ------------------------------------------------------------------

    /**
     * Registra a resposta de um participante. É o <b>único</b> caminho para criar uma
     * {@link Avaliacao} (Factory Method — ROO-10).
     *
     * @throws QuestionarioBloqueado  se o questionário não tiver perguntas
     * @throws ParticipanteInelegivel se a RN-13 ou a RN-14 impedirem, com o motivo
     * @throws RespostaInvalida       se alguma resposta não servir para a sua pergunta
     * @throws SubmissaoInvalida      se faltar obrigatória, houver repetida ou estranha
     */
    public Avaliacao responder(IdUsuario participante,
                               List<RespostaInformada> respostasInformadas,
                               LocalDateTime momento) {

        if (perguntas.isEmpty()) {
            throw new QuestionarioBloqueado(
                    "O questionário \"" + titulo + "\" ainda não tem perguntas e não pode receber respostas.");
        }

        ResultadoDeElegibilidade elegibilidade = verificarElegibilidade(participante, momento);
        if (!elegibilidade.elegivel()) {
            throw new ParticipanteInelegivel(elegibilidade.motivo().orElseThrow());
        }

        // A ORDEM DAS TRES LINHAS ABAIXO IMPORTA.
        //
        // A nova submissao e construida PRIMEIRO, porque a construcao valida as
        // respostas e pode falhar. Se a anterior fosse marcada como substituida antes,
        // uma submissao invalida deixaria o participante sem nenhuma resposta valida:
        // a antiga ja aposentada e a nova nunca criada. Construir antes de alterar
        // estado torna a operacao tudo-ou-nada.
        Avaliacao nova = new Avaliacao(this, participante, respostasInformadas, momento);
        submissaoValidaDe(participante).ifPresent(politicaDeEdicao::aplicarSobre);
        submissoes.add(nova);

        return nova;
    }

    /** A submissão que vale para este participante, se existir (RN-14). */
    public Optional<Avaliacao> submissaoValidaDe(IdUsuario participante) {
        return submissoes.stream()
                .filter(Avaliacao::valida)
                .filter(avaliacao -> avaliacao.autor().equals(participante))
                .findFirst();
    }

    // ------------------------------------------------------------------
    // Consolidação (RF-28)
    // ------------------------------------------------------------------

    /**
     * Consolida as submissões válidas em quantidades, distribuições e comentários.
     *
     * <p>Repare no laço: ele percorre as perguntas e chama
     * {@link Pergunta#consolidar(List)} em cada uma. <b>Não existe um único
     * {@code if} ou {@code switch} sobre o tipo da pergunta.</b> Texto, escolha e
     * escala consolidam de formas completamente diferentes, e este método não sabe de
     * nenhuma delas. É o que o item 8.4 pede no ROO-05, e a razão pela qual acrescentar
     * um quarto tipo de pergunta não exige tocar nesta classe.</p>
     *
     * <p>A anonimização acontece em {@link Avaliacao#autorApresentavel()}, uma linha
     * antes de os dados entrarem na consolidação. Daí em diante a identidade real não
     * existe mais no fluxo — ver {@link RespostaConsolidavel}.</p>
     */
    public ResultadoConsolidado consolidar() {
        List<Avaliacao> validas = submissoesValidas();
        List<ResumoDaPergunta> resumos = new ArrayList<>();

        for (Pergunta pergunta : perguntas) {
            List<RespostaConsolidavel> respostas = validas.stream()
                    .map(avaliacao -> new RespostaConsolidavel(
                            avaliacao.autorApresentavel(),
                            avaliacao.conteudoDe(pergunta)))
                    .filter(RespostaConsolidavel::preenchida)
                    .toList();

            resumos.add(pergunta.consolidar(respostas));
        }

        return new ResultadoConsolidado(
                titulo, atividade.titulo(), politicaDeIdentificacao, validas.size(), resumos);
    }

    public List<Avaliacao> submissoesValidas() {
        return submissoes.stream().filter(Avaliacao::valida).toList();
    }

    /** Todas as submissões, inclusive as substituídas — o histórico exigido pela RN-17. */
    public List<Avaliacao> historicoDeSubmissoes() {
        return List.copyOf(submissoes);
    }

    // ------------------------------------------------------------------
    // Consultas
    // ------------------------------------------------------------------

    /**
     * Lista somente para leitura.
     *
     * <p>Devolver {@code perguntas} diretamente permitiria que qualquer chamador
     * fizesse {@code questionario.perguntas().add(...)} e furasse a RN-17 sem passar
     * por {@link #adicionarPergunta}. {@link Collections#unmodifiableList} faz a
     * tentativa falhar em vez de funcionar em silêncio (ROO-02).</p>
     */
    public List<Pergunta> perguntas() {
        return Collections.unmodifiableList(perguntas);
    }

    public boolean estaAbertoEm(LocalDateTime momento) {
        return janelaDeRespostas.contem(momento);
    }

    public boolean podeSerAlterado() {
        return submissoes.isEmpty();
    }

    public String titulo() {
        return titulo;
    }

    public String instrucoes() {
        return instrucoes;
    }

    public AtividadeAvaliavel atividade() {
        return atividade;
    }

    public PoliticaDeIdentificacao politicaDeIdentificacao() {
        return politicaDeIdentificacao;
    }

    public PoliticaDeEdicao politicaDeEdicao() {
        return politicaDeEdicao;
    }

    public JanelaDeRespostas janelaDeRespostas() {
        return janelaDeRespostas;
    }

    @Override
    public String toString() {
        return "Questionário \"" + titulo + "\" (" + perguntas.size() + " perguntas, "
                + submissoesValidas().size() + " respostas válidas)";
    }
}
