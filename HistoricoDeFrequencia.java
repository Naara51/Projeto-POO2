package br.ueg.eventos.dominio.frequencia;

import br.ueg.eventos.dominio.comum.ValorInvalido;
import java.util.ArrayList;
import java.util.List;

/**
 * Histórico de frequência de um participante em uma atividade. É somente acréscimo: uma
 * correção nunca apaga nem substitui a marcação anterior, preservando a rastreabilidade
 * exigida por RN-12 e RN-17.
 *
 * <p>A {@link ChaveDeFrequencia} é a identidade do histórico, e também o seu limite: toda
 * marcação recebida precisa ser daquele participante naquela atividade. Antes de a chave
 * existir, este agregado aceitava marcações de qualquer pessoa, e a promessa da primeira frase
 * deste comentário dependia de quem chamasse.</p>
 *
 * <h2>Por que a autorização é argumento de {@link #corrigir}, e não campo — decisão E-10</h2>
 *
 * <p>Na primeira versão, {@link AutorizacaoDeFrequencia} entrava no construtor e ficava
 * guardada. A persistência mostrou o problema: ao reler um histórico do banco, o adaptador não
 * tem como saber quem está autorizado — isso depende de <b>quem está operando agora</b>, não
 * de um fato gravado. As saídas seriam todas ruins: injetar uma autorização que nega tudo
 * (tornando a correção silenciosamente impossível), injetar uma que aceita tudo (abrindo
 * RF-22), ou obrigar o repositório a receber o usuário corrente — o que faria a porta de
 * persistência depender de sessão.</p>
 *
 * <p>A autorização é uma <b>política do momento da operação</b>. Como argumento de
 * {@code corrigir}, o agregado volta a ser só dados e regras, e quem chama é obrigado a dizer,
 * naquela chamada, com que autoridade está corrigindo.</p>
 */
public final class HistoricoDeFrequencia {

    private final ChaveDeFrequencia chave;
    private final List<RegistroDeFrequencia> registros = new ArrayList<>();

    public HistoricoDeFrequencia(ChaveDeFrequencia chave) {
        if (chave == null) {
            throw new ValorInvalido("O histórico de frequência exige o participante e a atividade.");
        }
        this.chave = chave;
    }

    /**
     * Traz de volta um histórico já gravado, com as marcações na ordem em que foram feitas.
     *
     * <p>Não passa por {@link #registrar} nem por {@link #corrigir} de propósito: as marcações
     * manuais gravadas <b>já foram</b> autorizadas quando aconteceram, e reexigir autorização
     * agora significaria revalidar o passado contra as permissões de hoje — uma correção feita
     * por alguém que depois mudou de perfil desapareceria do histórico.</p>
     *
     * <p>A chave, em compensação, continua sendo verificada: marcação de outra pessoa dentro
     * deste histórico é dado corrompido, não fato do passado.</p>
     */
    public static HistoricoDeFrequencia reconstituir(
            ChaveDeFrequencia chave, List<RegistroDeFrequencia> registrosGravados) {
        HistoricoDeFrequencia historico = new HistoricoDeFrequencia(chave);
        if (registrosGravados == null) {
            throw new ValorInvalido("A reconstituição exige a lista de marcações, ainda que vazia.");
        }
        registrosGravados.forEach(registro -> {
            historico.exigirRegistro(registro);
            historico.registros.add(registro);
        });
        return historico;
    }

    /** De quem, e em qual atividade, é este histórico. */
    public ChaveDeFrequencia chave() {
        return chave;
    }

    /** Registra a leitura automática do QR Code (RF-21). */
    public void registrar(RegistroDeFrequencia registro) {
        exigirRegistro(registro);
        if (registro.origem() != OrigemDaMarcacao.QR_CODE) {
            throw new ValorInvalido("Lançamento manual deve ser feito por correção autorizada.");
        }
        registros.add(registro);
    }

    /**
     * Lança ou corrige manualmente a frequência, exigindo responsável autorizado (RF-22).
     *
     * @param autorizacao quem pode lançar ou corrigir <b>agora</b> — ver decisão E-10 no
     *                    comentário da classe
     */
    public void corrigir(RegistroDeFrequencia correcao, AutorizacaoDeFrequencia autorizacao) {
        exigirRegistro(correcao);
        if (autorizacao == null) {
            throw new ValorInvalido("A correção de frequência exige a autorização de quem a faz.");
        }
        if (correcao.origem() != OrigemDaMarcacao.MANUAL) {
            throw new ValorInvalido("A correção de frequência precisa ter origem manual.");
        }
        if (!autorizacao.podeLancarOuCorrigir(correcao.responsavelId())) {
            throw new CorrecaoNaoAutorizada(
                    "O responsável informado não pode lançar ou corrigir frequência.");
        }
        registros.add(correcao);
    }

    public List<RegistroDeFrequencia> registros() {
        return List.copyOf(registros);
    }

    /** Calcula a situação sem alterar o histórico; trocar a política não reescreve registros. */
    public SituacaoDePresenca calcularSituacao(PoliticaDeFrequencia politica) {
        if (politica == null) {
            throw new ValorInvalido("O cálculo de presença exige a política da atividade.");
        }
        return politica.calcular(registros());
    }

    private void exigirRegistro(RegistroDeFrequencia registro) {
        if (registro == null) {
            throw new ValorInvalido("O registro de frequência não pode ser nulo.");
        }
        if (!chave.equals(ChaveDeFrequencia.de(registro))) {
            throw new ValorInvalido(
                    "A marcação é de outro participante ou de outra atividade: não pertence a este histórico.");
        }
    }
}
