package br.com.pluviasentinela.excecao;

public class FonteIndisponivelException extends RuntimeException {

    public FonteIndisponivelException(String identificadorSensor) {
        super("Nao foi possivel obter a leitura do sensor '" + identificadorSensor
                + "'. Verifique sua conexao ou seu estado de funcionamento.");
    }

    public FonteIndisponivelException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
