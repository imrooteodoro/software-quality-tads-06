package app;

import LadoClient.LadoClient;
import MaquinaRefri.Caixa;
import MaquinaRefri.MaquinaRefri;
import MaquinaRefri.Produto;

import java.util.Map;
import java.util.Scanner;

public class Main {
    private static final String OWNER_USER = "owner";
    private static final String OWNER_PASS = "admin123";

    public static void main(String[] args) {
        MaquinaRefri maquina = new MaquinaRefri();
        LadoClient cliente = new LadoClient(maquina);
        maquina.iniciar();

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- Máquina de Refrigerante ---");
            System.out.println("1) Entrar como Cliente");
            System.out.println("2) Entrar como Proprietário");
            System.out.println("0) Sair");
            System.out.print("Escolha: ");
            String opt = sc.nextLine().trim();
            if (opt.equals("0")) {
                System.out.println("Saindo...");
                break;
            }

            if (opt.equals("1")) {
                clienteLoop(sc, cliente, maquina);
            } else if (opt.equals("2")) {
                System.out.print("Usuário: ");
                String user = sc.nextLine().trim();
                System.out.print("Senha: ");
                String pass = sc.nextLine();
                if (OWNER_USER.equals(user) && OWNER_PASS.equals(pass)) {
                    ownerLoop(sc, maquina);
                } else {
                    System.out.println("Credenciais inválidas.");
                }
            } else {
                System.out.println("Opção inválida.");
            }
        }
        sc.close();
    }

    private static void clienteLoop(Scanner sc, LadoClient cliente, MaquinaRefri maquina) {
        while (true) {
            System.out.println("\n--- Menu Cliente ---");
            System.out.println("Produtos disponíveis:");
            for (int i = 0; i < maquina.getEstoque().size(); i++) {
                var it = maquina.getEstoque().get(i);
                System.out.printf("%d) %s - R$ %.2f - qtd: %d\n", i, it.getProduto().getNome(), it.getProduto().getPreco(), it.getQuantidade());
            }
            System.out.println("c) Voltar");
            System.out.print("Escolha o índice do produto: ");
            String in = sc.nextLine().trim();
            if (in.equalsIgnoreCase("c")) return;
            int idx;
            try {
                idx = Integer.parseInt(in);
            } catch (NumberFormatException e) {
                System.out.println("Índice inválido.");
                continue;
            }

            if (idx < 0 || idx >= maquina.getEstoque().size()) {
                System.out.println("Produto inválido.");
                continue;
            }

            System.out.println("Insira a quantidade de notas/moedas: (apenas números inteiros)");
            int dez = readInt(sc, "Notas R$10: ");
            int cinco = readInt(sc, "Notas R$5: ");
            int dois = readInt(sc, "Notas R$2: ");
            int um = readInt(sc, "Moedas R$1: ");
            int cinquenta = readInt(sc, "Moedas R$0.50: ");

            Map<Integer, Integer> insercao = LadoClient.inserirNotas(dez, cinco, dois, um, cinquenta);
            String resultado = cliente.efetuarCompra(insercao, idx);
            System.out.println(resultado);

            System.out.print("Deseja comprar outro produto? (s/n): ");
            String resp = sc.nextLine().trim();
            if (!resp.equalsIgnoreCase("s")) return;
        }
    }

    private static void ownerLoop(Scanner sc, MaquinaRefri maquina) {
        while (true) {
            System.out.println("\n--- Menu Proprietário ---");
            System.out.println("1) Adicionar dinheiro ao caixa");
            System.out.println("2) Adicionar produto / Repor");
            System.out.println("3) Ver relatório");
            System.out.println("0) Logout");
            System.out.print("Escolha: ");
            String op = sc.nextLine().trim();
            if (op.equals("0")) return;
            if (op.equals("1")) {
                System.out.println("Escolha denominação:");
                System.out.println("1) R$10");
                System.out.println("2) R$5");
                System.out.println("3) R$2");
                System.out.println("4) R$1");
                System.out.println("5) R$0.50");
                System.out.print("Opção: ");
                String d = sc.nextLine().trim();
                int denom = 0;
                switch (d) {
                    case "1": denom = Caixa.TEN_REAL; break;
                    case "2": denom = Caixa.FIVE_REAL; break;
                    case "3": denom = Caixa.TWO_REAL; break;
                    case "4": denom = Caixa.ONE_REAL; break;
                    case "5": denom = Caixa.FIFTY_CENT; break;
                    default: System.out.println("Denominação inválida."); continue;
                }
                int qtd = readInt(sc, "Quantidade: ");
                maquina.adicionarDinheiroAoCaixa(denom, qtd);
                System.out.println("Dinheiro adicionado ao caixa.");
            } else if (op.equals("2")) {
                System.out.print("Nome do produto: ");
                String nome = sc.nextLine().trim();
                double preco = readDouble(sc, "Preço (ex: 3.50): ");
                int qtd = readInt(sc, "Quantidade: ");
                maquina.adicionarProduto(new Produto(nome, preco), qtd);
                System.out.println("Produto adicionado/reposto.");
            } else if (op.equals("3")) {
                System.out.println(maquina.relatorio());
            } else {
                System.out.println("Opção inválida.");
            }
        }
    }

    private static int readInt(Scanner sc, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String s = sc.nextLine().trim();
                int v = Integer.parseInt(s);
                if (v < 0) {
                    System.out.println("Digite um número não-negativo.");
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida, digite um inteiro.");
            }
        }
    }

    private static double readDouble(Scanner sc, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String s = sc.nextLine().trim().replace(',', '.');
                double v = Double.parseDouble(s);
                if (v < 0) {
                    System.out.println("Digite um número não-negativo.");
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida, digite um número.");
            }
        }
    }
}

