package br.ueg.eventos.dominio.usuario;

import br.ueg.eventos.dominio.comum.IdUsuario;

public final class Organizador extends Usuario {

    public Organizador(IdUsuario id, NomeDePessoa nome, Email email) {
        super(id, nome, email);
    }

    @Override
    public Perfil perfil() {
        return Perfil.ORGANIZADOR;
    }

    @Override
    public boolean podeExecutar(OperacaoProtegida operacao) {
        return switch (exigirOperacao(operacao)) {
            case GERENCIAR_EVENTO, ENCERRAR_EVENTO, REGISTRAR_FREQUENCIA -> true;

            // Gerenciar usuarios e do administrador (secao 3). Enxergar evento alheio, tambem.
            // Alterar evento alheio e o plano de contingencia: a RN-18 diz "administrador OU
            // organizador AUTORIZADO", e autorizado aqui significa o dono -- quem e dono passa
            // pelo alcance do proprio evento, nao por esta permissao.
            case GERENCIAR_USUARIOS, CONSULTAR_TODOS_OS_EVENTOS, ALTERAR_QUALQUER_EVENTO -> false;
        };
    }
}
