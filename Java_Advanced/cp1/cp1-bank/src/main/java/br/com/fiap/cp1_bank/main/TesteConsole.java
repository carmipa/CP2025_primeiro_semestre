package br.com.fiap.cp1_bank.main;

import br.com.fiap.cp1_bank.model.Conta;
import br.com.fiap.cp1_bank.service.ContaService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class TesteConsole implements CommandLineRunner {

    private final ContaService contaService;

    // Injete o serviço via construtor para aproveitar o bean gerenciado pelo Spring
    public TesteConsole(ContaService contaService) {
        this.contaService = contaService;
    }

    @Override
    public void run(String... args) {
        // Inicia o menu interativo em uma nova thread para não bloquear o servidor web
        new Thread(this::executarMenuConsole).start();
    }

    private void executarMenuConsole() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n==== Menu de Operações ====");
            System.out.println("1. Criar conta");
            System.out.println("2. Realizar depósito");
            System.out.println("3. Realizar saque");
            System.out.println("4. Realizar PIX");
            System.out.println("5. Encerrar conta");
            System.out.println("6. Listar contas");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");
            int opcao = scanner.nextInt();
            scanner.nextLine(); // consumir a quebra de linha

            switch (opcao) {
                case 1:
                    System.out.print("Número da conta: ");
                    long numero = scanner.nextLong();
                    System.out.print("Agência: ");
                    long agencia = scanner.nextLong();
                    scanner.nextLine(); // consumir a quebra de linha
                    System.out.print("Nome do titular: ");
                    String nome = scanner.nextLine();
                    System.out.print("CPF do titular: ");
                    String cpf = scanner.nextLine();
                    System.out.print("Data de abertura (AAAA-MM-DD): ");
                    String dataAbertura = scanner.nextLine();
                    System.out.print("Saldo inicial: ");
                    double saldo = scanner.nextDouble();
                    scanner.nextLine();
                    System.out.print("Conta ativa? (true/false): ");
                    boolean ativa = scanner.nextBoolean();
                    scanner.nextLine();
                    System.out.print("Tipo de conta (corrente, poupança ou salário): ");
                    String tipo = scanner.nextLine();

                    Conta conta = new Conta(numero, agencia, nome, cpf, dataAbertura, saldo, ativa, tipo);
                    contaService.criarConta(conta);
                    System.out.println("Conta criada com sucesso:\n" + conta);
                    break;
                case 2:
                    System.out.print("Número da conta para depósito: ");
                    long numDeposito = scanner.nextLong();
                    System.out.print("Valor do depósito: ");
                    double valorDeposito = scanner.nextDouble();
                    scanner.nextLine();
                    try {
                        Conta contaDeposito = contaService.depositar(numDeposito, valorDeposito);
                        System.out.println("Depósito realizado. Conta atualizada:\n" + contaDeposito);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Erro: " + e.getMessage());
                    }
                    break;
                case 3:
                    System.out.print("Número da conta para saque: ");
                    long numSaque = scanner.nextLong();
                    System.out.print("Valor do saque: ");
                    double valorSaque = scanner.nextDouble();
                    scanner.nextLine();
                    try {
                        Conta contaSaque = contaService.sacar(numSaque, valorSaque);
                        System.out.println("Saque realizado. Conta atualizada:\n" + contaSaque);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Erro: " + e.getMessage());
                    }
                    break;
                case 4:
                    System.out.print("Número da conta de origem: ");
                    long contaOrigem = scanner.nextLong();
                    System.out.print("Número da conta de destino: ");
                    long contaDestino = scanner.nextLong();
                    System.out.print("Valor do PIX: ");
                    double valorPix = scanner.nextDouble();
                    scanner.nextLine();
                    try {
                        Conta contaPix = contaService.pix(contaOrigem, contaDestino, valorPix);
                        System.out.println("PIX realizado com sucesso. Conta de origem atualizada:\n" + contaPix);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Erro: " + e.getMessage());
                    }
                    break;
                case 5:
                    System.out.print("Número da conta para encerrar: ");
                    long numEncerrar = scanner.nextLong();
                    scanner.nextLine();
                    Conta contaEncerrada = contaService.encerrarConta(numEncerrar);
                    if (contaEncerrada != null) {
                        System.out.println("Conta encerrada:\n" + contaEncerrada);
                    } else {
                        System.out.println("Conta não encontrada.");
                    }
                    break;
                case 6:
                    System.out.println("Contas cadastradas:");
                    for (Conta c : contaService.getTodasContas()) {
                        System.out.println(c);
                    }
                    break;
                case 0:
                    System.out.println("Encerrando o programa...");
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }
}
