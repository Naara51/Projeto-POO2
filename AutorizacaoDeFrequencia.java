package br.ueg.eventos.dominio.frequencia;

import br.ueg.eventos.dominio.comum.IdUsuario;

/**
 * Contrato mínimo de permissão para lançar ou corrigir frequência manualmente (RF-22).
 * O domínio de frequência não conhece perfis de usuário: a camada de aplicação preenche
 * este contrato a partir de {@code Usuario.podeExecutar}, mantendo os pacotes desacoplados.
 */
@FunctionalInterface
public interface AutorizacaoDeFrequencia {

    boolean podeLancarOuCorrigir(IdUsuario responsavelId);
}
