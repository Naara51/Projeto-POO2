package br.ueg.eventos.dominio.avaliacao;

import br.ueg.eventos.dominio.comum.IdUsuario;
import br.ueg.eventos.dominio.comum.ValorInvalido;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Uma submissão como ela está gravada — a forma em que o adaptador de persistência a entrega
 * de volta a {@link Questionario#reconstituir}.
 *
 * <h2>Por que existe um tipo só para isso</h2>
 *
 * <p>Uma {@link Avaliacao} não pode ser construída de fora do pacote: o construtor tem
 * visibilidade de pacote, e é assim que RN-14 e a {@link PoliticaDeEdicao} ficam impossíveis
 * de burlar. Para reidratar, o adaptador precisaria ou de um construtor público — que abriria
 * exatamente esse buraco — ou de um tipo neutro que descreva o que foi lido.</p>
 *
 * <p>Este record é esse tipo neutro. Ele não é uma submissão: é a <b>descrição</b> de uma. Quem
 * monta a {@code Avaliacao} de verdade continua sendo o questionário, dentro do pacote.</p>
 *
 * <h2>Por que a política vigente vem gravada, e não é lida do questionário</h2>
 *
 * <p>RN-15 exige que a política de identificação esteja definida antes do envio, e a
 * {@code Avaliacao} a congela no momento da submissão. Se a reidratação lesse a política
 * <i>atual</i> do questionário, trocar um questionário de ANÔNIMA para IDENTIFICADA revelaria
 * retroativamente quem respondeu anonimamente — que é precisamente o que a regra impede.</p>
 *
 * @param autor              quem respondeu; a apresentação continua passando pela política
 * @param dataHoraSubmissao  quando
 * @param politicaVigente    a política <b>congelada</b> naquele envio
 * @param situacao           se ainda vale ou se foi substituída (RN-17)
 * @param conteudos          as respostas, na ordem das perguntas do questionário
 */
public record SubmissaoReconstituida(
        IdUsuario autor,
        LocalDateTime dataHoraSubmissao,
        PoliticaDeIdentificacao politicaVigente,
        SituacaoDaAvaliacao situacao,
        List<String> conteudos) {

    public SubmissaoReconstituida {
        if (autor == null) {
            throw new ValorInvalido("A submissão gravada exige o autor.");
        }
        if (dataHoraSubmissao == null) {
            throw new ValorInvalido("A submissão gravada exige a data e hora do envio.");
        }
        if (politicaVigente == null) {
            throw new ValorInvalido("A submissão gravada exige a política de identificação vigente (RN-15).");
        }
        if (situacao == null) {
            throw new ValorInvalido("A submissão gravada exige a situação.");
        }
        if (conteudos == null) {
            throw new ValorInvalido("A submissão gravada exige a lista de respostas, ainda que vazia.");
        }
        conteudos = List.copyOf(conteudos);
    }
}
