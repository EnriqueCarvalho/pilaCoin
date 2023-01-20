package br.ufsm.csi.pilaCoin.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;


import java.util.List;

@Entity
public class Bloco {

    @Id
    @GeneratedValue
    private Long id;
    private Long nonce;
    private byte[] hashBlocoAnterior;
    @OneToMany(mappedBy = "bloco")
    private List<Transacao> transacoes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNonce() {
        return nonce;
    }

    public void setNonce(Long nonce) {
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
