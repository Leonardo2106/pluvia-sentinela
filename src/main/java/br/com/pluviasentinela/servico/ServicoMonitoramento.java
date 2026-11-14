package br.com.pluviasentinela.servico;

import br.com.pluviasentinela.dominio.Alerta;
import br.com.pluviasentinela.dominio.DadosTerritorio;
import br.com.pluviasentinela.dominio.EstrategiaPrevisao;
import br.com.pluviasentinela.dominio.Medicao;
import br.com.pluviasentinela.dominio.NivelAlerta;
import br.com.pluviasentinela.dominio.ResultadoPrevisao;
import br.com.pluviasentinela.dominio.Sensor;
import br.com.pluviasentinela.dominio.TipoPrevisao;
import br.com.pluviasentinela.excecao.DadosInvalidosException;
import br.com.pluviasentinela.persistencia.AlertaRepository;
import br.com.pluviasentinela.persistencia.MedicaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class ServicoMonitoramento {

    private final MedicaoRepository medicaoRepository;
    private final AlertaRepository alertaRepository;
    private final FabricaPrevisao fabricaPrevisao;

    public ServicoMonitoramento(MedicaoRepository medicaoRepository,
                                AlertaRepository alertaRepository,
                                FabricaPrevisao fabricaPrevisao) {
        this.medicaoRepository = medicaoRepository;
        this.alertaRepository = alertaRepository;
        this.fabricaPrevisao = fabricaPrevisao;
    }

    public Medicao registrarLeitura(Sensor sensor) {
        if (sensor == null) {
            throw new DadosInvalidosException("sensor", null, "o sensor deve ser informado");
        }
        double valor = sensor.lerValor();
        return medicaoRepository.save(new Medicao(sensor, valor));
    }

    public ResultadoPrevisao executarPrevisao(String identificadorSensor, TipoPrevisao tipo) {
        String identificador = validarIdentificador(identificadorSensor);
        List<Medicao> historico = medicaoRepository
                .findBySensorIdentificadorOrderByRegistradaEmDesc(identificador);
        EstrategiaPrevisao estrategia = fabricaPrevisao.criar(tipo);
        return estrategia.prever(historico);
    }

    public Optional<Alerta> avaliarEEmitirAlerta(String identificadorSensor,
                                                  ResultadoPrevisao resultado,
                                                  DadosTerritorio territorio) {
        String identificador = validarIdentificador(identificadorSensor);
        if (resultado == null) {
            throw new DadosInvalidosException("resultado", null, "o resultado deve ser informado");
        }
        if (territorio == null) {
            throw new DadosInvalidosException("territorio", null, "os dados do territorio devem ser informados");
        }

        double valor = resultado.valorPrevisto();
        NivelAlerta nivel;
        double limite;

        if (valor >= territorio.getLimiarCritico()) {
            nivel = NivelAlerta.CRITICO;
            limite = territorio.getLimiarCritico();
        } else if (valor >= territorio.getLimiarAlerta()) {
            nivel = NivelAlerta.ALERTA;
            limite = territorio.getLimiarAlerta();
        } else if (valor >= territorio.getLimiarAtencao()) {
            nivel = NivelAlerta.ATENCAO;
            limite = territorio.getLimiarAtencao();
        } else {
            return Optional.empty();
        }

        String justificativa = String.format(Locale.forLanguageTag("pt-BR"),
                "Sensor %s previu %.2f %s na regiao %s, atingindo o limite %s de %.2f %s.",
                identificador, valor, resultado.unidadeMedida(), territorio.getNomeRegiao(),
                nivel, limite, resultado.unidadeMedida());

        Alerta alerta = new Alerta(identificador, nivel, valor, limite, justificativa);
        return Optional.of(alertaRepository.save(alerta));
    }

    public List<Medicao> listarHistorico(String identificadorSensor) {
        return medicaoRepository.findBySensorIdentificadorOrderByRegistradaEmDesc(
                validarIdentificador(identificadorSensor));
    }

    public List<Alerta> listarAlertas() {
        return alertaRepository.findAllByOrderByEmitidoEmDesc();
    }

    private String validarIdentificador(String identificador) {
        if (identificador == null || identificador.isBlank()) {
            throw new DadosInvalidosException("identificadorSensor", identificador,
                    "o identificador deve ser informado");
        }
        return identificador.trim();
    }
}
