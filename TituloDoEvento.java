package br.ueg.eventos.dominio.evento;

import br.ueg.eventos.dominio.comum.ValorInvalido;

/**
 * O nome do evento — RF-04.
 *
 * <p>Objeto de valor, e não um {@code String} solto no agregado: a validação acompanha o
 * conceito, e um título inválido não chega a existir. É a mesma forma de {@code NomeDePessoa}
 * e {@code TipoDeAtividade}.</p>
 *
 * <p>Vazio é recusado. Um evento sem nome não se distingue de outro na lista pública, e foi
 * exatamente o que aconteceu enquanto o agregado não tinha este campo: o site mostrava
 * "Evento de 20/10/2026", porque a data era tudo o que havia para exibir.</p>
 */
public record TituloDoEvento(String valor) {

    private static final int LIMITE = 200;

    public TituloDoEvento {
        if (valor == null || valor.isBlank()) {
            throw new ValorInvalido("O título do evento não pode ser vazio.");
        }
        valor = valor.trim();
        if (valor.length() > LIMITE) {
            throw new ValorInvalido(
                    "O título do evento deve ter no máximo " + LIMITE + " caracteres.");
        }
    }

    @Override
    public String toString() {
        return valor;
    }
}
