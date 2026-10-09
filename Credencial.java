package br.ueg.eventos.dominio.usuario;

import br.ueg.eventos.dominio.comum.IdUsuario;
import br.ueg.eventos.dominio.comum.ValorInvalido;
import java.time.Instant;

/**
 * O que autentica um usuário — agregado próprio, deliberadamente separado de {@link Usuario}.
 *
 * <h2>Por que não é um campo de Usuario</h2>
 *
 * <p>Porque então toda consulta de usuário carregaria a senha junto: listar participantes,
 * exibir o organizador de um evento, montar o relatório de inscritos. Cada uma dessas seria
 * uma chance de vazamento, e evitá-lo passaria a depender de disciplina de quem escreve o
 * próximo DTO.</p>
 *
 * <p>Separados, {@code UsuarioRepository} não tem como devolver a senha — ele não a conhece.
 * É a mesma ideia da tabela `credencial` no esquema, e a razão de as duas coisas terem portas
 * diferentes. RNF-05 deixa de ser uma promessa e passa a ser uma consequência da estrutura.</p>
 *
 * <p>A identidade da credencial é o próprio usuário: não existe segunda credencial da mesma
 * pessoa. Por isso {@code CredencialRepository} é indexado por {@link IdUsuario}, sem chave
 * gerada.</p>
 */
public final class Credencial {

    private final IdUsuario usuarioId;
    private SenhaProtegida senha;
    private Instant atualizadoEm;

    public Credencial(IdUsuario usuarioId, SenhaProtegida senha, Instant atualizadoEm) {
        this.usuarioId = exigir(usuarioId, "A credencial precisa pertencer a um usuário.");
        this.senha = exigir(senha, "A credencial precisa de uma senha protegida.");
        this.atualizadoEm = exigir(atualizadoEm, "A credencial precisa do instante de atualização.");
    }

    public IdUsuario usuarioId() {
        return usuarioId;
    }

    public SenhaProtegida senha() {
        return senha;
    }

    public Instant atualizadoEm() {
        return atualizadoEm;
    }

    /**
     * Substitui a senha protegida.
     *
     * <p>O instante não pode retroceder. Não é preciosismo: {@code atualizadoEm} é o que
     * permite auditar quando uma credencial mudou, e uma troca com data anterior à atual
     * tornaria essa leitura mentirosa — duas trocas seguidas poderiam aparecer fora de ordem.</p>
     */
    public void trocarSenha(SenhaProtegida nova, Instant momento) {
        exigir(nova, "A nova senha protegida não pode ser nula.");
        exigir(momento, "A troca de senha precisa do instante.");
        if (momento.isBefore(atualizadoEm)) {
            throw new ValorInvalido("A troca de senha não pode ter instante anterior ao da credencial atual.");
        }
        this.senha = nova;
        this.atualizadoEm = momento;
    }

    /** Nem o hash nem o dono aparecem: um log de credencial não deve identificar ninguém. */
    @Override
    public String toString() {
        return "Credencial[algoritmo=" + senha.algoritmo() + ", atualizadoEm=" + atualizadoEm + "]";
    }

    private static <T> T exigir(T valor, String mensagem) {
        if (valor == null) {
            throw new ValorInvalido(mensagem);
        }
        return valor;
    }
}
