package br.ueg.eventos.dominio.avaliacao;

/**
 * O nome da forma de uma pergunta — RF-25.
 *
 * <h2>Por que existe um enum, se a hierarquia já distingue os três</h2>
 *
 * <p>Porque a fronteira precisa de um <b>nome</b>. Uma classe não atravessa HTTP: o que chega à
 * tela é JSON, e a tela precisa saber se desenha uma caixa de texto, uma lista de opções ou um
 * campo numérico. Até este enum existir, a API mandava apenas {@code descricaoDoFormato()} —
 * uma frase em português, boa para a pessoa ler e inútil para montar um controle. A tela
 * desenhava um campo de texto para os três tipos, e RF-25 pedia que <i>"cada tipo é
 * <b>exibido</b>"</i>.</p>
 *
 * <p>É o mesmo papel que {@code Perfil} cumpre para {@code Usuario}: o enum nomeia, a hierarquia
 * comporta-se. Ele <b>não</b> substitui o polimorfismo — nenhum {@code if} por tipo aparece no
 * domínio, na aplicação ou na API por causa dele. Quem responde qual é o seu nome é a própria
 * subclasse, em {@link Pergunta#formaDeResposta()}.</p>
 *
 * <p>Os nomes coincidem com o discriminador da coluna {@code pergunta.tipo}, e a coincidência é
 * deliberada: dois vocabulários para a mesma distinção divergiriam no dia em que um quarto tipo
 * aparecesse em só um deles.</p>
 */
public enum TipoDePergunta {
    TEXTO,
    ESCOLHA_UNICA,
    ESCALA
}
