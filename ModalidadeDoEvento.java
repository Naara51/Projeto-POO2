package br.ueg.eventos.dominio.evento;

/**
 * Como o evento acontece — RF-04, onde o critério fala em "local/modalidade".
 *
 * <h2>Por que enum, e por que ele responde uma pergunta</h2>
 *
 * <p>Modalidade é um conjunto fechado: três valores, e um quarto exigiria decisão da equipe. Se
 * fosse texto livre, "on-line", "Online" e "ONLINE" seriam três modalidades diferentes para o
 * mesmo conceito, e nenhum relatório fecharia.</p>
 *
 * <p>Mas um enum que é só rótulo não ganha o seu lugar. Este responde {@link #exigeLocal()}, e
 * é o agregado quem pergunta — em vez de um {@code switch} no construtor, que precisaria ser
 * reencontrado no dia em que existir uma quarta modalidade. Acrescentar um valor aqui obriga a
 * decidir a resposta ali mesmo, e o compilador cobra.</p>
 */
public enum ModalidadeDoEvento {

    /** Acontece num lugar físico. Sem local informado, a inscrição não sabe para onde ir. */
    PRESENCIAL {
        @Override
        public boolean exigeLocal() {
            return true;
        }
    },

    /** Acontece pela internet. O local é opcional e costuma ser a plataforma. */
    ONLINE {
        @Override
        public boolean exigeLocal() {
            return false;
        }
    },

    /** Tem parte presencial, então tem lugar físico — e a mesma exigência do presencial. */
    HIBRIDO {
        @Override
        public boolean exigeLocal() {
            return true;
        }
    };

    /** Esta modalidade obriga a informar onde o evento acontece? */
    public abstract boolean exigeLocal();
}
