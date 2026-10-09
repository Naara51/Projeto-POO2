package br.ueg.eventos.dominio.avaliacao;

import br.ueg.eventos.dominio.comum.IdUsuario;

/**
 * Uma resposta preparada para a consolidação: o conteúdo informado e o autor
 * <b>já apresentado</b> conforme a política de identificação.
 *
 * <h2>O detalhe que faz a RN-15 funcionar sozinha</h2>
 *
 * <p>O campo {@code autorApresentavel} é {@code String}, não {@link IdUsuario}.
 * Isso é deliberado: quando este objeto é criado, a anonimização <b>já aconteceu</b> —
 * {@link Avaliacao#autorApresentavel()} aplicou
 * {@link PoliticaDeIdentificacao#apresentarAutor} e devolveu ou o nome, ou a palavra
 * "Anônimo".</p>
 *
 * <p>A consequência é que a identidade real do participante <b>não entra</b> no
 * caminho da consolidação. Não existe, no código de consolidação, um objeto do qual
 * alguém pudesse extrair o autor por engano. A privacidade exigida pela RN-15 e pelo
 * RNF-08 não depende de ninguém lembrar de aplicá-la: depende do tipo.</p>
 *
 * <p>Essa é a diferença entre "protegemos a identidade" e "a identidade não está
 * disponível para ser vazada".</p>
 */
public record RespostaConsolidavel(String autorApresentavel, String conteudo) {

    public RespostaConsolidavel {
        autorApresentavel = autorApresentavel == null ? "" : autorApresentavel.strip();
        conteudo = conteudo == null ? "" : conteudo.strip();
    }

    public boolean preenchida() {
        return !conteudo.isEmpty();
    }
}
