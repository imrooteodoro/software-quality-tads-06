import MaquinaRefri.Caixa;
import MaquinaRefri.MaquinaRefri;
import LadoClient.LadoClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class MaquinaRefriTest {

    private MaquinaRefri maquina;
    private LadoClient cliente;

    @BeforeEach
    public void setup() {
        maquina = new MaquinaRefri();
        cliente = new LadoClient(maquina);
    }

    @Test
    public void compraBemSucedidaComTroco() {
        // garantir que caixa tem troco suficiente
        maquina.adicionarDinheiroAoCaixa(Caixa.FIFTY_CENT, 4); // R$2.00
        maquina.adicionarDinheiroAoCaixa(Caixa.ONE_REAL, 2); // R$2.00 => total R$4.00

        // produto 0 (Coca) custa R$4.50
        Map<Integer, Integer> insercao = LadoClient.inserirNotas(0, 1, 0, 0, 0); // 1 nota de 5 => R$5.00
        String res = cliente.efetuarCompra(insercao, 0);
        Assertions.assertTrue(res.contains("Compra bem sucedida"));
        Assertions.assertTrue(res.contains("Troco"));
    }

    @Test
    public void compraFalhaSemTroco() {
        // esvaziar caixa
        // inicialmente caixa está vazio; tentar comprar que precisa de troco
        Map<Integer, Integer> insercao = LadoClient.inserirNotas(0, 1, 0, 0, 0); // R$5
        String res = cliente.efetuarCompra(insercao, 0);
        Assertions.assertTrue(res.contains("Falha"));
        Assertions.assertTrue(res.contains("Sem troco"));
    }

    @Test
    public void proprietarioAdicionaDinheiroEClienteCompra() {
        // proprietário adiciona troco
        maquina.adicionarDinheiroAoCaixa(Caixa.ONE_REAL, 10);
        maquina.adicionarDinheiroAoCaixa(Caixa.FIFTY_CENT, 10);

        Map<Integer, Integer> insercao = LadoClient.inserirNotas(0, 1, 0, 0, 0); // R$5
        String res = cliente.efetuarCompra(insercao, 0);
        Assertions.assertTrue(res.contains("Compra bem sucedida"));
    }

    @Test
    public void relatorioContemInformacoes() {
        maquina.adicionarDinheiroAoCaixa(Caixa.ONE_REAL, 5);
        String rel = maquina.relatorio();
        Assertions.assertTrue(rel.contains("Relatório"));
        Assertions.assertTrue(rel.contains("Caixa total"));
    }
}
