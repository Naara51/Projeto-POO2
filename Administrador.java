package br.ueg.eventos.dominio.usuario;

import br.ueg.eventos.dominio.comum.IdUsuario;

public final class Administrador extends Usuario {

    public Administrador(IdUsuario id, NomeDePessoa nome, Email email) {
        super(id, nome, email);
    }

    @Override
    public Perfil perfil() {
        return Perfil.ADMINISTRADOR;
    }

    @Override
    public boolean podeExecutar(OperacaoProtegida operacao) {
        exigirOperacao(operacao);
        return true;
    }
}
