package br.com.fiap.cp1_bank.dto;

public class PixRequest {
    private long contaOrigem;
    private long contaDestino;
    private double valor;

    public long getContaOrigem() {
        return contaOrigem;
    }

    public void setContaOrigem(long contaOrigem) {
        this.contaOrigem = contaOrigem;
    }

    public long getContaDestino() {
        return contaDestino;
    }

    public void setContaDestino(long contaDestino) {
        this.contaDestino = contaDestino;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
