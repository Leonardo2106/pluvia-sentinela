package br.com.pluviasentinela.dominio;

import br.com.pluviasentinela.excecao.DadosInvalidosException;

public class PontoVulneravel extends ComponenteTerritorial {

    private double capacidadeDrenagemMmHora;
    private boolean drenagemObstruida;

    public PontoVulneravel(String id, String nome, String regiao, StatusComponente status,
                           double capacidadeDrenagemMmHora) {
        super(id, nome, regiao, status);
        setCapacidadeDrenagemMmHora(capacidadeDrenagemMmHora);
    }

    @Override
    public String verificarStatus() {
        if (getStatus() != StatusComponente.ATIVO) {
            return "Ponto " + getNome() + " indisponivel: " + getStatus() + ".";
        }
        if (drenagemObstruida) {
            return "Ponto " + getNome() + " ativo, mas com drenagem obstruida.";
        }
        return "Ponto " + getNome() + " ativo, com capacidade de drenagem de "
                + capacidadeDrenagemMmHora + " mm/h.";
    }

    public double getCapacidadeDrenagemMmHora() {
        return capacidadeDrenagemMmHora;
    }

    public void setCapacidadeDrenagemMmHora(double capacidadeDrenagemMmHora) {
        if (!Double.isFinite(capacidadeDrenagemMmHora) || capacidadeDrenagemMmHora <= 0) {
            throw new DadosInvalidosException("capacidadeDrenagemMmHora", capacidadeDrenagemMmHora,
                    "a capacidade deve ser um numero positivo");
        }
        this.capacidadeDrenagemMmHora = capacidadeDrenagemMmHora;
    }

    public boolean isDrenagemObstruida() {
        return drenagemObstruida;
    }

    public void setDrenagemObstruida(boolean drenagemObstruida) {
        this.drenagemObstruida = drenagemObstruida;
    }
}
