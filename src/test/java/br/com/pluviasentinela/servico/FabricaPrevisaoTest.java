package br.com.pluviasentinela.servico;

import br.com.pluviasentinela.dominio.PrevisaoComparacao;
import br.com.pluviasentinela.dominio.PrevisaoMediaMovel;
import br.com.pluviasentinela.dominio.TipoPrevisao;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class FabricaPrevisaoTest {

    @Test
    void deveCriarCadaEstrategiaSolicitada() {
        // Arrange
        FabricaPrevisao fabrica = new FabricaPrevisao();

        // Act + Assert
        assertInstanceOf(PrevisaoMediaMovel.class, fabrica.criar(TipoPrevisao.MEDIA_MOVEL));
        assertInstanceOf(PrevisaoComparacao.class, fabrica.criar(TipoPrevisao.COMPARACAO));
    }
}
