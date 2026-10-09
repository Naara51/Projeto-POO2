package br.ueg.eventos.dominio.avaliacao;

import br.ueg.eventos.dominio.comum.ValorInvalido;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Período em que um questionário aceita respostas.
 *
 * <h2>Por que isto é uma classe e não dois campos no Questionário</h2>
 *
 * <p>A primeira versão do modelo tinha {@code inicioDasRespostas} e
 * {@code fimDasRespostas} soltos dentro de {@code Questionario}. Duas consequências
 * ruins: nada impedia que o fim fosse anterior ao início, e a pergunta "este momento
 * está dentro da janela?" virava uma comparação escrita à mão em todo lugar que
 * precisasse dela.</p>
 *
 * <p>Extrair o conceito resolve as duas de uma vez. A invariante {@code fim > inicio}
 * é verificada no construtor e, a partir daí, <b>não existe janela inválida no
 * sistema</b> — nenhum código precisa reverificar. E {@link #contem(LocalDateTime)}
 * concentra a comparação num lugar só. Esta é a <b>Refatoração 1</b> registrada em
 * {@code docs/04-padroes-e-refatoracoes.md} (ROO-12).</p>
 *
 * <p><b>Sobre a RN-20</b> ("horários e datas devem considerar o fuso definido para o
 * evento"): o domínio trabalha com {@link LocalDateTime}, isto é, com o horário local
 * do evento, já resolvido. A conversão de fuso é responsabilidade do adaptador que
 * recebe a requisição — decisão registrada em {@code docs/02-modelagem-e-decisoes.md}.</p>
 */
public record JanelaDeRespostas(LocalDateTime inicio, LocalDateTime fim) {

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public JanelaDeRespostas {
        if (inicio == null || fim == null) {
            throw new ValorInvalido("A janela de respostas exige início e fim.");
        }
        if (!fim.isAfter(inicio)) {
            throw new ValorInvalido(
                    "O fim da janela de respostas deve ser posterior ao início. "
                            + "Informado: início " + inicio.format(FORMATO)
                            + ", fim " + fim.format(FORMATO) + ".");
        }
    }

    /** Verdadeiro quando o momento está dentro da janela, inclusive nos extremos. */
    public boolean contem(LocalDateTime momento) {
        if (momento == null) {
            return false;
        }
        return !momento.isBefore(inicio) && !momento.isAfter(fim);
    }

    public String descricao() {
        return "de " + inicio.format(FORMATO) + " até " + fim.format(FORMATO);
    }
}
