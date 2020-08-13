package com.example.ifoodclone.model;

import com.example.ifoodclone.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class Produto {

    private String idUsuario;
    private String idProduto;
    private String nome;
    private String descrcao;
    private Double preco;

    private String urlImagem;


    public Produto() {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference produtoRef = firebaseRef.child("produtos");
        setIdProduto(produtoRef.push().getKey());
    }

    public void salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference produtoRef = firebaseRef.child("produtos").child(getIdUsuario()).child(getIdProduto());
        produtoRef.setValue(this);

    }

    public void remover(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference produtoRef = firebaseRef.child("produtos").child(getIdUsuario()).child(getIdProduto());
        produtoRef.removeValue();
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(String idProduto) {
        this.idProduto = idProduto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescrcao() {
        return descrcao;
    }

    public void setDescrcao(String descrcao) {
        this.descrcao = descrcao;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }
}
