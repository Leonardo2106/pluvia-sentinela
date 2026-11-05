package br.com.pluviasentinela.dominio;

import br.com.pluviasentinela.excecao.DadosInvalidosException;
import br.com.pluviasentinela.excecao.FonteIndisponivelException;

import java.util.Random;

public abstract class SensorSimulado implements Sensor {

    private final String identificador;
    private final String regiao;
    private final Random gerador;
    private boolean funcionando;

    protected SensorSimulado(String identificador, String regiao) {
        this(identificador, regiao, new Random());
    }

    protected SensorSimulado(String identificador, String regiao, Random gerador) {
        this.identificador = validarTexto(identificador, "identificador");
        this.regiao = validarTexto(regiao, "regiao");
        if (gerador == null) {
            throw new DadosInvalidosException("gerador", null, "o gerador deve ser informado");
        }
        this.gerador = gerador;
        this.funcionando = true;
    }

    protected final double gerarLeitura(double minimo, double maximo) {
        if (!verificarFuncionamento()) {
            throw new FonteIndisponivelException(identificador);
        }
        return minimo + (maximo - minimo) * gerador.nextDouble();
    }

    protected final double validarFaixa(double valor, double minimo, double maximo) {
        if (!Double.isFinite(valor) || valor < minimo || valor > maximo) {
            throw new DadosInvalidosException("medicao", valor,
                    "o valor deve estar entre " + minimo + " e " + maximo);
        }
        return valor;
    }

    private String validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new DadosInvalidosException(campo, valor, "o texto deve ser informado");
        }
        return valor.trim();
    }

    @Override
    public boolean verificarFuncionamento() {
        return funcionando;
    }

    public void setFuncionando(boolean funcionando) {
        this.funcionando = funcionando;
    }

    @Override
    public String getIdentificador() {
        return identificador;
    }

    @Override
    public String getRegiao() {
        return regiao;
    }
}
