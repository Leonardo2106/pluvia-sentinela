package br.com.pluviasentinela.dominio;

import br.com.pluviasentinela.excecao.DadosInvalidosException;

import java.util.Comparator;
import java.util.List;

public abstract class EstrategiaPrevisao {

    private final String nome;
    private final int minimoLeituras;

    protected EstrategiaPrevisao(String nome, int minimoLeituras) {
        if (nome == null || nome.isBlank()) {
            throw new DadosInvalidosException("nome", nome, "o nome da estrategia deve ser informado");
        }
        if (minimoLeituras <= 0) {
            throw new DadosInvalidosException("minimoLeituras", minimoLeituras,
                    "a quantidade minima deve ser positiva");
        }
        this.nome = nome.trim();
        this.minimoLeituras = minimoLeituras;
    }

    public abstract ResultadoPrevisao prever(List<Medicao> historico);

    protected List<Medicao> prepararHistorico(List<Medicao> historico) {
        if (historico == null || historico.size() < minimoLeituras) {
            int quantidade = historico == null ? 0 : historico.size();
            throw new DadosInvalidosException("historico", quantidade,
                    "a estrategia " + nome + " exige pelo menos " + minimoLeituras + " leituras");
        }

        if (historico.stream().anyMatch(medicao -> medicao == null)) {
            throw new DadosInvalidosException("historico", "nulo",
                    "o historico nao pode conter medicoes nulas");
        }

        String sensor = historico.getFirst().getSensorIdentificador();
        String unidade = historico.getFirst().getUnidadeMedida();
        boolean dadosIncompativeis = historico.stream().anyMatch(medicao ->
                !sensor.equals(medicao.getSensorIdentificador())
                        || !unidade.equals(medicao.getUnidadeMedida()));

        if (dadosIncompativeis) {
            throw new DadosInvalidosException("historico", sensor,
                    "todas as medicoes devem pertencer ao mesmo sensor e unidade");
        }

        return historico.stream()
                .sorted(Comparator.comparing(Medicao::getRegistradaEm).reversed())
                .toList();
    }

    public String getNome() {
        return nome;
    }

    public int getMinimoLeituras() {
        return minimoLeituras;
    }
}
