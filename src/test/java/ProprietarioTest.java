import MaquinaRefri.Caixa;
import MaquinaRefri.MaquinaRefri;
import MaquinaRefri.Produto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProprietarioTest {

    private MaquinaRefri maquina;

    @BeforeEach
    public void setup() {
        maquina = new MaquinaRefri();
    }

    @Test
    public void adicionarDinheiroAoCaixa() {
        int before = maquina.getCaixa().getTotalCentavos();
        maquina.adicionarDinheiroAoCaixa(Caixa.TEN_REAL, 1); // R$10
        maquina.adicionarDinheiroAoCaixa(Caixa.FIVE_REAL, 2); // R$10
        maquina.adicionarDinheiroAoCaixa(Caixa.TWO_REAL, 3); // R$6
        int after = maquina.getCaixa().getTotalCentavos();
        Assertions.assertEquals(before + 1000 + 2*500 + 3*200, after);
    }

    @Test
    public void adicionarEReporProduto() {
        Produto p = new Produto("Teste", 2.50);
        maquina.adicionarProduto(p, 5);
        boolean found = false;
        for (var it : maquina.getEstoque()) {
            if (it.getProduto().getNome().equalsIgnoreCase("Teste")) {
                found = true;
                Assertions.assertTrue(it.getQuantidade() >= 5);
            }
        }
        Assertions.assertTrue(found);
    }

    @Test
    public void relatorioRefleteVendasEQuantidade() {
        // configurar caixa para permitir troco
        maquina.adicionarDinheiroAoCaixa(Caixa.ONE_REAL, 10);
        maquina.adicionarDinheiroAoCaixa(Caixa.FIFTY_CENT, 10);

        // realizar uma compra via cliente
        var cliente = new LadoClient.LadoClient(maquina);
        var insercao = LadoClient.LadoClient.inserirNotas(0,1,0,0,0); // R$5
        String res = cliente.efetuarCompra(insercao, 0);
        Assertions.assertTrue(res.contains("Compra bem sucedida"));

        String rel = maquina.relatorio();
        Assertions.assertTrue(rel.contains("vendidos"));
    }
}
