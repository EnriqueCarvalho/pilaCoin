package br.ufsm.csi.pilaCoin.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;


import java.util.List;


public class BlocoValidado {


    private Long id;

    private byte[] chaveUsuarioMinerador;
    private String nonce;
    private byte[] hashBlocoAnterior;

    private List<Transacao> transacoes;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getChaveUsuarioMinerador() {
        return chaveUsuarioMinerador;
    }

    public void setChaveUsuarioMinerador(byte[] chaveUsuarioMinerador) {
        this.chaveUsuarioMinerador = chaveUsuarioMinerador;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public byte[] getHashBlocoAnterior() {
        return hashBlocoAnterior;
    }

    public void setHashBlocoAnterior(byte[] hashBlocoAnterior) {
        this.hashBlocoAnterior = hashBlocoAnterior;
    }

    public List<Transacao> getTransacoes() {
        return transacoes;
    }

    public void setTransacoes(List<Transacao> transacoes) {
        this.transacoes = transacoes;
    }
}
