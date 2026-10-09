package br.ueg.eventos.dominio.usuario;

import br.ueg.eventos.dominio.comum.IdUsuario;
import br.ueg.eventos.dominio.comum.ValorInvalido;

public sealed abstract class Usuario permits Participante, Organizador, Administrador {

    private final IdUsuario id;
    private NomeDePessoa nome;
    private Email email;

    protected Usuario(IdUsuario id, NomeDePessoa nome, Email email) {
        this.id = exigirValor(id, "O identificador do usuário não pode ser nulo.");
        this.nome = exigirValor(nome, "O nome do usuário não pode ser nulo.");
        this.email = exigirValor(email, "O e-mail do usuário não pode ser nulo.");
    }

    public final IdUsuario id() {
        return id;
    }

    public final NomeDePessoa nome() {
        return nome;
    }

    public final Email email() {
        return email;
    }

    public abstract Perfil perfil();

    public abstract boolean podeExecutar(OperacaoProtegida operacao);

    public final void atualizarDados(NomeDePessoa nome, Email email) {
        NomeDePessoa novoNome = exigirValor(nome, "O nome do usuário não pode ser nulo.");
        Email novoEmail = exigirValor(email, "O e-mail do usuário não pode ser nulo.");
        this.nome = novoNome;
        this.email = novoEmail;
    }

    protected final OperacaoProtegida exigirOperacao(OperacaoProtegida operacao) {
        return exigirValor(operacao, "A operação protegida não pode ser nula.");
    }

    private static <T> T exigirValor(T valor, String mensagem) {
        if (valor == null) {
            throw new ValorInvalido(mensagem);
        }
        return valor;
    }
}
