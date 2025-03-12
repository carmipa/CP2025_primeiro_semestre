package br.com.fiap.cp1_bank.controller;

import br.com.fiap.cp1_bank.dto.DepositoRequest;
import br.com.fiap.cp1_bank.dto.PixRequest;
import br.com.fiap.cp1_bank.dto.SaqueRequest;
import br.com.fiap.cp1_bank.model.Conta;
import br.com.fiap.cp1_bank.service.ContaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
public class ContaController {

    private final ContaService contaService;

    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    // Endpoint raiz
    @GetMapping("/")
    public String home() {
        return "Projeto Bank - Integrantes: [Seu Nome e demais integrantes]";
    }

    // Cadastro de conta (criação)
    @PostMapping("/contas")
    public ResponseEntity<?> criarConta(@RequestBody Conta conta) {
        // Validações
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
            return ResponseEntity.badRequest().body("Data de abertura inválida. Use o formato AAAA-MM-DD");
        }
        if (conta.getSaldoInicial() < 0) {
            return ResponseEntity.badRequest().body("Saldo inicial não pode ser negativo");
        }
        if (!(conta.getTipoConta().equalsIgnoreCase("corrente")
                || conta.getTipoConta().equalsIgnoreCase("poupança")
                || conta.getTipoConta().equalsIgnoreCase("salário"))) {
            return ResponseEntity.badRequest().body("Tipo de conta inválido. Use: corrente, poupança ou salário");
        }

        Conta contaCriada = contaService.criarConta(conta);
        return ResponseEntity.ok(contaCriada);
    }

    // Busca: retornar todas as contas
    @GetMapping("/contas")
    public List<Conta> listarContas() {
        return contaService.getTodasContas();
    }

    // Busca: retornar conta por número (id)
    @GetMapping("/contas/{numero}")
    public ResponseEntity<?> getContaPorNumero(@PathVariable long numero) {
        Conta conta = contaService.getContaPorNumero(numero);
        if (conta == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(conta);
    }

    // Busca: retornar conta por CPF do titular
    @GetMapping("/contas/cpf/{cpf}")
    public ResponseEntity<?> getContaPorCpf(@PathVariable String cpf) {
        Conta conta = contaService.getContaPorCpf(cpf);
        if (conta == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(conta);
    }

    // Encerrar conta (inativar)
    @PutMapping("/contas/{numero}/encerrar")
    public ResponseEntity<?> encerrarConta(@PathVariable long numero) {
        Conta conta = contaService.encerrarConta(numero);
        if (conta == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(conta);
    }

    // Depósito
    @PostMapping("/contas/{numero}/deposito")
    public ResponseEntity<?> depositar(@PathVariable long numero, @RequestBody DepositoRequest deposito) {
        try {
            Conta conta = contaService.depositar(numero, deposito.getValor());
            return ResponseEntity.ok(conta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Saque
    @PostMapping("/contas/{numero}/saque")
    public ResponseEntity<?> sacar(@PathVariable long numero, @RequestBody SaqueRequest saque) {
        try {
            Conta conta = contaService.sacar(numero, saque.getValor());
            return ResponseEntity.ok(conta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Pix
    @PostMapping("/contas/pix")
    public ResponseEntity<?> pix(@RequestBody PixRequest pixRequest) {
        try {
            Conta contaOrigem = contaService.pix(
                    pixRequest.getContaOrigem(),
                    pixRequest.getContaDestino(),
                    pixRequest.getValor()
            );
            return ResponseEntity.ok(contaOrigem);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
