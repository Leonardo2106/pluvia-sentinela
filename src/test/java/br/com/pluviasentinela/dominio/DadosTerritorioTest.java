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

    @Test
    void naoDeveAlterarParcialmenteOsDadosQuandoUmLimiarForInvalido() {
        // Arrange
        DadosTerritorio territorio = new DadosTerritorio(
                "Vila Esperanca", 8_500, 30, 50, 80, "2026");

        // Act
        assertThrows(DadosInvalidosException.class,
                () -> territorio.alterarDados("2026", 10_000, 80, 50, 30));

        // Assert
        assertEquals(8_500, territorio.getPopulacaoEstimada());
        assertEquals(30, territorio.getLimiarAtencao());
        assertEquals(50, territorio.getLimiarAlerta());
        assertEquals(80, territorio.getLimiarCritico());
    }
}
