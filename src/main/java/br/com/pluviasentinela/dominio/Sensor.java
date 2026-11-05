package br.com.pluviasentinela.dominio;

public interface Sensor {

    double lerValor();

    boolean verificarFuncionamento();

    String retornarTipo();

    String getIdentificador();

    String getRegiao();

    String getUnidadeMedida();
}
