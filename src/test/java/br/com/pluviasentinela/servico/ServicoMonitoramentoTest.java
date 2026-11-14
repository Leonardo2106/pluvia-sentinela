package br.com.pluviasentinela.servico;

import br.com.pluviasentinela.dominio.Alerta;
import br.com.pluviasentinela.dominio.DadosTerritorio;
import br.com.pluviasentinela.dominio.Medicao;
import br.com.pluviasentinela.dominio.NivelAlerta;
import br.com.pluviasentinela.dominio.ResultadoPrevisao;
import br.com.pluviasentinela.dominio.Sensor;
import br.com.pluviasentinela.persistencia.AlertaRepository;
import br.com.pluviasentinela.persistencia.MedicaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServicoMonitoramentoTest {

    @Mock
    private MedicaoRepository medicaoRepository;

    @Mock
    private AlertaRepository alertaRepository;

    @Mock
    private Sensor sensor;

    private ServicoMonitoramento servico;
    private DadosTerritorio territorio;

    @BeforeEach
    void preparar() {
        servico = new ServicoMonitoramento(
                medicaoRepository, alertaRepository, new FabricaPrevisao());
        territorio = new DadosTerritorio("Vila Esperanca", 8_500, 30, 50, 80, "2026");
    }

    @Test
    void devePersistirLeituraValida() {
        // Arrange
        when(sensor.lerValor()).thenReturn(42.5);
        when(sensor.getIdentificador()).thenReturn("PLUV-01");
        when(sensor.retornarTipo()).thenReturn("PLUVIOMETRICO");
        when(sensor.getRegiao()).thenReturn("Vila Esperanca");
        when(sensor.getUnidadeMedida()).thenReturn("mm/h");
        when(medicaoRepository.save(any(Medicao.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        // Act
        Medicao medicao = servico.registrarLeitura(sensor);

        // Assert
        assertEquals(42.5, medicao.getValor(), 0.001);
        verify(medicaoRepository).save(any(Medicao.class));
    }

    @Test
    void devePersistirAlertaCriticoComJustificativa() {
        // Arrange
        ResultadoPrevisao resultado = new ResultadoPrevisao(95, "mm/h", "Chuva em crescimento.");
        when(alertaRepository.save(any(Alerta.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        // Act
        Optional<Alerta> alerta = servico.avaliarEEmitirAlerta("PLUV-01", resultado, territorio);

        // Assert
        assertTrue(alerta.isPresent());
        assertEquals(NivelAlerta.CRITICO, alerta.orElseThrow().getNivel());
        assertTrue(alerta.orElseThrow().getJustificativa().contains("PLUV-01"));

        ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
        verify(alertaRepository).save(captor.capture());
        assertEquals(80, captor.getValue().getLimiteUltrapassado(), 0.001);
    }

    @Test
    void naoDevePersistirAlertaAbaixoDoLimiarDeAtencao() {
        // Arrange
        ResultadoPrevisao resultado = new ResultadoPrevisao(20, "mm/h", "Chuva fraca.");

        // Act
        Optional<Alerta> alerta = servico.avaliarEEmitirAlerta("PLUV-01", resultado, territorio);

        // Assert
        assertTrue(alerta.isEmpty());
        verify(alertaRepository, never()).save(any(Alerta.class));
    }
}
