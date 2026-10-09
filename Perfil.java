package br.ueg.eventos.dominio.usuario;

import br.ueg.eventos.dominio.comum.IdUsuario;

/**
 * Os três perfis de {@link Usuario} — e, em cada um, como construir o usuário correspondente.
 *
 * <h2>Por que o enum constrói</h2>
 *
 * <p>Traduzir perfil em subclasse é necessário em pelo menos dois lugares: no cadastro
 * (RF-01), que recebe o perfil escolhido, e no adaptador de persistência, que lê a coluna
 * {@code perfil} e precisa devolver o objeto certo. Com um {@code switch} em cada um, existiriam
 * duas traduções da mesma coisa — e acrescentar um quarto perfil exigiria lembrar dos dois.</p>
 *
 * <p>Aqui existe uma só, e ela é polimórfica: nenhum {@code switch}, nenhum {@code if}. O dia
 * em que um perfil novo for criado, o compilador exige que ele diga como se constrói, porque
 * {@link #criar} é abstrato — não há como esquecer.</p>
 *
 * <p>É a mesma ideia de {@code PoliticaDeInscricao} e {@code PoliticaDeFrequencia}: enum com
 * comportamento, que a decisão D-06 adotou como padrão do projeto e que a seção 8.4 cobra em
 * ROO-05.</p>
 */
public enum Perfil {

    PARTICIPANTE {
        @Override
        public Usuario criar(IdUsuario id, NomeDePessoa nome, Email email) {
            return new Participante(id, nome, email);
        }
    },

    ORGANIZADOR {
        @Override
        public Usuario criar(IdUsuario id, NomeDePessoa nome, Email email) {
            return new Organizador(id, nome, email);
        }
    },

    ADMINISTRADOR {
        @Override
        public Usuario criar(IdUsuario id, NomeDePessoa nome, Email email) {
            return new Administrador(id, nome, email);
        }
    };

    /**
     * Constrói o usuário deste perfil.
     *
     * <p>A validação de cada argumento continua nos construtores das subclasses e nos objetos
     * de valor: este método não repete nenhuma checagem.</p>
     */
    public abstract Usuario criar(IdUsuario id, NomeDePessoa nome, Email email);
}
