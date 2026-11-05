package br.com.pluviasentinela.dominio;

public class SensorUmidadeSolo extends SensorSimulado {

    private static final double UMIDADE_MINIMA = 0.0;
    private static final double UMIDADE_MAXIMA = 100.0;

    public SensorUmidadeSolo(String identificador, String regiao) {
        super(identificador, regiao);
    }

    @Override
    public double lerValor() {
        return validarFaixa(gerarLeitura(UMIDADE_MINIMA, UMIDADE_MAXIMA), UMIDADE_MINIMA, UMIDADE_MAXIMA);
    }

    @Override
    public String retornarTipo() {
        return "UMIDADE_SOLO";
    }

    @Override
    public String getUnidadeMedida() {
        return "%";
    }
}
