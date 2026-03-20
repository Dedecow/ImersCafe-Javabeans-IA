package com.animahub.javabeans.ia.engine;

public enum TipoCliente {
    CALMO(5, "Cliente calmo, quer se sentir acolhido. "),
    APRESSADO(10, "Cliente com pressa, quer rapidez. "),
    ESTETICO(15, "Cliente estética, quer ser cativada. "),
    EXECUTIVO(20, "Cliente executivo, quer agilidade. "),
    ESPECIALISTA(25, "Cliente fiel do estabelecimento, quer ser surpreendido"),
    INDECISO(30, "Cliente confuso, quer clareza. ");

    private int pontos;
    private String descricao;

    TipoCliente (int pontos, String descricao) {
        this.pontos = pontos;
        this.descricao = descricao;
    }

    public int getPontos(){
        return pontos;
    }

    public String getDescricao(){
        return descricao;
    }
}
