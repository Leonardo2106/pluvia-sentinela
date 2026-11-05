package br.com.pluviasentinela.dominio;

public class SensorPluviometrico extends SensorSimulado {

    private static final double CHUVA_MINIMA = 0.0;
    private static final double CHUVA_MAXIMA = 200.0;

    public SensorPluviometrico(String identificador, String regiao) {
        super(identificador, regiao);
    }

    @Override
    public double lerValor() {
        return validarFaixa(gerarLeitura(CHUVA_MINIMA, CHUVA_MAXIMA), CHUVA_MINIMA, CHUVA_MAXIMA);
    }

    @Override
    public String retornarTipo() {
        return "PLUVIOMETRICO";
    }

    @Override
    public String getUnidadeMedida() {
        return "mm/h";
    }
}
