package br.com.fiap.cp1_bank.controller;

import br.com.fiap.cp1_bank.model.Conta;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/contas")
public class ContaController {

    private List<Conta> contas = new ArrayList<>();

    // Endpoint para cadastrar conta com validações
    @PostMapping
    public ResponseEntity<?> criarConta(@RequestBody Conta conta) {
        // Validações simples
        if (conta.getNomeTitular() == null || conta.getNomeTitular().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Nome do titular é obrigatório");
        }
        if (conta.getCpfTitular() == null || conta.getCpfTitular().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("CPF do titular é obrigatório");
        }
        try {
            LocalDate dataAbertura = LocalDate.parse(conta.getDataAbertura());
            if (dataAbertura.isAfter(LocalDate.now())) {
                return ResponseEntity.badRequest().body("Data de abertura não pode ser no futuro");
            }
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Data de abertura inválida");
        }
        if (conta.getSaldoInicial() < 0) {
            return ResponseEntity.badRequest().body("Saldo inicial não pode ser negativo");
        }
        // Validando tipo da conta (corrente, poupança ou salário)
        if (conta.getTipoConta() == null ||
                (!conta.getTipoConta().equalsIgnoreCase("corrente") &&
                        !conta.getTipoConta().equalsIgnoreCase("poupança") &&
                        !conta.getTipoConta().equalsIgnoreCase("salário"))) {
            return ResponseEntity.badRequest().body("Tipo de conta inválido. Deve ser 'corrente', 'poupança' ou 'salário'");
        }

        contas.add(conta);
        return ResponseEntity.status(HttpStatus.CREATED).body(conta);
    }

    // Endpoint para listar todas as contas
    @GetMapping
    public List<Conta> listarContas() {
        return contas;
    }

    // Endpoint para buscar uma conta pelo número
    @GetMapping("/{numero}")
    public ResponseEntity<Conta> buscarConta(@PathVariable long numero) {
        Conta conta = contas.stream()
                .filter(c -> c.getNumero() == numero)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada"));
        return ResponseEntity.ok(conta);
    }

    // Endpoint para encerrar conta (marcar como inativa)
    @DeleteMapping("/{numero}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void encerrarConta(@PathVariable long numero) {
        Conta conta = contas.stream()
                .filter(c -> c.getNumero() == numero)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada"));
        conta.setAtiva(false);
    }

    // Endpoint para depósito
    @PostMapping("/{numero}/deposito")
    public ResponseEntity<?> depositar(@PathVariable long numero, @RequestParam double valor) {
        Conta conta = contas.stream()
                .filter(c -> c.getNumero() == numero)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada"));
        if (valor <= 0) {
            return ResponseEntity.badRequest().body("Valor de depósito deve ser positivo");
        }
        conta.setSaldoInicial(conta.getSaldoInicial() + valor);
        return ResponseEntity.ok(conta);
    }

    // Endpoint para saque
    @PostMapping("/{numero}/saque")
    public ResponseEntity<?> sacar(@PathVariable long numero, @RequestParam double valor) {
        Conta conta = contas.stream()
                .filter(c -> c.getNumero() == numero)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada"));
        if (valor <= 0) {
            return ResponseEntity.badRequest().body("Valor de saque deve ser positivo");
        }
        if (valor > conta.getSaldoInicial()) {
            return ResponseEntity.badRequest().body("Saldo insuficiente para saque");
        }
        conta.setSaldoInicial(conta.getSaldoInicial() - valor);
        return ResponseEntity.ok(conta);
    }

    // Endpoint para realizar PIX entre contas
    @PostMapping("/pix")
    public ResponseEntity<?> realizarPix(@RequestParam long contaOrigem,
                                         @RequestParam long contaDestino,
                                         @RequestParam double valor) {
        Conta origem = contas.stream()
                .filter(c -> c.getNumero() == contaOrigem)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta de origem não encontrada"));
        Conta destino = contas.stream()
                .filter(c -> c.getNumero() == contaDestino)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta de destino não encontrada"));

        if (valor <= 0) {
            return ResponseEntity.badRequest().body("Valor do PIX deve ser positivo");
        }
        if (valor > origem.getSaldoInicial()) {
            return ResponseEntity.badRequest().body("Saldo insuficiente na conta de origem");
        }
        origem.setSaldoInicial(origem.getSaldoInicial() - valor);
        destino.setSaldoInicial(destino.getSaldoInicial() + valor);
        return ResponseEntity.ok(origem);
    }
}
