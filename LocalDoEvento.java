package br.ueg.eventos.dominio.evento;

import br.ueg.eventos.dominio.comum.ValorInvalido;

/**
 * Onde o evento acontece — RF-04. Texto livre: "Campus Central", um endereço, ou o nome da
 * plataforma num evento on-line.
 *
 * <h2>Por que não reaproveita {@code programacao.Local}</h2>
 *
 * <p>Aquele é a <b>sala</b> de uma atividade, e é sobre ele que {@code PoliticaDeConflito}
 * decide se duas atividades colidem. Este é o lugar do evento inteiro, numa granularidade
 * diferente, e ninguém detecta conflito entre eventos por ele. Compartilhar o tipo criaria uma
 * dependência entre os pacotes do Augusto e do Israell para unir conceitos que só têm o nome em
 * comum — e a primeira regra que valesse para um passaria a valer para o outro por acidente.</p>
 */
public record LocalDoEvento(String valor) {

    private static final int LIMITE = 200;

    public LocalDoEvento {
        if (valor == null || valor.isBlank()) {
            throw new ValorInvalido("O local do evento, quando informado, não pode ser vazio.");
        }
        valor = valor.trim();
        if (valor.length() > LIMITE) {
            throw new ValorInvalido(
                    "O local do evento deve ter no máximo " + LIMITE + " caracteres.");
        }
    }

    @Override
    public String toString() {
        return valor;
    }
}
