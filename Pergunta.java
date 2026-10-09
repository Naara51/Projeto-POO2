package br.ueg.eventos.dominio.avaliacao;

import br.ueg.eventos.dominio.comum.ValorInvalido;

import java.util.List;

/**
 * Uma pergunta de um questionário. Classe abstrata: o que varia entre os tipos de
 * pergunta é <b>como</b> ela valida uma resposta e <b>como</b> ela consolida as
 * respostas recebidas.
 *
 * <h2>O ponto de variação central do subdomínio (ROO-05)</h2>
 *
 * <p>O RF-25 exige "ao menos resposta textual, escolha única e escala numérica".
 * A forma procedural de atender isso seria um campo {@code String tipo} e, na
 * validação, um {@code switch (tipo)} com três casos. O problema aparece na
 * consolidação (RF-28), que também depende do tipo: seria um <b>segundo</b> switch,
 * com os mesmos três casos, em outro arquivo. Acrescentar "múltipla escolha" no
 * futuro exigiria achar os dois e alterar os dois — e a página 12 do documento de
 * requisitos chama isso pelo nome: "evitando cadeias extensas de if/switch por tipo".</p>
 *
 * <p>Com herança, cada tipo responde pelas duas coisas em um arquivo só, e o
 * {@link Questionario} nunca pergunta de que tipo a pergunta é. Um quarto tipo é uma
 * classe nova; nenhum código existente muda. A própria página 13 do documento sugere
 * este caminho para questionários: <i>"composição ou hierarquia de perguntas e
 * validação polimórfica de respostas"</i>.</p>
 *
 * <h2>Padrão de projeto: Template Method (ROO-10)</h2>
 *
 * <p>{@link #validar(String)} é {@code final} e define o esqueleto invariante:</p>
 *
 * <ol>
 *   <li>normaliza o conteúdo (nulo e espaços viram string vazia);</li>
 *   <li>trata o caso "não respondeu", que é igual para todos os tipos — recusa se a
 *       pergunta for obrigatória, aceita em silêncio se for opcional;</li>
 *   <li>só então delega o que é específico para {@link #validarConteudo(String)}.</li>
 * </ol>
 *
 * <p>O ganho é concreto: nenhuma subclasse precisa lembrar de tratar nulo nem de
 * verificar obrigatoriedade. Se cada uma implementasse {@code validar} inteiro, uma
 * delas esqueceria — e o bug apareceria só em produção, com um {@code NullPointerException}.
 * O passo fixo fica na superclasse; o passo variável é o abstrato.</p>
 *
 * <h2>Por que Pergunta não guarda a sua ordem</h2>
 *
 * <p>O documento 004 diz que a pergunta sabe "a sua ordem dentro do questionário".
 * Um campo {@code int ordem} criaria duas fontes de verdade: o campo e a posição na
 * lista do questionário. Elas divergiriam na primeira reordenação. A lista <b>é</b> a
 * ordem — é o que {@link Questionario#perguntas()} devolve. Decisão registrada em
 * {@code docs/02-modelagem-e-decisoes.md}.</p>
 *
 * <h2>Por que Pergunta não é um objeto de valor</h2>
 *
 * <p>Duas perguntas com o mesmo enunciado, no mesmo questionário, são duas perguntas
 * diferentes — cada uma com as suas respostas. Portanto {@code equals} por conteúdo
 * seria errado: a comparação correta aqui é por identidade, que é o comportamento
 * padrão do Java. Nem tudo é objeto de valor, e o ROO-03 pede justamente critério
 * ("quando identidade não for necessária"), não uso indiscriminado.</p>
 */
public abstract class Pergunta {

    private final String enunciado;
    private final boolean obrigatoria;

    protected Pergunta(String enunciado, boolean obrigatoria) {
        if (enunciado == null || enunciado.isBlank()) {
            throw new ValorInvalido("O enunciado da pergunta não pode ser vazio.");
        }
        this.enunciado = enunciado.strip();
        this.obrigatoria = obrigatoria;
    }

    /**
     * Template Method. Passo fixo comum a todos os tipos; o passo variável é
     * {@link #validarConteudo(String)}.
     *
     * @param conteudo texto informado pelo participante; pode ser nulo ou vazio
     * @throws RespostaInvalida quando o conteúdo não serve para esta pergunta
     */
    public final void validar(String conteudo) {
        String texto = normalizar(conteudo);

        if (texto.isEmpty()) {
            if (obrigatoria) {
                throw new RespostaInvalida(this, "a resposta é obrigatória");
            }
            return; // pergunta opcional em branco é aceitável
        }

        validarConteudo(texto);
    }

    /**
     * Passo variável: cada especialização julga o formato que ela mesma exige.
     * Recebe o conteúdo já normalizado e garantidamente não vazio.
     */
    protected abstract void validarConteudo(String conteudo);

    /**
     * Descrição do formato aceito, em português, para compor mensagens de erro e
     * orientar o preenchimento. Faz parte do contrato porque o RNF-07 exige mensagens
     * compreensíveis — e só a própria pergunta sabe o que ela aceita.
     */
    public abstract String descricaoDoFormato();

    /**
     * Como esta pergunta quer ser exibida — RF-25.
     *
     * <p>Irmão de {@link #descricaoDoFormato()}, e com destinatário diferente: aquele produz
     * uma frase para a <b>pessoa</b> ler; este produz a estrutura para a <b>tela</b> montar o
     * controle certo. Antes dele existir, a única informação de formato que atravessava a API
     * era a frase, e o site desenhava um campo de texto para os três tipos.</p>
     *
     * <p>É abstrato de propósito: um quarto tipo de pergunta não compila sem dizer como quer
     * ser desenhado. O "ao menos" do RF-25 significa que esse quarto tipo vai existir um dia.</p>
     */
    public abstract FormaDeResposta formaDeResposta();

    /**
     * Consolida as respostas recebidas por esta pergunta (RF-28).
     *
     * <p>Segunda aplicação do polimorfismo, e a que mais economiza código: texto
     * vira lista de comentários, escolha única vira contagem por opção, escala vira
     * distribuição com média. O {@link Questionario} chama este método sem saber de
     * qual tipo é a pergunta.</p>
     *
     * @param respostas respostas efetivamente preenchidas, já com o autor apresentado
     *                  conforme a política de identificação vigente
     */
    public abstract ResumoDaPergunta consolidar(List<RespostaConsolidavel> respostas);

    private static String normalizar(String conteudo) {
        return conteudo == null ? "" : conteudo.strip();
    }

    public String enunciado() {
        return enunciado;
    }

    public boolean obrigatoria() {
        return obrigatoria;
    }

    @Override
    public String toString() {
        return enunciado + (obrigatoria ? " (obrigatória)" : " (opcional)");
    }
}
