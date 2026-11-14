package br.com.pluviasentinela.dominio;

import java.util.List;
import java.util.Locale;

public class PrevisaoComparacao extends EstrategiaPrevisao {

    private static final int TAMANHO_PERIODO = 3;
    private static final int TOTAL_LEITURAS = TAMANHO_PERIODO * 2;

    public PrevisaoComparacao() {
        super("Comparacao de periodos", TOTAL_LEITURAS);
    }

    @Override
    public ResultadoPrevisao prever(List<Medicao> historico) {
        List<Medicao> leituras = prepararHistorico(historico).stream()
                .limit(TOTAL_LEITURAS)
                .toList();

        double mediaRecente = calcularMedia(leituras.subList(0, TAMANHO_PERIODO));
        double mediaAnterior = calcularMedia(leituras.subList(TAMANHO_PERIODO, TOTAL_LEITURAS));
        double tendencia = mediaRecente - mediaAnterior;
        double previsao = Math.max(0, mediaRecente + tendencia);
        String unidade = leituras.getFirst().getUnidadeMedida();

        String explicacao = String.format(Locale.forLanguageTag("pt-BR"),
                "Media recente: %.2f %s; media anterior: %.2f %s; tendencia: %+.2f %s.",
                mediaRecente, unidade, mediaAnterior, unidade, tendencia, unidade);

        return new ResultadoPrevisao(previsao, unidade, explicacao);
    }

    private double calcularMedia(List<Medicao> medicoes) {
        return medicoes.stream()
                .mapToDouble(Medicao::getValor)
                .average()
                .orElseThrow();
    }
}
