package MaquinaRefri;

public class EstoqueItem {
    private Produto produto;
    private int quantidade;
    private int vendidos;

    public EstoqueItem(Produto produto, int quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.vendidos = 0;
    }

    public Produto getProduto() {
        return produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void reduzirQuantidade() {
        if (quantidade > 0) {
            quantidade--;
            vendidos++;
        }
    }

    public int getVendidos() {
        return vendidos;
    }

    public void adicionar(int qtd) {
        if (qtd > 0) quantidade += qtd;
    }
}
