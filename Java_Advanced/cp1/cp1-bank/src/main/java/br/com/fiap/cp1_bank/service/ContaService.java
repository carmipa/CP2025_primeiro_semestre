package br.com.fiap.cp1_bank.service;

import br.com.fiap.cp1_bank.model.Conta;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ContaService {

    // Armazena as contas em memória, usando o número da conta como chave.
    private final Map<Long, Conta> contas = new ConcurrentHashMap<>();

    // Cria e armazena a conta
    public Conta criarConta(Conta conta) {
        contas.put(conta.getNumero(), conta);
        return conta;
    }

    // Retorna uma lista com todas as contas cadastradas
    public List<Conta> getTodasContas() {
        return new ArrayList<>(contas.values());
    }

    // Busca uma conta pelo número (ID)
    public Conta getContaPorNumero(long numero) {
        return contas.get(numero);
    }

    // Busca uma conta pelo CPF do titular
    public Conta getContaPorCpf(String cpf) {
        return contas.values().stream()
                .filter(c -> c.getCpfTitular().equals(cpf))
                .findFirst()
                .orElse(null);
    }

    // Encerrar conta: marca a conta como inativa
    public Conta encerrarConta(long numero) {
        Conta conta = contas.get(numero);
        if (conta != null) {
            conta.setAtiva(false);
        }
        return conta;
    }

    // Depósito: adiciona o valor ao saldo da conta
    public Conta depositar(long numero, double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor do depósito deve ser positivo");
        }
        Conta conta = contas.get(numero);
        if (conta == null) {
            throw new IllegalArgumentException("Conta não encontrada");
        }
        conta.setSaldoInicial(conta.getSaldoInicial() + valor);
        return conta;
    }

    // Saque: subtrai o valor do saldo da conta, se houver saldo suficiente
    public Conta sacar(long numero, double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor do saque deve ser positivo");
        }
        Conta conta = contas.get(numero);
        if (conta == null) {
            throw new IllegalArgumentException("Conta não encontrada");
        }
        if (conta.getSaldoInicial() < valor) {
            throw new IllegalArgumentException("Saldo insuficiente para saque");
        }
        conta.setSaldoInicial(conta.getSaldoInicial() - valor);
        return conta;
    }

    // PIX: transfere o valor de uma conta para outra
    public Conta pix(long contaOrigemId, long contaDestinoId, double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor do PIX deve ser positivo");
        }
        Conta contaOrigem = contas.get(contaOrigemId);
        Conta contaDestino = contas.get(contaDestinoId);
        if (contaOrigem == null || contaDestino == null) {
            throw new IllegalArgumentException("Conta de origem ou destino não encontrada");
        }
        if (contaOrigem.getSaldoInicial() < valor) {
            throw new IllegalArgumentException("Saldo insuficiente para PIX");
        }
        contaOrigem.setSaldoInicial(contaOrigem.getSaldoInicial() - valor);
        contaDestino.setSaldoInicial(contaDestino.getSaldoInicial() + valor);
        return contaOrigem;
    }
}
