package br.ueg.eventos.dominio.avaliacao;

import br.ueg.eventos.dominio.comum.IdUsuario;
import java.util.List;

/**
 * O relatório de avaliação de uma atividade (RF-28).
 *
 * <p>Reúne o cabeçalho — qual questionário, qual atividade, qual política, quantas
 * respostas — e um {@link ResumoDaPergunta} por pergunta, na ordem do questionário.</p>
 *
 * <p>Note o que <b>não</b> está aqui: nenhuma referência a {@link IdUsuario},
 * nenhum acesso às submissões. O relatório é um retrato, não uma janela para os dados
 * originais. Quem o receber não tem como navegar de volta até quem respondeu o quê —
 * o que é a forma mais simples de cumprir a RN-15 e o RNF-08 sem depender de disciplina
 * de quem usa.</p>
 *
 * <p>A apresentação textual existe para a demonstração de terça-feira e para os testes.
 * O adaptador que gerar CSV ou PDF (RF-31) monta a saída dele a partir dos mesmos
 * objetos, sem depender deste formato.</p>
 */
public record ResultadoConsolidado(String tituloDoQuestionario,
                                   String tituloDaAtividade,
                                   PoliticaDeIdentificacao politicaDeIdentificacao,
                                   int totalDeSubmissoesValidas,
                                   List<ResumoDaPergunta> resumos) {

    public ResultadoConsolidado {
        resumos = List.copyOf(resumos);
    }

    /** Verdadeiro quando ninguém respondeu — situação normal, não erro. */
    public boolean vazio() {
        return totalDeSubmissoesValidas == 0;
    }

    public String apresentacaoTextual() {
        StringBuilder texto = new StringBuilder();
        texto.append("RESULTADO CONSOLIDADO\n");
        texto.append("Questionário: ").append(tituloDoQuestionario).append('\n');
        texto.append("Atividade: ").append(tituloDaAtividade).append('\n');
        texto.append("Política de identificação: ")
                .append(politicaDeIdentificacao.descricao()).append('\n');
        texto.append("Submissões válidas: ").append(totalDeSubmissoesValidas).append('\n');
        texto.append('\n');

        if (vazio()) {
            texto.append("Nenhuma resposta recebida até o momento.\n");
            return texto.toString();
        }

        for (ResumoDaPergunta resumo : resumos) {
            texto.append(resumo.apresentacaoTextual()).append('\n');
        }
        return texto.toString();
    }

    @Override
    public String toString() {
        return apresentacaoTextual();
    }
}
