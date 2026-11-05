package br.com.pluviasentinela.excecao;

public class DadosInvalidosException extends RuntimeException {

    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }

    public DadosInvalidosException(String campo, Object valor, String motivo) {
        super("Dado invalido em '" + campo + "' (" + valor + "): " + motivo);
    }
}
