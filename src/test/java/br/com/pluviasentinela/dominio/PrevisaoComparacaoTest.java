package br.com.pluviasentinela.dominio;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PrevisaoComparacaoTest {

    @Test
    void deveProjetarTendenciaEntrePeriodos() {
        // Arrange: periodo anterior = 20, periodo recente = 50
        List<Medicao> historico = List.of(
                medicao(10, 1), medicao(20, 2), medicao(30, 3),
                medicao(40, 4), medicao(50, 5), medicao(60, 6));
        PrevisaoComparacao estrategia = new PrevisaoComparacao();

        // Act
        ResultadoPrevisao resultado = estrategia.prever(historico);

        // Assert: 50 + (50 - 20) = 80
        assertEquals(80.0, resultado.valorPrevisto(), 0.001);
    }

    @Test
    void naoDeveProduzirPrevisaoNegativaEmQuedaAcentuada() {
        // Arrange: periodo anterior alto e periodo recente baixo
        List<Medicao> historico = List.of(
                medicao(100, 1), medicao(100, 2), medicao(100, 3),
                medicao(10, 4), medicao(10, 5), medicao(10, 6));
        PrevisaoComparacao estrategia = new PrevisaoComparacao();

        // Act
        ResultadoPrevisao resultado = estrategia.prever(historico);

        // Assert
        assertEquals(0.0, resultado.valorPrevisto(), 0.001);
    }

    private Medicao medicao(double valor, int minuto) {
        return new Medicao("PLUV-01", "PLUVIOMETRICO", "Vila Esperanca",
                valor, "mm/h", LocalDateTime.of(2026, 11, 6, 10, minuto));
    }
}
