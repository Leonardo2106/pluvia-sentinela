package br.com.pluviasentinela.dominio;

import java.util.List;
import java.util.Locale;

public class PrevisaoMediaMovel extends EstrategiaPrevisao {

    private static final int JANELA = 5;

    public PrevisaoMediaMovel() {
        super("Media movel", JANELA);
    }

    @Override
    public ResultadoPrevisao prever(List<Medicao> historico) {
        List<Medicao> leiturasRecentes = prepararHistorico(historico).stream()
                .limit(JANELA)
                .toList();

        double media = leiturasRecentes.stream()
                .mapToDouble(Medicao::getValor)
                .average()
                .orElseThrow();

        String unidade = leiturasRecentes.getFirst().getUnidadeMedida();
        String explicacao = String.format(Locale.forLanguageTag("pt-BR"),
                "Media das %d leituras mais recentes: %.2f %s.", JANELA, media, unidade);

        return new ResultadoPrevisao(media, unidade, explicacao);
    }
}
