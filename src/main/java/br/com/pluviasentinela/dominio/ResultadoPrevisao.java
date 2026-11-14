package br.com.pluviasentinela.dominio;

import br.com.pluviasentinela.excecao.DadosInvalidosException;

public record ResultadoPrevisao(double valorPrevisto, String unidadeMedida, String explicacao) {

    public ResultadoPrevisao {
        if (!Double.isFinite(valorPrevisto) || valorPrevisto < 0) {
            throw new DadosInvalidosException("valorPrevisto", valorPrevisto,
                    "a previsao deve ser finita e nao negativa");
        }
        unidadeMedida = validarTexto(unidadeMedida, "unidadeMedida");
        explicacao = validarTexto(explicacao, "explicacao");
    }

    private static String validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new DadosInvalidosException(campo, valor, "o texto deve ser informado");
        }
        return valor.trim();
    }
}
