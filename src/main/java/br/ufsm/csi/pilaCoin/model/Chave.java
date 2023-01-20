package br.ufsm.csi.pilaCoin.model;

public class Chave {
    private byte[] publica;
    private byte[] privada;

    public byte[] getPublica() {
        return publica;
    }

    public void setPublica(byte[] publica) {
        this.publica = publica;
    }

    public byte[] getPrivada() {
        return privada;
    }

    public void setPrivada(byte[] privada) {
        this.privada = privada;
    }
}
