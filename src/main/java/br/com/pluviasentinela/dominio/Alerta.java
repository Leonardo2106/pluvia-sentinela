package br.com.pluviasentinela.dominio;

import br.com.pluviasentinela.excecao.DadosInvalidosException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "alertas")
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String sensorIdentificador;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private NivelAlerta nivel;

    @Column(nullable = false)
    private double valorPrevisto;

    @Column(nullable = false)
    private double limiteUltrapassado;

    @Column(nullable = false, length = 500)
    private String justificativa;

    @Column(nullable = false)
    private LocalDateTime emitidoEm;

    protected Alerta() {
    }

    public Alerta(String sensorIdentificador, NivelAlerta nivel, double valorPrevisto,
                  double limiteUltrapassado, String justificativa) {
        this.sensorIdentificador = validarTexto(sensorIdentificador, "sensorIdentificador");
        if (nivel == null) {
            throw new DadosInvalidosException("nivel", null, "o nivel do alerta deve ser informado");
        }
        validarValor(valorPrevisto, "valorPrevisto");
        validarValor(limiteUltrapassado, "limiteUltrapassado");
        this.nivel = nivel;
        this.valorPrevisto = valorPrevisto;
        this.limiteUltrapassado = limiteUltrapassado;
        this.justificativa = validarTexto(justificativa, "justificativa");
        this.emitidoEm = LocalDateTime.now();
    }

    private String validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new DadosInvalidosException(campo, valor, "o texto deve ser informado");
        }
        return valor.trim();
    }

    private void validarValor(double valor, String campo) {
        if (!Double.isFinite(valor) || valor < 0) {
            throw new DadosInvalidosException(campo, valor, "o valor deve ser finito e nao negativo");
        }
    }

    public Long getId() {
        return id;
    }

    public String getSensorIdentificador() {
        return sensorIdentificador;
    }

    public NivelAlerta getNivel() {
        return nivel;
    }

    public double getValorPrevisto() {
        return valorPrevisto;
    }

    public double getLimiteUltrapassado() {
        return limiteUltrapassado;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public LocalDateTime getEmitidoEm() {
        return emitidoEm;
    }
}
