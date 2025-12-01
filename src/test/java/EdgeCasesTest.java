import MaquinaRefri.Caixa;
import MaquinaRefri.MaquinaRefri;
import LadoClient.LadoClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class EdgeCasesTest {

    private MaquinaRefri maquina;
    private LadoClient cliente;

    @BeforeEach
    public void setup() {
        maquina = new MaquinaRefri();
        cliente = new LadoClient(maquina);
    }

    @Test
    public void compraQuandoProdutoEsgotado() {
        // esvaziar um produto manualmente
        // reduzir quantidade até 0
        int idx = 1; // Pepsi tem quantidade 8
        for (int i = 0; i < 8; i++) {
            maquina.adicionarDinheiroAoCaixa(Caixa.ONE_REAL, 10);
            maquina.adicionarDinheiroAoCaixa(Caixa.FIFTY_CENT, 10);
            var ins = LadoClient.inserirNotas(0,1,0,0,0); // R$5 para garantir
            String res = cliente.efetuarCompra(ins, idx);
            Assertions.assertTrue(res.contains("Compra bem sucedida"));
        }
        // agora a Pepsi deve estar esgotada
        var ins2 = LadoClient.inserirNotas(0,1,0,0,0);
        String res2 = cliente.efetuarCompra(ins2, idx);
        Assertions.assertTrue(res2.contains("Falha") && res2.contains("esgotado"));
    }

    @Test
    public void compraComTrocoInsuficienteParcial() {
        // garantir que caixa esteja vazio (sem troco suficiente)
        // tenta comprar com valor que exige troco
        var ins = LadoClient.inserirNotas(0,1,0,0,0); // R$5 para produto 0 que custa R$4.5
        // ensure no internal cash
        String res = cliente.efetuarCompra(ins, 0);
        Assertions.assertTrue(res.contains("Falha") && res.contains("Sem troco"));
    }

    @Test
    public void compraExataSemTroco() {
        // comprar com valor exato
        int idx = 2; // Guaraná R$3.50
        var ins = LadoClient.inserirNotas(0,0,1,1,1); // R$2 + R$1 + R$0.5 = R$3.5
        String res = cliente.efetuarCompra(ins, idx);
        Assertions.assertTrue(res.contains("Compra bem sucedida"));
        Assertions.assertFalse(res.contains("Troco"));
    }
}
