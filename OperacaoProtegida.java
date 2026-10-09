package br.ueg.eventos.dominio.usuario;

public enum OperacaoProtegida {
    GERENCIAR_EVENTO,
    ENCERRAR_EVENTO,
    GERENCIAR_USUARIOS,

    /**
     * Consultar os eventos de qualquer organizador, e não apenas os próprios.
     *
     * <p>Seção 3 do protocolo, responsabilidades mínimas do administrador: <i>"pode consultar
     * dados de <b>todos</b> os eventos"</i>. É <b>consulta</b>, não comando: alterar continua
     * decidido pelo agregado {@code Evento}, que compara o organizador com quem pede.</p>
     *
     * <p>Separada de {@code GERENCIAR_EVENTO} de propósito. Se fossem a mesma, um organizador
     * passaria a enxergar a agenda inteira da instituição para poder administrar a própria — e
     * a única maneira de separar as duas coisas de novo seria mexer em código.</p>
     */
    CONSULTAR_TODOS_OS_EVENTOS,

    /**
     * Alterar o evento de outro organizador — <b>plano de contingência</b>.
     *
     * <p>RN-18: <i>"Somente <b>administrador ou</b> organizador autorizado altera dados
     * operacionais do evento"</i>. O administrador é nomeado primeiro na regra, e até 14/09/2026
     * era recusado pelo agregado junto com o intruso: {@code Evento} comparava o identificador
     * de quem pedia com o do organizador, e mais nada.</p>
     *
     * <p>É <b>contingência</b>, não rotina: existe para quando o organizador não está disponível
     * e o evento precisa ser publicado, corrigido ou encerrado. Por isso é operação separada de
     * {@link #CONSULTAR_TODOS_OS_EVENTOS} — enxergar todos os eventos e poder alterá-los são
     * coisas diferentes, e separá-las permite conceder uma sem a outra sem mexer em código.</p>
     */
    ALTERAR_QUALQUER_EVENTO,

    /**
     * Lançar ou corrigir frequência manualmente (RF-22).
     *
     * <p>Quem consulta esta operação é a camada de aplicação, ao preencher o contrato
     * {@code frequencia.AutorizacaoDeFrequencia}. O domínio de frequência continua sem
     * importar este pacote: ele recebe a decisão pronta, e não o usuário.</p>
     */
    REGISTRAR_FREQUENCIA
}
