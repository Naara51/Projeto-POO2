package br.ueg.eventos.dominio.frequencia;

import br.ueg.eventos.dominio.comum.ValorInvalido;
import java.time.Instant;
import java.util.regex.Pattern;

/**
 * Conteúdo do QR Code de frequência (RF-20, RN-10). O valor é opaco: identifica a
 * operação, mas não carrega participante, atividade, CPF, e-mail nem senha. A geração
 * criptográfica do valor é responsabilidade de uma porta da camada de aplicação; aqui
 * o domínio apenas garante o formato opaco e a validade temporal.
 */
public record TokenDeFrequencia(String valor, Instant expiraEm) {

    private static final Pattern OPACO = Pattern.compile("[A-Za-z0-9_-]{8,}");

    public TokenDeFrequencia {
        if (valor == null || !OPACO.matcher(valor).matches()) {
            throw new ValorInvalido(
                    "O token de frequência deve ser opaco e não pode conter dado pessoal legível.");
        }
        if (expiraEm == null) {
            throw new ValorInvalido("A validade do token de frequência é obrigatória.");
        }
    }

    public void exigirValidoEm(Instant instante) {
        if (instante == null || !instante.isBefore(expiraEm)) {
            throw new TokenExpirado("O token de frequência expirou.");
        }
    }
}
