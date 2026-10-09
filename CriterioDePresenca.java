package br.ueg.eventos.dominio.frequencia;

/**
 * O critério de presença escolhido para uma atividade — RF-19.
 *
 * <h2>Por que existe, se já existe PoliticaDeFrequencia</h2>
 *
 * <p>{@link PoliticaDeFrequencia} é uma interface com três implementações, e é assim que o
 * cálculo de presença fica polimórfico. Mas uma interface não se guarda em coluna: o banco
 * precisa de um nome estável, e a atividade precisa <b>declarar</b> qual critério usa.</p>
 *
 * <p>Este enum é esse nome. Ele não reimplementa nada — cada constante devolve a política
 * correspondente. A alternativa seria o adaptador de persistência ter um {@code switch}
 * traduzindo texto em classe, o que espalharia a tradução: aqui ela é uma só, e acrescentar um
 * critério novo obriga o compilador a exigir a implementação.</p>
 *
 * <p>É a mesma solução de {@code Perfil.criar}: o enum sabe construir o comportamento que ele
 * nomeia.</p>
 */
public enum CriterioDePresenca {

    /** Uma leitura basta para considerar presente. É o padrão. */
    CHECK_IN_UNICO {
        @Override
        public PoliticaDeFrequencia politica() {
            return new CheckInUnico();
        }
    },

    /** Exige entrada e saída — atividades longas, em que sair cedo descaracteriza a presença. */
    ENTRADA_E_SAIDA {
        @Override
        public PoliticaDeFrequencia politica() {
            return new EntradaESaida();
        }
    },

    /** Só a validação manual do organizador conta — atividades sem leitor de QR Code. */
    VALIDACAO_MANUAL {
        @Override
        public PoliticaDeFrequencia politica() {
            return new ValidacaoManual();
        }
    };

    /** A regra que calcula a situação a partir das marcações. */
    public abstract PoliticaDeFrequencia politica();
}
