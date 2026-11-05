package br.com.pluviasentinela.dominio;

import br.com.pluviasentinela.excecao.DadosInvalidosException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "medicoes")
public class Medicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String sensorIdentificador;

    @Column(nullable = false, length = 40)
    private String tipoSensor;

    @Column(nullable = false, length = 80)
    private String regiao;

    @Column(nullable = false)
    private double valor;

    @Column(nullable = false, length = 20)
    private String unidadeMedida;

    @Column(nullable = false)
    private LocalDateTime registradaEm;

    protected Medicao() {
    }

    public Medicao(Sensor sensor, double valor) {
        if (sensor == null) {
            throw new DadosInvalidosException("sensor", null, "o sensor deve ser informado");
        }
        definirSensor(sensor.getIdentificador(), sensor.retornarTipo(), sensor.getRegiao(), sensor.getUnidadeMedida());
        setValor(valor);
        this.registradaEm = LocalDateTime.now();
    }

    public Medicao(String sensorIdentificador, String tipoSensor, String regiao,
                   double valor, String unidadeMedida, LocalDateTime registradaEm) {
        definirSensor(sensorIdentificador, tipoSensor, regiao, unidadeMedida);
        setValor(valor);
        if (registradaEm == null) {
            throw new DadosInvalidosException("registradaEm", null, "a data da medicao deve ser informada");
        }
        this.registradaEm = registradaEm;
    }

    private void definirSensor(String identificador, String tipo, String regiao, String unidade) {
        this.sensorIdentificador = validarTexto(identificador, "sensorIdentificador");
        this.tipoSensor = validarTexto(tipo, "tipoSensor");
        this.regiao = validarTexto(regiao, "regiao");
        this.unidadeMedida = validarTexto(unidade, "unidadeMedida");
    }

    private String validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new DadosInvalidosException(campo, valor, "o texto deve ser informado");
        }
        return valor.trim();
    }

    private void setValor(double valor) {
        if (!Double.isFinite(valor) || valor < 0) {
            throw new DadosInvalidosException("valor", valor, "a medicao deve ser finita e nao negativa");
        }
        this.valor = valor;
    }

    public Long getId() {
        return id;
    }

    public String getSensorIdentificador() {
        return sensorIdentificador;
    }

    public String getTipoSensor() {
        return tipoSensor;
    }

    public String getRegiao() {
        return regiao;
    }

    public double getValor() {
        return valor;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public LocalDateTime getRegistradaEm() {
        return registradaEm;
    }
}
