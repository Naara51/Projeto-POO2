package br.ueg.eventos.dominio.avaliacao;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Consolidação de uma {@link PerguntaEscolhaUnica}: quantas vezes cada opção foi
 * escolhida, com o percentual (RF-28, "distribuições simples").
 *
 * <p>A contagem parte de <b>todas</b> as opções da pergunta, inclusive as que ninguém
 * escolheu. Um relatório que omite a opção com zero votos esconde informação relevante:
 * "ninguém achou a palestra ruim" é um resultado, não uma ausência de resultado. Por
 * isso o mapa é inicializado com zero para cada opção antes da contagem.</p>
 *
 * <p>{@link LinkedHashMap} preserva a ordem em que o organizador cadastrou as opções —
 * o relatório sai na mesma ordem do questionário, e não numa ordem arbitrária de
 * hash.</p>
 */
public final class ResumoDeEscolhaUnica extends ResumoDaPergunta {

    private final Map<Opcao, Integer> contagem;

    ResumoDeEscolhaUnica(String enunciado, List<Opcao> opcoes, List<RespostaConsolidavel> respostas) {
        super(enunciado, respostas.size());

        Map<Opcao, Integer> apuracao = new LinkedHashMap<>();
        for (Opcao opcao : opcoes) {
            apuracao.put(opcao, 0);
        }
        for (RespostaConsolidavel resposta : respostas) {
            for (Opcao opcao : opcoes) {
                if (opcao.correspondeA(resposta.conteudo())) {
                    apuracao.merge(opcao, 1, Integer::sum);
                    break;
                }
            }
        }
        // LinkedHashMap e nao Map.copyOf: Map.copyOf devolve um mapa imutavel, porem
        // com ordem de iteracao NAO especificada, o que embaralharia o relatorio.
        // A imutabilidade e garantida aqui por nunca expor o campo diretamente.
        this.contagem = new LinkedHashMap<>(apuracao);
    }

    /** Contagem por opção, na ordem de cadastro. */
    public Map<Opcao, Integer> contagem() {
        return new LinkedHashMap<>(contagem);
    }

    public int votosDe(Opcao opcao) {
        return contagem.getOrDefault(opcao, 0);
    }

    @Override
    public List<String> linhas() {
        int total = totalDeRespostas();
        return contagem.entrySet().stream()
                .map(entrada -> formatar(entrada.getKey(), entrada.getValue(), total))
                .toList();
    }

    private static String formatar(Opcao opcao, int votos, int total) {
        // Locale.ROOT garante ponto decimal em qualquer maquina. Sem isso, uma maquina
        // configurada em pt-BR imprimiria "33,3" e outra em en-US imprimiria "33.3",
        // e o mesmo teste passaria num computador e falharia no do colega.
        String percentual = total == 0
                ? "0,0"
                : String.format(Locale.ROOT, "%.1f", (votos * 100.0) / total).replace('.', ',');
        return "- " + opcao.rotulo() + ": " + votos + " (" + percentual + "%)";
    }
}
