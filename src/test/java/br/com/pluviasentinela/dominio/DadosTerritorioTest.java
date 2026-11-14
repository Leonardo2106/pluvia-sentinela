package br.com.pluviasentinela.dominio;

import br.com.pluviasentinela.excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DadosTerritorioTest {

    @Test
    void deveAlterarLimiaresComCodigoCorreto() {
        // Arrange
        DadosTerritorio territorio = new DadosTerritorio(
                "Vila Esperanca", 8_500, 30, 50, 80, "2026");

        // Act
        territorio.alterarLimiares("2026", 35, 55, 90);

        // Assert
        assertEquals(35, territorio.getLimiarAtencao());
        assertEquals(55, territorio.getLimiarAlerta());
        assertEquals(90, territorio.getLimiarCritico());
    }

    @Test
    void deveRecusarLimiaresForaDeOrdem() {
        // Arrange, Act + Assert
        assertThrows(DadosInvalidosException.class, () -> new DadosTerritorio(
                "Vila Esperanca", 8_500, 80, 50, 30, "2026"));
    }
}
