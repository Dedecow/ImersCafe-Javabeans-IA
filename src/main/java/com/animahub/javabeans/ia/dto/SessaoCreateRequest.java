package com.animahub.javabeans.ia.dto;

/**
 * DTO de request para criação de uma nova sessão de jogo.
 */
public class SessaoCreateRequest {

    private String nomeUsuario;
    private String nomePersonagem;

    public SessaoCreateRequest() {}

    public SessaoCreateRequest(String nomeUsuario, String nomePersonagem) {
        this.nomeUsuario = nomeUsuario;
        this.nomePersonagem = nomePersonagem;
    }

    public String getNomeUsuario() { return nomeUsuario; }
    public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }

    public String getNomePersonagem() { return nomePersonagem; }
    public void setNomePersonagem(String nomePersonagem) { this.nomePersonagem = nomePersonagem; }
}
