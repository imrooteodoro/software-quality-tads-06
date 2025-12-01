package MaquinaRefri;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MaquinaRefri {
    private List<EstoqueItem> estoque = new ArrayList<>();
    private Caixa caixa = new Caixa();

    public MaquinaRefri() {
        inicializarProdutos();
    }

    private void inicializarProdutos() {
        // adicionar 5 tipos de bebida com preços distintos (valores em reais)
        estoque.add(new EstoqueItem(new Produto("Coca", 4.50), 10));
        estoque.add(new EstoqueItem(new Produto("Pepsi", 4.00), 8));
        estoque.add(new EstoqueItem(new Produto("Guaraná", 3.50), 12));
        estoque.add(new EstoqueItem(new Produto("Fanta", 3.75), 6));
        estoque.add(new EstoqueItem(new Produto("Sprite", 3.25), 9));
    }

    public List<EstoqueItem> getEstoque() {
        return estoque;
    }

    public Caixa getCaixa() {
        return caixa;
    }

    public boolean adicionarDinheiroAoCaixa(int valorCentavos, int quantidade) {
        caixa.adicionarNota(valorCentavos, quantidade);
        return true;
    }

    public boolean adicionarProduto(Produto p, int qtd) {
        for (EstoqueItem item : estoque) {
            if (item.getProduto().getNome().equalsIgnoreCase(p.getNome())) {
                item.adicionar(qtd);
                return true;
            }
        }
        estoque.add(new EstoqueItem(p, qtd));
        return true;
    }

    // cliente insere valores (map centavos->quantidade) e escolhe indice do produto
    public synchronized PurchaseResult comprar(Map<Integer, Integer> insercao, int indiceProduto) {
        if (indiceProduto < 0 || indiceProduto >= estoque.size()) {
            return new PurchaseResult(false, "Produto inválido", null, 0);
        }

        EstoqueItem item = estoque.get(indiceProduto);
        if (item.getQuantidade() <= 0) {
            return new PurchaseResult(false, "Produto esgotado", null, 0);
        }

        int totalInserido = 0;
        for (Map.Entry<Integer, Integer> e : insercao.entrySet()) {
            totalInserido += e.getKey() * e.getValue();
        }

        int precoCentavos = (int) Math.round(item.getProduto().getPreco() * 100);
        if (totalInserido < precoCentavos) {
            return new PurchaseResult(false, "Dinheiro insuficiente", null, totalInserido);
        }

        int trocoNecessario = totalInserido - precoCentavos;

        // NOTE: Dinheiro inserido NÃO deve misturar com o caixa interno destinado ao troco
        // Primeiro, verificamos se o caixa interno tem troco suficiente
        Map<Integer, Integer> troco = caixa.calcularTroco(trocoNecessario);
        if (troco == null && trocoNecessario > 0) {
            return new PurchaseResult(false, "Sem troco disponível", null, totalInserido);
        }

        // sucesso: atualizar estoque e atualizar caixa: inserir as notas inseridas ao caixa (após dar troco)
        item.reduzirQuantidade();

        // adicionar ao caixa as notas inseridas
        for (Map.Entry<Integer, Integer> e : insercao.entrySet()) {
            caixa.adicionarNota(e.getKey(), e.getValue());
        }

        // retirar troco do caixa (se houver)
        if (troco != null && !troco.isEmpty()) {
            caixa.retirarTroco(troco);
        }

        return new PurchaseResult(true, "Compra realizada", troco, trocoNecessario);
    }

    public String relatorio() {
        StringBuilder sb = new StringBuilder();
        sb.append("Relatório da Máquina:\n");
        for (EstoqueItem it : estoque) {
            sb.append(String.format("%s - preço: %s - qtd: %d - vendidos: %d\n",
                    it.getProduto().getNome(), util.Money.formatDouble(it.getProduto().getPreco()), it.getQuantidade(), it.getVendidos()));
        }
        sb.append(String.format("Caixa total: %s\n", util.Money.formatCents(caixa.getTotalCentavos())));
        return sb.toString();
    }

    public void iniciar() {
        System.out.println("Máquina de Refrigerante Iniciada");
    }
}
