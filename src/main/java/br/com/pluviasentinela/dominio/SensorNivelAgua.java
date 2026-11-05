package br.com.pluviasentinela.dominio;

public class SensorNivelAgua extends SensorSimulado {

    private static final double NIVEL_MINIMO = 0.0;
    private static final double NIVEL_MAXIMO = 500.0;

    public SensorNivelAgua(String identificador, String regiao) {
        super(identificador, regiao);
    }

    @Override
    public double lerValor() {
        return validarFaixa(gerarLeitura(NIVEL_MINIMO, NIVEL_MAXIMO), NIVEL_MINIMO, NIVEL_MAXIMO);
    }

    @Override
    public String retornarTipo() {
        return "NIVEL_AGUA";
    }

    @Override
    public String getUnidadeMedida() {
        return "cm";
    }
}
