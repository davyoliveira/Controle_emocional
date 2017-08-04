/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ControleEmocional_classes;

import java.io.Serializable;

/**
 *
 * @author Davy
 */
public class Usuario implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String nome;
    private String senha;
    private boolean pCoracao;
    private int idade;
    private char sexo;

    public Usuario(String nome, String senha, boolean pCoracao, int idade, char sexo) {
        this.nome = nome;
        this.senha = senha;
        this.pCoracao = pCoracao;
        this.idade = idade;
        this.sexo = sexo;
    }
    
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public boolean ispCoracao() {
        return pCoracao;
    }

    public void setpCoracao(boolean pCoracao) {
        this.pCoracao = pCoracao;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public char getSexo() {
        return sexo;
    }

    public void setSexo(char sexo) {
        this.sexo = sexo;
    }
    
    
}
