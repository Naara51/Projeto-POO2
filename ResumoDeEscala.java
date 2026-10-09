package br.ueg.eventos.dominio.avaliacao;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.OptionalDouble;

/**
 * Consolidação de uma {@link PerguntaEscala}: distribuição por valor e média
 * (RF-28, "quantidades e distribuições simples").
 *
 * <p>Como no {@link ResumoDeEscolhaUnica}, todos os valores da escala aparecem,
 * inclusive os que ninguém marcou — uma distribuição com buracos é ilegível.</p>
 *
 * <p>A média é devolvida como {@link OptionalDouble} em vez de {@code 0.0} quando não
 * há resposta alguma. Zero seria uma mentira: numa escala de 1 a 5, média zero não é
 * um valor possível, e um relatório que mostrasse "média 0,00" levaria o organizador a
 * concluir que a atividade foi péssima quando na verdade ninguém respondeu. Estado
 * ausente modelado explicitamente — ROO-11.</p>
 */
public final class ResumoDeEscala extends ResumoDaPergunta {

    private final EscalaNumerica escala;
    private final Map<Integer, Integer> distribuicao;
    private final OptionalDouble media;

    ResumoDeEscala(String enunciado, EscalaNumerica escala, List<RespostaConsolidavel> respostas) {
        super(enunciado, respostas.size());
        this.escala = escala;

        Map<Integer, Integer> apuracao = new LinkedHashMap<>();
        for (int valor = escala.minimo(); valor <= escala.maximo(); valor++) {
            apuracao.put(valor, 0);
        }

        int soma = 0;
        int quantidade = 0;
        for (RespostaConsolidavel resposta : respostas) {
            int valor = Integer.parseInt(resposta.conteudo());
            apuracao.merge(valor, 1, Integer::sum);
            soma += valor;
            quantidade++;
        }

        this.distribuicao = apuracao;
        this.media = quantidade == 0
                ? OptionalDouble.empty()
                : OptionalDouble.of((double) soma / quantidade);
    }

    public Map<Integer, Integer> distribuicao() {
        return new LinkedHashMap<>(distribuicao);
    }

    public int quantidadeDe(int valor) {
        return distribuicao.getOrDefault(valor, 0);
    }

    /** Vazio quando nenhuma resposta foi recebida. */
    public OptionalDouble media() {
        return media;
    }

    @Override
    public List<String> linhas() {
        List<String> linhas = new java.util.ArrayList<>();
        linhas.add("escala " + escala.descricao());
        linhas.add(media.isPresent()
                ? "média: " + formatar(media.getAsDouble())
                : "média: não há respostas");
        distribuicao.forEach((valor, quantidade) ->
                linhas.add("- " + valor + ": " + quantidade + " " + barra(quantidade)));
        return List.copyOf(linhas);
    }

    /**
     * Locale.ROOT e substituicao manual da virgula: sem isso, o formato do numero
     * mudaria conforme a configuracao regional da maquina, e o mesmo teste passaria
     * no meu computador e falharia no do colega.
     */
    private static String formatar(double valor) {
        return String.format(Locale.ROOT, "%.2f", valor).replace('.', ',');
    }

    private static String barra(int quantidade) {
        return "#".repeat(Math.max(0, quantidade));
    }
}
