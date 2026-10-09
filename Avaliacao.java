package br.ueg.eventos.dominio.avaliacao;

import br.ueg.eventos.dominio.comum.IdUsuario;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A submissão de um participante a um questionário: o conjunto de respostas enviado
 * de uma vez, com data, hora e situação.
 *
 * <h2>Esta é a classe que o mapa 002 chamava de "Avaliacao"</h2>
 *
 * <p>Na versão original ela tinha um campo {@code conteudoResposta: String} e três
 * métodos: {@code validarFormato}, {@code verificarElegibilidade} e
 * {@code bloquearMultiplasTentativas}. As três responsabilidades saíram daqui, cada
 * uma por um motivo objetivo — o raciocínio completo está em
 * {@code docs/02-modelagem-e-decisoes.md}. O que sobrou é o que uma submissão
 * realmente é: <b>um registro histórico do que alguém respondeu, quando</b>.</p>
 *
 * <h2>Só o Questionário cria uma Avaliação (Factory Method — ROO-10)</h2>
 *
 * <p>O construtor tem visibilidade de pacote e não há {@code new Avaliacao(...)}
 * possível fora do domínio. O único caminho é {@link Questionario#responder}, que
 * confere a elegibilidade antes. Se o construtor fosse público, alguém — em três
 * meses, com pressa — criaria uma avaliação direto e furaria a RN-13 sem perceber.</p>
 *
 * <p>Isto responde literalmente ao documento 004: <i>"só se deixa criar após confirmar
 * com o Questionário que o participante é elegível; não apura a elegibilidade por
 * conta própria"</i>. A garantia não é um comentário pedindo cuidado — é o compilador.</p>
 *
 * <h2>A política de identificação é congelada aqui (RN-15 e RN-17)</h2>
 *
 * <p>{@link #politicaVigente} é copiada do questionário <b>no momento do envio</b> e
 * nunca mais muda. O motivo é a RN-17: se o organizador trocasse o questionário de
 * anônimo para identificado depois de trinta pessoas terem respondido, aquelas trinta
 * respostas — enviadas sob a promessa de anonimato — passariam a exibir os nomes.
 * Seria a alteração silenciosa de registro histórico que a regra proíbe, e uma quebra
 * de confiança com quem respondeu.</p>
 *
 * <p>Guardando a política dentro da submissão, uma mudança futura no questionário
 * afeta apenas as submissões futuras. É o mesmo princípio de uma nota fiscal, que
 * guarda a alíquota vigente na data da venda em vez de consultar a tabela atual.</p>
 */
public final class Avaliacao {

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final Questionario questionario;
    private final IdUsuario autor;
    private final LocalDateTime dataHoraSubmissao;
    private final PoliticaDeIdentificacao politicaVigente;
    private final List<Resposta> respostas;

    /** Único campo mutável da classe, e só na direção VALIDA -> SUBSTITUIDA. */
    private SituacaoDaAvaliacao situacao;

    Avaliacao(Questionario questionario,
              IdUsuario autor,
              List<RespostaInformada> respostasInformadas,
              LocalDateTime dataHoraSubmissao) {

        this.questionario = Objects.requireNonNull(questionario);
        this.autor = Objects.requireNonNull(autor);
        this.dataHoraSubmissao = Objects.requireNonNull(dataHoraSubmissao);
        this.politicaVigente = questionario.politicaDeIdentificacao(); // congelada
        this.respostas = montarRespostas(questionario, respostasInformadas);
        this.situacao = SituacaoDaAvaliacao.VALIDA;
    }

    /**
     * Reidrata uma submissão já gravada.
     *
     * <p>Visibilidade de pacote, como o outro construtor: continua sendo impossível criar uma
     * {@code Avaliacao} de fora. Quem chama é {@link Questionario#reconstituir}.</p>
     *
     * <p>Diferenças em relação ao construtor normal, e o motivo de cada uma:</p>
     *
     * <ul>
     *   <li>a política vigente vem <b>gravada</b>, não lida do questionário atual — senão
     *       trocar a política do questionário revelaria retroativamente autores anônimos,
     *       violando RN-15;</li>
     *   <li>a situação vem gravada, inclusive SUBSTITUIDA — reexecutar a
     *       {@link PoliticaDeEdicao} na releitura reescreveria o histórico que RN-17 protege;</li>
     *   <li>as respostas continuam passando pela validação de {@link Resposta}: conteúdo que
     *       não serve para a sua pergunta é dado corrompido, não fato do passado.</li>
     * </ul>
     */
    Avaliacao(Questionario questionario,
              IdUsuario autor,
              List<Resposta> respostasGravadas,
              LocalDateTime dataHoraSubmissao,
              PoliticaDeIdentificacao politicaVigente,
              SituacaoDaAvaliacao situacao) {

        this.questionario = Objects.requireNonNull(questionario);
        this.autor = Objects.requireNonNull(autor);
        this.dataHoraSubmissao = Objects.requireNonNull(dataHoraSubmissao);
        this.politicaVigente = Objects.requireNonNull(politicaVigente);
        this.respostas = List.copyOf(Objects.requireNonNull(respostasGravadas));
        this.situacao = Objects.requireNonNull(situacao);
    }

    /**
     * Constrói as respostas e verifica o que só a submissão inteira consegue verificar.
     *
     * <p>Repare que a validação de <i>formato</i> não aparece aqui: ela acontece dentro
     * do construtor de {@link Resposta}, que pergunta à {@link Pergunta}. Esta classe
     * nunca precisa saber que existem três tipos de pergunta — é o efeito prático do
     * polimorfismo do ROO-05.</p>
     */
    private static List<Resposta> montarRespostas(Questionario questionario,
                                                  List<RespostaInformada> informadas) {

        if (informadas == null) {
            throw new SubmissaoInvalida("A submissão precisa conter a lista de respostas.");
        }

        List<Pergunta> perguntas = questionario.perguntas();
        List<Resposta> respostas = new ArrayList<>();

        // Pergunta nao sobrescreve equals: a comparacao e por identidade, que e o
        // comportamento correto aqui. Duas perguntas com o mesmo enunciado no mesmo
        // questionario continuam sendo duas perguntas distintas.
        Set<Pergunta> jaRespondidas = new HashSet<>();

        for (RespostaInformada informada : informadas) {
            Pergunta pergunta = informada.pergunta();

            if (!perguntas.contains(pergunta)) {
                throw new SubmissaoInvalida(
                        "A pergunta \"" + pergunta.enunciado() + "\" não pertence ao questionário \""
                                + questionario.titulo() + "\".");
            }
            if (!jaRespondidas.add(pergunta)) {
                throw new SubmissaoInvalida(
                        "A pergunta \"" + pergunta.enunciado()
                                + "\" foi respondida mais de uma vez na mesma submissão.");
            }

            respostas.add(new Resposta(pergunta, informada.conteudo()));
        }

        for (Pergunta pergunta : perguntas) {
            if (pergunta.obrigatoria() && !foiPreenchida(respostas, pergunta)) {
                throw new SubmissaoInvalida(
                        "A pergunta obrigatória \"" + pergunta.enunciado() + "\" não foi respondida.");
            }
        }

        return List.copyOf(respostas);
    }

    private static boolean foiPreenchida(List<Resposta> respostas, Pergunta pergunta) {
        return respostas.stream()
                .anyMatch(resposta -> resposta.pergunta() == pergunta && resposta.preenchida());
    }

    /**
     * Marca esta submissão como substituída por uma mais recente.
     *
     * <p>Visibilidade de pacote e acionado apenas por
     * {@link PoliticaDeEdicao#SUBSTITUI_ANTERIOR}. A submissão <b>não é apagada</b>:
     * a RN-17 exige preservar o registro histórico, e o documento 004 é explícito —
     * <i>"altera a própria situação para Substituída em vez de ser apagada"</i>.</p>
     */
    void marcarComoSubstituida() {
        if (situacao != SituacaoDaAvaliacao.VALIDA) {
            throw new IllegalStateException(
                    "Só uma submissão válida pode ser substituída. Situação atual: " + situacao + ".");
        }
        this.situacao = SituacaoDaAvaliacao.SUBSTITUIDA;
    }

    /**
     * Conteúdo respondido para uma pergunta específica, ou string vazia se a pergunta
     * ficou em branco. Usado pelo {@link Questionario} ao consolidar.
     */
    String conteudoDe(Pergunta pergunta) {
        return respostas.stream()
                .filter(resposta -> resposta.pergunta() == pergunta)
                .map(Resposta::conteudo)
                .findFirst()
                .orElse("");
    }

    /**
     * Identidade real do autor.
     *
     * <p>Visibilidade de pacote <b>de propósito</b>: o {@link Questionario} precisa
     * dela para aplicar a RN-14 (uma resposta válida por participante), mas nenhum
     * código fora do domínio deve conseguir obtê-la. Quem está do lado de fora só tem
     * {@link #autorApresentavel()}, que já respeita a política de identificação.</p>
     *
     * <p>É a diferença entre confiar que ninguém vai vazar o nome e tornar o vazamento
     * impossível de escrever.</p>
     */
    IdUsuario autor() {
        return autor;
    }

    /** Nome do autor ou "Anônimo", conforme a política congelada no envio (RN-15). */
    public String autorApresentavel() {
        return politicaVigente.apresentarAutor(autor);
    }

    public boolean valida() {
        return situacao == SituacaoDaAvaliacao.VALIDA;
    }

    public SituacaoDaAvaliacao situacao() {
        return situacao;
    }

    public LocalDateTime dataHoraSubmissao() {
        return dataHoraSubmissao;
    }

    public PoliticaDeIdentificacao politicaVigente() {
        return politicaVigente;
    }

    public Questionario questionario() {
        return questionario;
    }

    /** Lista imutável: {@code List.copyOf} na construção, sem setter e sem add. */
    public List<Resposta> respostas() {
        return respostas;
    }

    public String apresentacaoTextual() {
        StringBuilder texto = new StringBuilder();
        texto.append("Submissão de ").append(autorApresentavel())
                .append(" em ").append(dataHoraSubmissao.format(FORMATO))
                .append(" [").append(situacao.descricao()).append("]\n");
        for (Resposta resposta : respostas) {
            texto.append("  ").append(resposta.apresentacaoLegivel()).append('\n');
        }
        return texto.toString();
    }

    @Override
    public String toString() {
        return "Avaliação de " + autorApresentavel() + " (" + situacao.descricao() + ")";
    }
}
