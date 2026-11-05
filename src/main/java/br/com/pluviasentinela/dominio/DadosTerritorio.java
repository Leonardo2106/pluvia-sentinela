package br.com.pluviasentinela.dominio;

import br.com.pluviasentinela.excecao.DadosInvalidosException;

public class DadosTerritorio {

    private String nomeRegiao;
    private int populacaoEstimada;
    private double limiarAtencao;
    private double limiarAlerta;
    private double limiarCritico;
    private final String codigoAcesso;

    public DadosTerritorio(String nomeRegiao, int populacaoEstimada,
                           double limiarAtencao, double limiarAlerta, double limiarCritico,
                           String codigoAcesso) {
        setNomeRegiao(nomeRegiao);
        setPopulacaoEstimada(populacaoEstimada);
        this.codigoAcesso = validarCodigo(codigoAcesso);
        definirLimiares(limiarAtencao, limiarAlerta, limiarCritico);
    }

    public void alterarLimiares(String codigoInformado, double atencao, double alerta, double critico) {
        validarAcesso(codigoInformado);
        definirLimiares(atencao, alerta, critico);
    }

    public void alterarPopulacaoEstimada(String codigoInformado, int populacaoEstimada) {
        validarAcesso(codigoInformado);
        setPopulacaoEstimada(populacaoEstimada);
    }

    private void definirLimiares(double atencao, double alerta, double critico) {
        if (!Double.isFinite(atencao) || !Double.isFinite(alerta) || !Double.isFinite(critico)
                || atencao < 0 || atencao >= alerta || alerta >= critico) {
            throw new DadosInvalidosException("limiares", atencao + "/" + alerta + "/" + critico,
                    "use numeros nao negativos em ordem crescente: atencao < alerta < critico");
        }
        this.limiarAtencao = atencao;
        this.limiarAlerta = alerta;
        this.limiarCritico = critico;
    }

    private void validarAcesso(String codigoInformado) {
        if (codigoInformado == null || !codigoAcesso.equals(codigoInformado)) {
            throw new DadosInvalidosException("codigoAcesso", "protegido", "codigo de acesso incorreto");
        }
    }

    private String validarCodigo(String codigo) {
        if (codigo == null || codigo.isBlank() || codigo.length() < 4) {
            throw new DadosInvalidosException("codigoAcesso", "protegido",
                    "informe um codigo com pelo menos 4 caracteres");
        }
        return codigo;
    }

    public String getNomeRegiao() {
        return nomeRegiao;
    }

    public void setNomeRegiao(String nomeRegiao) {
        if (nomeRegiao == null || nomeRegiao.isBlank()) {
            throw new DadosInvalidosException("nomeRegiao", nomeRegiao, "a regiao deve ser informada");
        }
        this.nomeRegiao = nomeRegiao.trim();
    }

    public int getPopulacaoEstimada() {
        return populacaoEstimada;
    }

    private void setPopulacaoEstimada(int populacaoEstimada) {
        if (populacaoEstimada < 0) {
            throw new DadosInvalidosException("populacaoEstimada", populacaoEstimada,
                    "a populacao nao pode ser negativa");
        }
        this.populacaoEstimada = populacaoEstimada;
    }

    public double getLimiarAtencao() {
        return limiarAtencao;
    }

    public double getLimiarAlerta() {
        return limiarAlerta;
    }

    public double getLimiarCritico() {
        return limiarCritico;
    }
}
