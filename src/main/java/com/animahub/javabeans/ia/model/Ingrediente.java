package com.animahub.javabeans.ia.model;

public enum Ingrediente {
    CAFE("Café"), LEITE("Leite"), CHOCOLATE("Chocolate"), 
    ESPUMA("Espuma de Leite"), CHANTILLY("Chantilly"), 
    CARAMELO("Caramelo"), CANELA("Canela"), ACUCAR("Açúcar"), 
    BAUNILHA("Baunilha"), CREME("Creme"), AGUA("Água"), GELO("Gelo");

    private final String nome;

    Ingrediente(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
}