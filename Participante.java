package br.ueg.eventos.dominio.usuario;

import br.ueg.eventos.dominio.comum.IdUsuario;

public final class Participante extends Usuario {

    public Participante(IdUsuario id, NomeDePessoa nome, Email email) {
        super(id, nome, email);
    }

    @Override
    public Perfil perfil() {
        return Perfil.PARTICIPANTE;
    }

    @Override
    public boolean podeExecutar(OperacaoProtegida operacao) {
        exigirOperacao(operacao);
        return false;
    }
}
