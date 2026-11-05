package br.com.pluviasentinela.dominio;

import br.com.pluviasentinela.excecao.DadosInvalidosException;

public abstract class ComponenteTerritorial {

    private final String id;
    private String nome;
    private String regiao;
    private StatusComponente status;

    protected ComponenteTerritorial(String id, String nome, String regiao, StatusComponente status) {
        this.id = validarTexto(id, "id");
        setNome(nome);
        setRegiao(regiao);
        setStatus(status);
    }

    public abstract String verificarStatus();

    protected final String validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new DadosInvalidosException(campo, valor, "o texto deve ser informado");
        }
        return valor.trim();
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = validarTexto(nome, "nome");
    }

    public String getRegiao() {
        return regiao;
    }

    public void setRegiao(String regiao) {
        this.regiao = validarTexto(regiao, "regiao");
    }

    public StatusComponente getStatus() {
        return status;
    }

    public void setStatus(StatusComponente status) {
        if (status == null) {
            throw new DadosInvalidosException("status", null, "o status deve ser informado");
        }
        this.status = status;
    }
}
