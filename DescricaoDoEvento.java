package br.ueg.eventos.dominio.evento;

import br.ueg.eventos.dominio.comum.ValorInvalido;

/**
 * O texto que explica o evento — RF-04.
 *
 * <h2>Por que pode ser vazia, e por que não pode ser nula</h2>
 *
 * <p>O requisito pede que o organizador <b>mantenha</b> uma descrição, não que ela seja
 * obrigatória: um evento pode ser criado antes de haver o que descrever. Ausente vira texto
 * vazio, e não {@code null}, para que ninguém precise decidir entre os dois na hora de exibir —
 * é a diferença entre uma tela escrever {@code descricao.valor()} e uma tela escrever
 * {@code descricao == null ? "" : descricao.valor()}, repetida em cada lugar que mostra.</p>
 */
public record DescricaoDoEvento(String valor) {

    private static final int LIMITE = 2000;

    public DescricaoDoEvento {
        valor = valor == null ? "" : valor.trim();
        if (valor.length() > LIMITE) {
            throw new ValorInvalido(
                    "A descrição do evento deve ter no máximo " + LIMITE + " caracteres.");
        }
    }

    public boolean vazia() {
        return valor.isEmpty();
    }

    @Override
    public String toString() {
        return valor;
    }
}
