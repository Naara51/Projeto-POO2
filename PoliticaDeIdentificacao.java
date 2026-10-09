package br.ueg.eventos.dominio.avaliacao;

import br.ueg.eventos.dominio.comum.IdUsuario;

/**
 * Define se as respostas de um questionário são identificadas ou anônimas (RN-15).
 *
 * <h2>Por que o enum tem comportamento</h2>
 *
 * <p>A versão ingênua seria um enum sem corpo e, espalhado pelo código, um
 * {@code if (politica == ANONIMA) ... else ...} toda vez que fosse preciso exibir o
 * autor de uma resposta. Cada novo lugar que apresentasse respostas seria uma nova
 * chance de esquecer o {@code if} — e esquecer significa <b>vazar a identidade de
 * quem respondeu anonimamente</b>, violando a RN-15 e o RNF-08 (privacidade).</p>
 *
 * <p>Com o método abstrato {@link #apresentarAutor(IdUsuario)}, a decisão existe
 * em um lugar só. Quem exibe uma avaliação chama o método e recebe o texto correto,
 * sem saber qual política está em vigor nem precisar lembrar da regra.</p>
 *
 * <p><b>Evidência de ROO-05 (polimorfismo).</b> O item 8.4 pede "tratar ao menos um
 * ponto real de variação por comportamento polimórfico, evitando cadeias extensas de
 * if/switch por tipo". Este é um ponto de variação pequeno, mas real, e o custo de
 * resolvê-lo assim é zero.</p>
 */
public enum PoliticaDeIdentificacao {

    /** O nome de quem respondeu acompanha a resposta na consolidação. */
    IDENTIFICADA("identificada") {
        @Override
        public String apresentarAutor(IdUsuario autor) {
            return autor.valor().toString();
        }
    },

    /** A resposta é consolidada sem qualquer vínculo com quem a enviou. */
    ANONIMA("anônima") {
        @Override
        public String apresentarAutor(IdUsuario autor) {
            return "Anônimo";
        }
    };

    private final String descricao;

    PoliticaDeIdentificacao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Converte a identidade real do autor no texto que pode ser exibido.
     *
     * <p>Note que a política ANÔNIMA recebe o autor e simplesmente o descarta. Isso
     * é proposital: quem chama não precisa decidir se pode ou não passar a identidade,
     * o que elimina a possibilidade de acertar em um lugar e errar em outro.</p>
     */
    public abstract String apresentarAutor(IdUsuario autor);

    public String descricao() {
        return descricao;
    }
}
