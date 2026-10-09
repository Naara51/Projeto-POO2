package br.ueg.eventos.dominio.usuario;

import br.ueg.eventos.dominio.comum.ValorInvalido;

/**
 * Uma senha já protegida — RNF-05: <i>senha com hash apropriado, nunca em texto puro</i>.
 *
 * <h2>O que este tipo garante, e o que não garante</h2>
 *
 * <p>Ele garante que, <b>depois</b> da proteção, o resultado circula como um valor
 * declarado: quem recebe uma {@code SenhaProtegida} sabe pelo tipo que não está com o texto
 * da senha na mão. Um {@code String} solto não diria isso — e é justamente por circular como
 * {@code String} que senha vaza em log, em mensagem de erro e em DTO gerado por reflexão.</p>
 *
 * <p>Ele <b>não</b> garante que o algoritmo é bom. Isso é decisão de infraestrutura, atrás da
 * porta {@code ServicoDeSenha}, e é por isso que o algoritmo viaja junto com o valor: o dia em
 * que o projeto trocar PBKDF2 por outro esquema, as credenciais antigas continuam conferíveis
 * porque cada uma diz com o que foi gerada.</p>
 *
 * <h2>Por que {@code toString} é sobrescrito</h2>
 *
 * <p>Um {@code record} gera um {@code toString} que imprime todos os componentes. Aqui isso
 * colocaria o hash em qualquer log que registrasse o objeto. O algoritmo continua aparecendo
 * — ele não é segredo, e saber com o que a credencial foi gerada ajuda a diagnosticar.</p>
 *
 * @param algoritmo nome do esquema que gerou o valor, p. ex. {@code PBKDF2WithHmacSHA256}
 * @param valor     o resultado da proteção, no formato que o próprio esquema sabe reler
 */
public record SenhaProtegida(String algoritmo, String valor) {

    public SenhaProtegida {
        if (algoritmo == null || algoritmo.isBlank()) {
            throw new ValorInvalido("A senha protegida precisa declarar o algoritmo que a gerou.");
        }
        if (valor == null || valor.isBlank()) {
            throw new ValorInvalido("A senha protegida não pode ser vazia.");
        }
        algoritmo = algoritmo.trim();
        valor = valor.trim();
    }

    @Override
    public String toString() {
        return "SenhaProtegida[algoritmo=" + algoritmo + ", valor=oculto]";
    }
}
