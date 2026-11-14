package br.com.pluviasentinela.dominio;

import br.com.pluviasentinela.excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PrevisaoMediaMovelTest {

    @Test
    void deveCalcularMediaDasCincoLeiturasMaisRecentes() {
        // Arrange
        List<Medicao> historico = List.of(
                medicao(10, 1), medicao(20, 2), medicao(30, 3),
                medicao(40, 4), medicao(50, 5), medicao(60, 6));
        PrevisaoMediaMovel estrategia = new PrevisaoMediaMovel();

        // Act
        ResultadoPrevisao resultado = estrategia.prever(historico);

        // Assert: a leitura mais antiga (10) fica fora da janela
        assertEquals(40.0, resultado.valorPrevisto(), 0.001);
        assertEquals("mm/h", resultado.unidadeMedida());
    }

    @Test
    void deveRecusarHistoricoComMenosDeCincoLeituras() {
        // Arrange
        List<Medicao> historico = List.of(
                medicao(10, 1), medicao(20, 2), medicao(30, 3), medicao(40, 4));
        PrevisaoMediaMovel estrategia = new PrevisaoMediaMovel();

        // Act + Assert
        assertThrows(DadosInvalidosException.class, () -> estrategia.prever(historico));
    }

    private Medicao medicao(double valor, int minuto) {
        return new Medicao("PLUV-01", "PLUVIOMETRICO", "Vila Esperanca",
                valor, "mm/h", LocalDateTime.of(2026, 11, 6, 10, minuto));
    }
}
