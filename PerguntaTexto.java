package br.ueg.eventos.dominio.avaliacao;

import br.ueg.eventos.dominio.comum.ValorInvalido;

import java.util.List;

/**
 * Pergunta de resposta textual livre (RF-25).
 *
 * <p>Exemplo: <i>"O que poderia melhorar nesta palestra?"</i></p>
 *
 * <p>O único formato que ela exige é um limite de tamanho. Isso não é preciosismo:
 * sem limite, o campo aceita qualquer volume de texto, e o RNF-07 pede rejeição de
 * entrada inválida "sem corromper dados" — um texto maior que a coluna do banco é
 * exatamente o caso que corromperia. O limite mora aqui, no domínio, e não numa
 * anotação de persistência, para que a regra valha também nos testes e em qualquer
 * outro adaptador.</p>
 */
public final class PerguntaTexto extends Pergunta {

    private final int tamanhoMaximo;

    public PerguntaTexto(String enunciado, boolean obrigatoria, int tamanhoMaximo) {
        super(enunciado, obrigatoria);
        if (tamanhoMaximo <= 0) {
            throw new ValorInvalido(
                    "O tamanho máximo do texto deve ser positivo. Informado: " + tamanhoMaximo + ".");
        }
        this.tamanhoMaximo = tamanhoMaximo;
    }

    /** Atalho para o caso comum: comentário opcional de até 500 caracteres. */
    public static PerguntaTexto comentarioOpcional(String enunciado) {
        return new PerguntaTexto(enunciado, false, 500);
    }

    @Override
    protected void validarConteudo(String conteudo) {
        if (conteudo.length() > tamanhoMaximo) {
            throw new RespostaInvalida(this,
                    "o texto tem " + conteudo.length() + " caracteres");
        }
    }

    @Override
    public String descricaoDoFormato() {
        return "texto livre de até " + tamanhoMaximo + " caracteres";
    }

    @Override
    public FormaDeResposta formaDeResposta() {
        return FormaDeResposta.texto(tamanhoMaximo);
    }

    @Override
    public ResumoDaPergunta consolidar(List<RespostaConsolidavel> respostas) {
        return new ResumoDeTexto(enunciado(), respostas);
    }

    public int tamanhoMaximo() {
        return tamanhoMaximo;
    }
}
